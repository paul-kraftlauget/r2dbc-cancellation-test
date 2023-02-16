package no.dnb.r2dbc.test.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.dnb.r2dbc.test.demo.service.DemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
@Slf4j
public class DemoControllerV1 {

    private final DemoService demoService;

    @GetMapping(value="/schema-versions", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Mono<ResponseEntity<String>> getFlyway() {
        return demoService.flywayChanges()
                .collectList()
                .map(Objects::toString)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK));
    }

    @GetMapping(value="/corporate-users", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Mono<ResponseEntity<String>> getCorporateUsers(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return demoService.getAllUsers(offset, pageSize)
                .collectList()
                .doOnNext(list -> log.info("Found {} corp users", list.size()))
                .map(Objects::toString)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .doOnCancel(() -> log.info("Cancelled query"));
    }

    @GetMapping(value="/create-corporate-users/{count}/{type}")
    public Mono<ResponseEntity<Long>> createSomeUsers(@PathVariable("count") Integer count,
                                                      @PathVariable(value = "type") String type) {
        long start = System.currentTimeMillis();
        return demoService.createSomeUsers(count, type)
                .map(item -> new ResponseEntity<>(item, HttpStatus.CREATED))
                .doOnCancel(() -> log.info("Cancelled query"))
                .doFinally(signalType -> log.info("Done "+count + " " + type + " ("+signalType+"): " + (System.currentTimeMillis() - start) + " millis"));
    }


}
