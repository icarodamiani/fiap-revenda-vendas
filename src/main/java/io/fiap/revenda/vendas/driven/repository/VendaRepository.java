package io.fiap.revenda.vendas.driven.repository;

import io.fiap.revenda.vendas.driven.domain.ImmutableDocumento;
import io.fiap.revenda.vendas.driven.domain.ImmutablePagamento;
import io.fiap.revenda.vendas.driven.domain.ImmutablePessoa;
import io.fiap.revenda.vendas.driven.domain.ImmutableVeiculo;
import io.fiap.revenda.vendas.driven.domain.ImmutableVenda;
import io.fiap.revenda.vendas.driven.domain.Pessoa;
import io.fiap.revenda.vendas.driven.domain.Veiculo;
import io.fiap.revenda.vendas.driven.domain.Venda;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Repository
public class VendaRepository {
    private static final String TABLE_NAME = "vendas_tb";

    private final DynamoDbAsyncClient client;

    public VendaRepository(DynamoDbAsyncClient client) {
        this.client = client;
    }

    public Mono<Venda> save(Venda venda) {
        var atributos = new HashMap<String, AttributeValueUpdate>();

        atributos.put(
            "PESSOA",
            AttributeValueUpdate.builder().value(v ->
                v.m(buildPessoaAtributos().apply(venda.getPessoa())).build()
            ).build()
        );

        atributos.put(
            "VEICULO",
            AttributeValueUpdate.builder().value(v ->
                v.m(buildVeiculoAtributos().apply(venda.getVeiculo())).build()
            ).build()
        );

        atributos.put("CODIGO_PAGAMENTO", AttributeValueUpdate.builder().value(v ->
                v.s(venda.getPagamento().getCodigo()).build()
            ).build()
        );

        var request = UpdateItemRequest.builder()
            .attributeUpdates(atributos)
            .tableName(TABLE_NAME)
            .key(Map.of("ID", AttributeValue.fromS(venda.getId())))
            .build();

        return Mono.fromFuture(client.updateItem(request))
            .then(Mono.just(venda));
    }

    private Function<Pessoa, Map<String, AttributeValue>> buildPessoaAtributos() {
        return pessoa -> {
            var pessoaAttr = new HashMap<String, AttributeValue>();
            pessoaAttr.put("ID", AttributeValue.builder().s(pessoa.getId()).build());
            pessoaAttr.put("NOME", AttributeValue.builder().s(pessoa.getNome()).build());
            pessoaAttr.put("SOBRENOME", AttributeValue.builder().s(pessoa.getSobrenome()).build());

            var documentoAttr = new HashMap<String, AttributeValue>();
            documentoAttr.put("TIPO", AttributeValue.builder().s(pessoa.getDocumento().getTipo()).build());
            documentoAttr.put("VALOR", AttributeValue.builder().s(pessoa.getDocumento().getValor()).build());

            pessoaAttr.put("DOCUMENTO", AttributeValue.builder().m(documentoAttr).build());

            return pessoaAttr;
        };
    }

    private Function<Veiculo, Map<String, AttributeValue>> buildVeiculoAtributos() {
        return veiculo -> {
            var veiculoAttr = new HashMap<String, AttributeValue>();
            veiculoAttr.put("ID", AttributeValue.builder().s(veiculo.getId()).build());
            veiculoAttr.put("COR", AttributeValue.builder().s(veiculo.getCor()).build());
            veiculoAttr.put("ANO", AttributeValue.builder().s(veiculo.getAno()).build());
            veiculoAttr.put("MARCA", AttributeValue.builder().s(veiculo.getMarca()).build());
            veiculoAttr.put("PLACA", AttributeValue.builder().s(veiculo.getPlaca()).build());
            veiculoAttr.put("VALOR", AttributeValue.builder().s(veiculo.getValor()).build());
            veiculoAttr.put("CAMBIO", AttributeValue.builder().s(veiculo.getCambio()).build());
            veiculoAttr.put("RENAVAM", AttributeValue.builder().s(veiculo.getRenavam()).build());
            veiculoAttr.put("MODELO", AttributeValue.builder().s(veiculo.getModelo()).build());
            veiculoAttr.put("MOTORIZACAO", AttributeValue.builder().s(veiculo.getMotorizacao()).build());
            veiculoAttr.put("QUILOMETRAGEM", AttributeValue.builder().s(veiculo.getQuilometragem()).build());

            return veiculoAttr;
        };
    }

    public Mono<Void> deleteById(String id) {
        var key = new HashMap<String, AttributeValue>();
        key.put("ID", AttributeValue.fromS(id));

        var request = DeleteItemRequest.builder()
            .key(key)
            .tableName(TABLE_NAME)
            .build();

        return Mono.fromFuture(client.deleteItem(request))
            .then();
    }

    public Flux<Venda> fetch() {
        return Mono.fromFuture(client.scan(ScanRequest.builder().tableName(TABLE_NAME).build()))
            .filter(ScanResponse::hasItems)
            .map(response -> response.items()
                .stream()
                .map(this::convertItem)
                .toList()
            )
            .flatMapIterable(l -> l);
    }

    public Mono<Venda> findById(String id) {
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

    private Venda convertItem(Map<String, AttributeValue> item) {
        return ImmutableVenda.builder()
            .id(item.get("ID").s())
            .codigoPagamento(item.get("CODIGO_PAGAMENTO").s())
            .veiculo(ImmutableVeiculo.builder()
                .id(item.get("VEICULO").m().get("ID").s())
                .ano(item.get("VEICULO").m().get("ANO").s())
                .cor(item.get("VEICULO").m().get("COR").s())
                .valor(item.get("VEICULO").m().get("VALOR").s())
                .marca(item.get("VEICULO").m().get("MARCA").s())
                .quilometragem(item.get("VEICULO").m().get("QUILOMETRAGEM").s())
                .placa(item.get("VEICULO").m().get("PLACA").s())
                .renavam(item.get("VEICULO").m().get("RENAVAM").s())
                .cambio(item.get("VEICULO").m().get("CAMBIO").s())
                .modelo(item.get("VEICULO").m().get("MODELO").s())
                .motorizacao(item.get("VEICULO").m().get("MOTORIZACAO").s())
                .build()
            )
            .pessoa(ImmutablePessoa.builder()
                .id(item.get("PESSOA").m().get("ID").s())
                .documento(ImmutableDocumento.builder()
                    .tipo(item.get("PESSOA").m().get("DOCUMENTO").m().get("TIPO").s())
                    .valor(item.get("PESSOA").m().get("DOCUMENTO").m().get("VALOR").s()).build())
                .nome(item.get("PESSOA").m().get("NOME").s())
                .sobrenome(item.get("PESSOA").m().get("SOBRENOME").s())
                .build()
            )
            .build();
    }
}
