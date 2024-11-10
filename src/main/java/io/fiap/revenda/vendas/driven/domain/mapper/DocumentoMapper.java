package io.fiap.revenda.vendas.driven.domain.mapper;

import io.fiap.revenda.vendas.driven.domain.Documento;
import io.fiap.revenda.vendas.driver.controller.dto.DocumentoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DocumentoMapper.class})
public interface DocumentoMapper extends BaseMapper<DocumentoDTO, Documento> {
}
