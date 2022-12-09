package no.dnb.r2dbc.test.demo.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.dnb.r2dbc.test.demo.model.CorpCustomer;
import no.dnb.r2dbc.test.demo.model.SchemaVersion;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.ConnectionFactoryUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DemoDao {

    private final R2dbcEntityTemplate template;

    private final ObjectMapper objectMapper;

    public Flux<SchemaVersion> flywayChanges() {
        return template.select(SchemaVersion.class)
                .from("master.dbo.flyway_schema_history")
                .all();
    }

    public Flux<CorpCustomer> getAllUsers() {
        return template.select(CorpCustomer.class)
                .from("master.dbo.corp_customer")
                .all()
                .doOnSubscribe(subscription -> log.info("Starting query"));
    }

    public Flux<CorpCustomer> getAllUsersWithProcedure(int offset, int pageSize) {
        return template.getDatabaseClient()
                .sql("EXECUTE master.dbo.getAllUsers @offset, @pageSize")
                .bind("@offset", offset)
                .bind("@pageSize", pageSize)
                .map(template.getDataAccessStrategy().getRowMapper(CorpCustomer.class))
                .all();
    }

    public Mono<Long> createSomeUsers(Integer count) {
        int base = new Random().nextInt();
        var corpCustomerFlux = Flux.fromStream(IntStream.range(0, count).boxed())
                .map(i -> CorpCustomer.builder()
                        .id(base + i)
                        .userId("SingleUser"+i)
                        .build());
        return corpCustomerFlux
                .flatMap(corpCustomer -> template.insert(CorpCustomer.class)
                        .into("master.dbo.corp_customer")
                        .using(corpCustomer))
                .count()
                .checkpoint("Creating " + count + " users with multiple inserts");
    }

    public Mono<Long> createSomeUsersBatch(Integer count) {
        int base = new Random().nextInt();
        var connection = ConnectionFactoryUtils.getConnection(template.getDatabaseClient().getConnectionFactory());
        var userFlux = Flux.fromStream(IntStream.range(0, count).boxed())
                .map(i -> CorpCustomer.builder()
                .id(base + i)
                .userId("BatchUser"+i)
                .build());

        // This has an SQL injection problem since batch is just a String currently and cannot bind variables
        return connection
                .map(Connection::createBatch)
                .flatMap(batch -> userFlux
                        .map(corpCustomer -> "INSERT INTO master.dbo.corp_customer " +
                                "VALUES (" + corpCustomer.getId() + ", '"+ corpCustomer.getUserId() +"')")
                        .map(batch::add)
                        .last())
                .map(Batch::execute)
                .flatMapMany(Flux::from)
                .flatMap(Result::getRowsUpdated)
                .reduce(Long::sum)
                .checkpoint("Creating " + count + " users with batch inserts");
    }

    public Mono<Long> createSomeUsersWithProcedure(Integer count) {
        int base = new Random().nextInt();
        var userList = IntStream.range(0, count).boxed()
                .map(i -> CorpCustomer.builder()
                        .id(base + i)
                        .userId("StoredProc" + i)
                        .build())
                .toList();

        var json = convertToJson(userList);
        log.info("Converted JSON: {}", json);
        return template.getDatabaseClient()
                .sql("EXECUTE master.dbo.insertManyUsers @json")
                .bind("@json", json)
                .flatMap(Result::getRowsUpdated) // This doesn't seem to give the right response as the inserts happen in the stored proc
                .reduce(Long::sum)
                .checkpoint("Creating " + count + " users with stored procedure");
    }

    private String convertToJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
