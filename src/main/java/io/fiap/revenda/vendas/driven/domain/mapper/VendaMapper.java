package io.fiap.revenda.vendas.driven.domain.mapper;

import io.fiap.revenda.vendas.driven.domain.Venda;
import io.fiap.revenda.vendas.driver.controller.dto.VendaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {PessoaMapper.class, PagamentoMapper.class, VeiculoMapper.class},
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface VendaMapper extends BaseMapper<VendaDTO, Venda> {
}
