package no.dnb.r2dbc.test.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.dnb.r2dbc.test.demo.dao.DemoDao;
import no.dnb.r2dbc.test.demo.model.CorpCustomer;
import no.dnb.r2dbc.test.demo.model.SchemaVersion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DemoService {

    private final DemoDao demoDao;

    public Flux<CorpCustomer> getAllUsers(Integer offset, Integer pageSize) {
        if (offset == null && pageSize == null) {
            return demoDao.getAllUsers();
        } else {
            return demoDao.getAllUsersWithProcedure(
                    Optional.ofNullable(offset).orElse(0),
                    Optional.ofNullable(pageSize).orElse(500)
            );
        }
    }

    public Mono<Long> createSomeUsers(Integer count, String type) {
        if ("BATCH".equalsIgnoreCase(type)) {
            log.info("Created " + count + " users with batch mode");
            return demoDao.createSomeUsersBatch(count);
        } else if ("PROCEDURE".equalsIgnoreCase(type)) {
            log.info("Created " + count + " users with a stored procedure");
            return demoDao.createSomeUsersWithProcedure(count);
        } else {
            log.info("Created " + count + " users with a bunch of insert statements");
            return demoDao.createSomeUsers(count);
        }
    }

    public Flux<SchemaVersion> flywayChanges() {
        return demoDao.flywayChanges();
    }
}
