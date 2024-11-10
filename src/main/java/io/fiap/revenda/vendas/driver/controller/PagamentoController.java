package io.fiap.revenda.vendas.driver.controller;

import io.fiap.revenda.vendas.driven.domain.mapper.PagamentoMapper;
import io.fiap.revenda.vendas.driven.service.PagamentoService;
import io.fiap.revenda.vendas.driver.controller.dto.PagamentoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SecurityRequirement(name = "OAuth2")
@RestController
@RequestMapping(value = "/vendas", produces = MediaType.APPLICATION_JSON_VALUE)
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PagamentoMapper pagamentoMapper;

    public PagamentoController(PagamentoService pagamentoService, PagamentoMapper pagamentoMapper) {
        this.pagamentoService = pagamentoService;
        this.pagamentoMapper = pagamentoMapper;
    }

    @PatchMapping("/{vendaId}/pagamentos/{id}/receber")
    @Operation(description = "Atualiza um pagamento existente")
    public Mono<Void> receber(@PathVariable String vendaId, @PathVariable String id) {
        return pagamentoService.receber(vendaId,  id);
    }

    @GetMapping("/pagamentos/{codigo}")
    @Operation(description = "Busca um pagamento por seu código")
    public Flux<PagamentoDTO> fetch(@PathVariable String codigo) {
        return pagamentoService.fetchByCodigo(codigo)
            .map(pagamentoMapper::dtoFromDomain);
    }
}
