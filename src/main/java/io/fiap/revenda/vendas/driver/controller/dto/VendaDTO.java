package io.fiap.revenda.vendas.driver.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableVendaDTO.class)
@JsonDeserialize(as = ImmutableVendaDTO.class)
@Value.Immutable
@Value.Style(privateNoargConstructor = true, jdkOnly = true)
public abstract class VendaDTO {
    @Nullable
    public abstract String getId();
    @Nullable
    public abstract PagamentoDTO getPagamento();
    public abstract VeiculoDTO getVeiculo();
    public abstract PessoaDTO getPessoa();
}