package io.fiap.revenda.vendas.driven.service;

import io.fiap.revenda.vendas.driven.domain.ImmutablePagamento;
import io.fiap.revenda.vendas.driven.domain.ImmutableVenda;
import io.fiap.revenda.vendas.driven.domain.Pagamento;
import io.fiap.revenda.vendas.driven.domain.Venda;
import io.fiap.revenda.vendas.driven.repository.VendaRepository;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final PagamentoService pagamentoService;

    public VendaService(VendaRepository reservaRepository,
                        PagamentoService pagamentoService) {
        this.vendaRepository = reservaRepository;
        this.pagamentoService = pagamentoService;
    }

    public Mono<String> save(Venda venda) {
        return Mono.just(venda)
            .map(v -> ImmutableVenda.copyOf(v).withId(UUID.randomUUID().toString()))
            .flatMap(v -> gerarPagamento().apply(v))
            .flatMap(vendaRepository::save)
            .map(Venda::getPagamento)
            .map(Pagamento::getCodigo);
    }

    private Function<Venda, Mono<Venda>> gerarPagamento() {
        return venda -> {
            var pagamento = ImmutablePagamento.builder()
                .tipo("PIX")
                .valor(venda.getVeiculo().getValor())
                .codigo(UUID.randomUUID().toString())
                .expiracao(LocalDate.now().plusDays(3))
                .recebido(false)
                .id(UUID.randomUUID().toString())
                .build();

            return pagamentoService.save(pagamento)
                .map(p -> ImmutableVenda.copyOf(venda).withPagamento(p));
        };
    }

    public Flux<Venda> fetch() {
        return vendaRepository.fetch()
            .flatMap(venda -> pagamentoService.fetchByCodigo(venda.codigoPagamento())
                .map(pagamento -> ImmutableVenda.copyOf(venda).withPagamento(pagamento)));
    }
}
