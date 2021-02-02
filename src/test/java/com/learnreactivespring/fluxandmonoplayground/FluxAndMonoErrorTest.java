package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                                      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                      .concatWith(Flux.just("D"))
                                      .onErrorResume((e) -> { // this block gets executed
                                          System.out.println("Exception is " + e);
                                          return Flux.just("Default", "Default1");
                                      });

        StepVerifier.create(stringFlux.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    //                    .expectError(RuntimeException.class)
                    //                    .verify();
                    .expectNext("Default", "Default1")
                    .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_onErrorReturn() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                                      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                      .concatWith(Flux.just("D"))
                                      .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    //                    .expectError(RuntimeException.class)
                    //                    .verify();
                    .expectNext("default")
                    .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetry() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                                      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                      .concatWith(Flux.just("D"))
                                      .retry(2);

        StepVerifier.create(stringFlux.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectError(RuntimeException.class)
                    .verify();
    }

}
