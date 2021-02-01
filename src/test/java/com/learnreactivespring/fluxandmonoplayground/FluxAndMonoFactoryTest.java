package com.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

    @Test
    public void fluxUsingIterable() {

        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux) // acts as subscriber
                    .expectNext("Adam", "Anna", "Jack", "Jenny")
                    .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {

        String[] names = new String[] {"Adam", "Anna", "Jack", "Jenny"};

        Flux<String> namesFlux = Flux.fromArray(names);

        StepVerifier.create(namesFlux) // acts as subscriber
                    .expectNext("Adam", "Anna", "Jack", "Jenny")
                    .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {

        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux) // acts as subscriber
                    .expectNext("Adam", "Anna", "Jack", "Jenny")
                    .verifyComplete(); // stream will start emitting the data once this is called
    }

    @Test
    public void monoUsingJustOrEmpty() {

        Mono<String> mono = Mono.justOrEmpty(null);// Mono.Empty()

        StepVerifier.create(mono.log())
                    .verifyComplete();

    }

    @Test
    public void monoUsingSupplier() {

        Supplier<String> stringSupplier = () -> "Anna";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        System.out.println(stringSupplier.get());

        StepVerifier.create(stringMono.log())
                    .expectNext("Anna")
                    .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {

        Flux<Integer> integerFlux = Flux.range(1, 5);

        StepVerifier.create(integerFlux.log())
                    .expectNext(1, 2, 3, 4, 5)
                    .verifyComplete();
    }

}
