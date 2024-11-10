package io.fiap.revenda.vendas.driver.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableDocumentoDTO.class)
@JsonDeserialize(as = ImmutableDocumentoDTO.class)
@Value.Immutable
@Value.Style(privateNoargConstructor = true, jdkOnly = true)
public abstract class DocumentoDTO {
    public abstract String getTipo();
    public abstract String getValor();
}