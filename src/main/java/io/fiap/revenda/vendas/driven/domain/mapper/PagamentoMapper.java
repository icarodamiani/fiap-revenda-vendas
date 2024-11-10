package io.fiap.revenda.vendas.driven.domain.mapper;

import io.fiap.revenda.vendas.driven.domain.Pagamento;
import io.fiap.revenda.vendas.driver.controller.dto.PagamentoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface PagamentoMapper extends BaseMapper<PagamentoDTO, Pagamento> {
}
