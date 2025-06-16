package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux_map() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map();
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_mapFilter() {
        int strLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(strLength);
        StepVerifier.create(namesFlux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();
        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        int strLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(strLength);
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        int strLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(strLength);
        StepVerifier.create(namesFlux)
                //.expectNext("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {
        int strLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(strLength);
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                //   .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_flatmap() {
        int strLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatmap(strLength);
        StepVerifier.create(namesFlux)
                .expectNext(List.of("A", "L", "E", "X"))
                //   .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_1() {
        int strLength = 6;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(strLength);
        StepVerifier.create(namesFlux)
                //.expectNext(List.of("A", "L", "E", "X"))
                //   .expectNextCount(9)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchifEmpty() {
        int strLength = 6;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchifempty(strLength);
        StepVerifier.create(namesFlux)
                //.expectNext(List.of("A", "L", "E", "X"))
                //   .expectNextCount(9)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {

        var concatFlux = fluxAndMonoGeneratorService.explore_concat();
        StepVerifier.create(concatFlux)
                //.expectNext(List.of("A", "L", "E", "X"))
                //   .expectNextCount(9)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_merge() {

        var mergeFlux = fluxAndMonoGeneratorService.explore_merge();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {
        var mergeSeqFlux = fluxAndMonoGeneratorService.explore_mergeSequential();
        StepVerifier.create(mergeSeqFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        var mergeZipFlux = fluxAndMonoGeneratorService.explore_zip();
        StepVerifier.create(mergeZipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1() {
        var mergeZipTouplesFlux = fluxAndMonoGeneratorService.explore_zip_1();
        StepVerifier.create(mergeZipTouplesFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void exception_flux() {
        var value = fluxAndMonoGeneratorService.exception_flux();

        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void explore_OnErrorReturn() {
        var value = fluxAndMonoGeneratorService.explore_OnErrorReturn();
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }
}
