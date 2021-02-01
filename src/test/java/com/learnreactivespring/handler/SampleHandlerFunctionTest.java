package com.learnreactivespring.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
class SampleHandlerFunctionTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get()
                                                 .uri("/functional/flux")
                                                 .accept(MediaType.APPLICATION_JSON)
                                                 .exchange() // this is where the call is actually made
                                                 .expectStatus().isOk()
                                                 .returnResult(Integer.class).getResponseBody();

        StepVerifier.create(integerFlux)
                    .expectSubscription()
                    .expectNext(1, 2, 3, 4)
                    .verifyComplete();
    }

    @Test
    public void mono() {

        Integer expectedValue = new Integer(1);

        webTestClient.get()
                     .uri("/functional/mono")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(Integer.class)
                     .consumeWith((response) -> {
                         assertEquals(expectedValue, response.getResponseBody());
                     });
    }

}