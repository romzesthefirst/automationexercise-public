package com.shangin.automationexercise.support;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/** Invocation-local randomness; replay does not depend on worker scheduling. */
public final class TestRandom {
    private record State(Random data, Random products, Consumer<String> report) {}

    private static final ThreadLocal<State> CURRENT = new ThreadLocal<>();

    private TestRandom() {}

    public static void begin(Consumer<String> report) {
        long seed =
                Long.parseLong(
                        System.getProperty(
                                "test.seed",
                                Long.toString(ThreadLocalRandom.current().nextLong())));
        begin(seed, report);
    }

    public static void begin(long seed, Consumer<String> report) {
        CURRENT.set(new State(new Random(seed), new Random(seed ^ 0x5DEECE66DL), report));
        report.accept("Test seed: " + seed);
    }

    private static State state() {
        if (CURRENT.get() == null) {
            begin(System.out::println);
        }
        return CURRENT.get();
    }

    public static Random data() {
        return state().data();
    }

    public static int productIndex(int size) {
        return state().products().nextInt(size);
    }

    public static void selectedProduct(String identifier) {
        state().report().accept("Selected product: " + identifier);
    }

    public static void clear() {
        CURRENT.remove();
    }
}
