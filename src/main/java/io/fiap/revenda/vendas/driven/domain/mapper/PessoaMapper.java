package io.fiap.revenda.vendas.driven.domain.mapper;

import io.fiap.revenda.vendas.driven.domain.Pessoa;
import io.fiap.revenda.vendas.driver.controller.dto.PessoaDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PessoaMapper extends BaseMapper<PessoaDTO, Pessoa> {
}
