package io.fiap.revenda.vendas.driven.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fiap.revenda.vendas.driven.client.dto.ImmutableDocumentoEmitirRequestDTO;
import io.fiap.revenda.vendas.driven.domain.ImmutablePagamento;
import io.fiap.revenda.vendas.driven.domain.Pagamento;
import io.fiap.revenda.vendas.driven.exception.BusinessException;
import io.fiap.revenda.vendas.driven.port.MessagingPort;
import io.fiap.revenda.vendas.driven.repository.PagamentoRepository;
import io.fiap.revenda.vendas.driven.repository.VendaRepository;
import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PagamentoService {
    private final VendaRepository vendaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final MessagingPort messagingPort;
    private final ObjectMapper objectMapper;
    private final String queue;

    public PagamentoService(VendaRepository vendaRepository,
                            PagamentoRepository pagamentoRepository,
                            MessagingPort messagingPort, ObjectMapper objectMapper,
                            @Value("${aws.sqs.documentosEmitir.queue}")
                            String queue) {
        this.vendaRepository = vendaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.messagingPort = messagingPort;
        this.objectMapper = objectMapper;
        this.queue = queue;
    }

    public Mono<Pagamento> save(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    public Flux<Pagamento> fetchByCodigo(String codigo) {
        return pagamentoRepository.fetchByCodigo(codigo);
    }

    public Mono<Void> receber(String vendaId, String id) {
        return pagamentoRepository.fetchById(id)
            .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException("Pagamento não encontrado. Id[" + id + "]"))))
            .flatMap(pagamento ->
                vendaRepository.findById(vendaId)
                    .filter(venda -> venda.codigoPagamento().equals(pagamento.getCodigo()))
                    .switchIfEmpty(Mono.defer(() ->
                            Mono.error(
                                new BusinessException("Código de pagamento não bate com o códgio registrado na venda. " +
                                    "Id da Venda[" + vendaId + "] Id do Pagamento[" + id + "]"))
                        )
                    )
                    .map(venda -> pagamento)
            )
            .map(pagamento -> recebido().apply(pagamento))
            .flatMap(pagamentoRepository::save)
            .flatMap(pagamento -> this.emitirDocumento(vendaId));
    }

    private Function1<Pagamento, Pagamento> recebido() {
        return pagamento -> ImmutablePagamento.copyOf(pagamento).withRecebimento(LocalDate.now()).withRecebido(true);
    }

    public Mono<Void> emitirDocumento(String vendaId) {
        return vendaRepository.findById(vendaId)
            .map(venda -> ImmutableDocumentoEmitirRequestDTO.builder()
                .id(UUID.randomUUID().toString())
                .pessoa(venda.getPessoa())
                .veiculo(venda.getVeiculo())
                .orgao("DETRAN")
                .tipo("CRV")
                .build())
            .flatMap(request -> messagingPort.send(queue, request, serializePayload()))
            .then();
    }

    private <T> CheckedFunction1<T, String> serializePayload() {
        return objectMapper::writeValueAsString;
    }
}
