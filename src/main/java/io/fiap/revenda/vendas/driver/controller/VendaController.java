package io.fiap.revenda.vendas.driver.controller;

import io.fiap.revenda.vendas.driven.domain.mapper.VendaMapper;
import io.fiap.revenda.vendas.driven.service.VendaService;
import io.fiap.revenda.vendas.driver.controller.dto.VendaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SecurityRequirement(name = "OAuth2")
@RestController
@RequestMapping(value = "/vendas", produces = MediaType.APPLICATION_JSON_VALUE)
public class VendaController {

    private final VendaService vendaService;
    private final VendaMapper vendaMapper;

    public VendaController(VendaService vendaService, VendaMapper vendaMapper) {
        this.vendaService = vendaService;
        this.vendaMapper = vendaMapper;
    }

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok", content =
            {@Content(mediaType = "application/json", schema =
            @Schema(implementation = String.class, description = "Código de Pagamento"))})
    })
    @Operation(description = "Cria uma nova venda. Modo de pagamento padrão, PIX.")
    public Mono<String> save(@RequestBody VendaDTO venda) {
        return Mono.fromSupplier(() -> vendaMapper.domainFromDto(venda))
            .flatMap(vendaService::save);
    }

    @GetMapping
    @Operation(description = "Busca todas as vendas")
    public Flux<VendaDTO> fetch() {
        return vendaService.fetch()
            .map(vendaMapper::dtoFromDomain);
    }
}
