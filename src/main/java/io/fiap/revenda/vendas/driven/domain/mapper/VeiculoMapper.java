package io.fiap.revenda.vendas.driven.domain.mapper;

import io.fiap.revenda.vendas.driven.domain.Veiculo;
import io.fiap.revenda.vendas.driver.controller.dto.VeiculoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VeiculoMapper extends BaseMapper<VeiculoDTO, Veiculo> {
}
