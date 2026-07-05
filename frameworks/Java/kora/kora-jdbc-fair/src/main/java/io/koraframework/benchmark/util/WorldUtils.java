package io.koraframework.benchmark.util;

import java.util.concurrent.ThreadLocalRandom;

public final class WorldUtils {

    private WorldUtils() { }

    private static final int MIN_QUERIES = 1;
    private static final int MAX_QUERIES = 500;
    private static final int WORLD_ROWS = 10000;

    public static int parseCount(String value) {
        if (value == null) {
            return MIN_QUERIES;
        }

        int parsedValue;
        try {
            parsedValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return MIN_QUERIES;
        }

        if (parsedValue < MIN_QUERIES) {
            return MIN_QUERIES;
        }

        return Math.min(parsedValue, MAX_QUERIES);
    }

    public static int randomWorld() {
        return ThreadLocalRandom.current().nextInt(WORLD_ROWS) + 1;
    }

    public static int[] randomWorlds(int amount) {
        return ThreadLocalRandom.current()
                .ints(1, WORLD_ROWS + 1)
                .distinct()
                .limit(amount)
                .toArray();
    }
}
