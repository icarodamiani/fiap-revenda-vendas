package io.fiap.revenda.vendas.driver.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutablePagamentoDTO.class)
@JsonDeserialize(as = ImmutablePagamentoDTO.class)
@Value.Immutable
@Value.Style(privateNoargConstructor = true, jdkOnly = true)
public abstract class PagamentoDTO {
    @Nullable
    public abstract String getId();
    public abstract String getTipo();
    public abstract String getValor();
    public abstract String getCodigo();
    public abstract LocalDate getExpiracao();
    @Nullable
    public abstract Boolean getRecebido();
    @Nullable
    public abstract LocalDate getRecebimento();
}