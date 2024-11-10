package io.fiap.revenda.vendas.driven.repository;

import io.fiap.revenda.vendas.driven.domain.ImmutablePagamento;
import io.fiap.revenda.vendas.driven.domain.Pagamento;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Repository
public class PagamentoRepository {
    private static final String TABLE_NAME = "pagamentos_tb";

    private final DynamoDbAsyncClient client;

    public PagamentoRepository(DynamoDbAsyncClient client) {
        this.client = client;
    }

    public Mono<Pagamento> save(Pagamento pagamento) {
        var atributos = new HashMap<String, AttributeValueUpdate>();
        atributos.put("CODIGO",
            AttributeValueUpdate.builder().value(builder -> builder.s(pagamento.getCodigo()).build()).build());
        atributos.put("TIPO",
            AttributeValueUpdate.builder().value(builder -> builder.s(pagamento.getTipo()).build()).build());
        atributos.put("VALOR",
            AttributeValueUpdate.builder().value(builder -> builder.s(pagamento.getValor()).build()).build());
        atributos.put("EXPIRACAO",
            AttributeValueUpdate.builder().value(builder -> builder.s(
                String.valueOf(Objects.requireNonNull(pagamento.getExpiracao()).toEpochDay())).build()
            ).build());
        atributos.put("RECEBIDO",
            AttributeValueUpdate.builder().value(builder -> builder.s(
                pagamento.getRecebido() != null ? pagamento.getRecebido().toString() : Boolean.FALSE.toString()).build()
            ).build());

        if (Boolean.TRUE.equals(pagamento.getRecebido()) && pagamento.getRecebimento() != null) {
            atributos.put("RECEBIMENTO",
                AttributeValueUpdate.builder().value(builder -> builder.s(
                    String.valueOf(Objects.requireNonNull(pagamento.getRecebimento()).toEpochDay())).build()
                ).build());
        }

        var request = UpdateItemRequest.builder()
            .attributeUpdates(atributos)
            .tableName(TABLE_NAME)
            .key(Map.of("ID", AttributeValue.fromS(pagamento.getId())))
            .build();

        return Mono.fromFuture(client.updateItem(request))
            .then(Mono.just(pagamento));
    }

    public Flux<Pagamento> fetchByCodigo(String codigo) {
        var request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .indexName("CodigoIndex")
            .keyConditionExpression("#codigo = :codigo")
            .expressionAttributeNames(Map.of("#codigo", "CODIGO"))
            .expressionAttributeValues(Map.of(":codigo", AttributeValue.fromS(codigo)))
            .build();

        return Mono.fromFuture(client.query(request))
            .filter(QueryResponse::hasItems)
            .map(response -> response.items()
                .stream()
                .map(this::convertItem)
                .toList()
            )
            .flatMapIterable(l -> l);
    }

    public Mono<Pagamento> fetchById(String id) {
        var request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .keyConditionExpression("#id = :id")
            .expressionAttributeNames(Map.of("#id", "ID"))
            .expressionAttributeValues(Map.of(":id", AttributeValue.fromS(id)))
            .build();

        return Mono.fromFuture(client.query(request))
            .filter(QueryResponse::hasItems)
            .map(response -> response.items().get(0))
            .map(this::convertItem);
    }

    private Pagamento convertItem(Map<String, AttributeValue> item) {
        return ImmutablePagamento.builder()
            .id(item.get("ID").s())
            .tipo(item.get("TIPO").s())
            .valor(item.get("VALOR").s())
            .codigo(item.get("CODIGO").s())
            .expiracao(LocalDate.ofEpochDay(Long.parseLong(item.get("EXPIRACAO").s())))
            .recebido(Boolean.valueOf(item.get("RECEBIDO").s()))
            .build();
    }
}
