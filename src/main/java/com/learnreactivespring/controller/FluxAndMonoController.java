package com.learnreactivespring.controller;

import java.time.Duration;

import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FluxAndMonoController {

    // By default, browsers are blocking clients
    @GetMapping("/flux") // Browser is the subscriber
    public Flux<Integer> returnFlux() {
        return Flux.just(1, 2, 3, 4)
//                   .delayElements(Duration.ofSeconds(1))
                   .log();
    }

    // Force browser to return stream
    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnFluxStrem() {
        return Flux.interval(Duration.ofSeconds(1)) // infinite publisher
                   .log();
    }

    // Force browser to return stream
    @GetMapping(value = "/mono", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<Integer> returnMono() {
        return Mono.just(1).log();
    }

}

