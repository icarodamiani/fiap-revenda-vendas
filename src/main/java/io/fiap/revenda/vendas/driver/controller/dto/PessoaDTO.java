package io.fiap.revenda.vendas.driver.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutablePessoaDTO.class)
@JsonDeserialize(as = ImmutablePessoaDTO.class)
@Value.Immutable
@Value.Style(privateNoargConstructor = true, jdkOnly = true)
public abstract class PessoaDTO {
    public abstract String getId();
    public abstract String getNome();
    public abstract String getSobrenome();
    public abstract DocumentoDTO getDocumento();
}