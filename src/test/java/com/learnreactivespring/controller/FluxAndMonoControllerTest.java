package com.learnreactivespring.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest // will only scan packages which have @Controller, @RestController, etc. Doesn't scan @Component, @Repository etc
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient; // @WebFLuxTest annotation creates the instance here

    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get()
                                                 .uri("/flux")
                                                 .accept(MediaType.APPLICATION_JSON)
                                                 .exchange() // this is where the call is actually made
                                                 .expectStatus().isOk()
                                                 .returnResult(Integer.class)
                                                 .getResponseBody();

        StepVerifier.create(integerFlux)
                    .expectSubscription()
                    .expectNext(1, 2, 3, 4)
                    .verifyComplete();
    }

    @Test
    public void flux_approach2() {
        webTestClient.get()
                     .uri("/flux")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Integer.class).hasSize(4);
    }

    @Test
    public void flux_approach3() {

        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get()
                                                                                .uri("/flux")
                                                                                .accept(MediaType.APPLICATION_JSON)
                                                                                .exchange()
                                                                                .expectStatus().isOk()
                                                                                .expectBodyList(Integer.class)
                                                                                .returnResult();

        assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
    }

    @Test
    public void flux_approach4() {

        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);

        webTestClient.get()
                     .uri("/flux")
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBodyList(Integer.class)
                     .consumeWith((response) -> {
                         assertEquals(expectedIntegerList, response.getResponseBody());
                     });
    }

    @Test
    public void fluxStream() {

        Flux<Long> longStreamFlux = webTestClient.get()
                                                 .uri("/fluxstream")
                                                 .accept(MediaType.APPLICATION_STREAM_JSON)
                                                 .exchange() // this is where the call is actually made
                                                 .expectStatus().isOk()
                                                 .returnResult(Long.class).getResponseBody();

        StepVerifier.create(longStreamFlux)
                    .expectNext(0L, 1L, 2L)
                    .thenCancel()
                    .verify();
    }

    @Test
    public void mono() {

        Integer expectedValue = new Integer(1);

        webTestClient.get()
                     .uri("/mono")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(Integer.class)
                     .consumeWith((response) -> {
            assertEquals(expectedValue, response.getResponseBody());
        });
    }

}