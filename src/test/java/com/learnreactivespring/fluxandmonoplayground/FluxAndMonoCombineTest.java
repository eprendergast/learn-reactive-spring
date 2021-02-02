package com.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        // DOES NOT MAINTAIN ORDER
        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        // THIS WILL FAIL
        StepVerifier.create(mergedFlux.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C", "D", "E", "F")
                    .verifyComplete();
    }

    @Test
    public void combineUsingConcat_withDelay() {

        VirtualTimeScheduler.getOrSet(); // enables virtual time for this test case

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> stringFlux = Flux.concat(flux1, flux2);

        StepVerifier.withVirtualTime(() -> stringFlux.log())
                    .expectSubscription()
                    .thenAwait(Duration.ofSeconds(6)) // if you do not make this call then Virtual Time will not work
                    .expectNextCount(6)
                    .verifyComplete();

        // THIS WILL PASS
        //        StepVerifier.create(stringFlux.log())
        //                    .expectSubscription()
        //                    .expectNext("A", "B", "C", "D", "E", "F")
        //                    .verifyComplete();
    }

    @Test
    public void combineUsingZip() {

        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> stringFlux = Flux.zip(flux1, flux2, (t1, t2) -> {
            return t1.concat(t2);
        });

        StepVerifier.withVirtualTime(() -> stringFlux.log())
                    .expectSubscription()
                    .thenAwait(Duration.ofSeconds(3))
                    .expectNext("AD", "BE", "CF")
                    .verifyComplete();
    }

}
