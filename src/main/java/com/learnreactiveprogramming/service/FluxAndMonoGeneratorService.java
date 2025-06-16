package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Name is : " + name);
                });

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Mono name is : " + name);
                });
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log();

    }

    public Mono<String> nameMono() {
        return Mono.just("alex");
    }

    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFlux_map(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .map(s -> s.length() + "-" + s)
                .doOnNext(name -> {
                    System.out.println("Name is : " + name);
                })
                .doOnSubscribe(s -> {
                    System.out.println("Subscription is : " + s);
                })
                .doOnComplete(() -> {
                    System.out.println("Inside the complete callback");
                })
                .doFinally(signalType -> {
                    System.out.println("Inside dofinally :" + signalType);
                })
                .log();
    }

    public Flux<String> namesFlux_flatmap(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                //.map(s -> s.length() + "-" + s)
                .flatMap(s -> splitString(s))
                .log();
    }

    public Flux<String> namesFlux_flatmap_async(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                //.map(s -> s.length() + "-" + s)
                .flatMap(s -> splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_concatmap(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                //.map(s -> s.length() + "-" + s)
                .concatMap(s -> splitString_withDelay(s))
                .log();
    }

    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFlux_immutability() {
        var nameFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        nameFlux.map(String::toUpperCase);
        return nameFlux;
    }

    public Mono<List<String>> namesMono_flatmap(int stringlength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringlength)
                .flatMap(this::splitStringMono)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> namesMono_flatMapMany(int stringlength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringlength)
                .flatMapMany(this::splitString)
                .log();
    }

    public Flux<String> namesFlux_transform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(s -> splitString(s))
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchifempty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filterMap);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> explore_concat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatwith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.concatWith(defFlux);
    }

    public Flux<String> explore_concatwith_mono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");
        return aMono.concatWith(bMono);
    }

    public Flux<String> explore_merge() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));
        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_mergeSequential() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));
        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> explore_zip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
    }

    public Flux<String> explore_zip_1() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");
        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }

    public Flux<String> explore_zipWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.zipWith(defFlux, (first, second) -> first + second).log();
    }


    public Mono<String> explore_ZipWith_mono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");
        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }

    public Flux<String> exception_flux() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D")).log();
    }

    public Flux<String> explore_OnErrorReturn() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalArgumentException(("Exception occurred"))))
                .onErrorReturn("D").log();
    }
}
