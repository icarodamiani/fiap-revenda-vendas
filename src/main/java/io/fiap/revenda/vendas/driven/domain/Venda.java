package io.fiap.revenda.vendas.driven.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableVenda.class)
@JsonDeserialize(as = ImmutableVenda.class)
@Value.Immutable
@Value.Style(privateNoargConstructor = true, jdkOnly = true)
public abstract class Venda {
    @Nullable
    public abstract String getId();
    @Nullable
    public abstract String codigoPagamento();
    @Nullable
    public abstract Pagamento getPagamento();
    public abstract Veiculo getVeiculo();
    public abstract Pessoa getPessoa();
}