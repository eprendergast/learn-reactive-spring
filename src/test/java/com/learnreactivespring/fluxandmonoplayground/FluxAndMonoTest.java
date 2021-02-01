package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                                      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                      .concatWith(Flux.just("After Error")) // This will ony execute when the above line is commented out
                                      .log();

        // Attach a subscriber that reads all the values from the Flux
        stringFlux.subscribe(System.out::println,
                             (e) -> System.err.println("Exception is " + e),
                             () -> System.out.println("Completed"));

    }

    @Test
    public void fluxTestElements_withoutError() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                                      .log();

        StepVerifier.create(stringFlux)
                    .expectNext("Spring")
                    .expectNext("Spring Boot")
                    .expectNext("Reactive Spring")
                    .verifyComplete(); // If we do not add this, the events will not flow from the Flux
    }

    @Test
    public void fluxTestElements_withError() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                                      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                      .log();

        StepVerifier.create(stringFlux)
                    .expectNext("Spring")
                    .expectNext("Spring Boot")
                    .expectNext("Reactive Spring")
                    //                    .expectErrorMessage("Exception Occurred");
                    .verifyError(RuntimeException.class);
    }

    @Test
    public void fluxTestElementsCount_withError() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                                      .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                      .log();

        StepVerifier.create(stringFlux)
                    .expectNextCount(3)
                    .verifyError(RuntimeException.class);
    }

    @Test
    public void fluxTestElements_withError1() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                                      .log();

        StepVerifier.create(stringFlux)
                    .expectNext("Spring", "Spring Boot", "Reactive Spring")
                    .verifyComplete();
    }

    @Test
    public void monoTest() {

        Mono<String> stringMono = Mono.just("Spring").log();

        StepVerifier.create(stringMono)
                    .expectNext("Spring")
                    .verifyComplete();

    }

    @Test
    public void monoTest_withError() {

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred"))
                                .log())
                    .expectError(RuntimeException.class)
                    .verify(); // if this is ommitted, Mono will not emit the elements to the subscriber

    }

}
