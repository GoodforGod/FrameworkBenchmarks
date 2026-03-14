package io.koraframework.benchmark.util;

import java.util.concurrent.ThreadLocalRandom;

public final class QueryUtils {

    private QueryUtils() { }

    private static final int MIN_QUERIES = 1;
    private static final int MAX_QUERIES = 500;
    private static final int WORLD_ROWS = 10000;

    public static int parseCount(Integer value) {
        if (value == null) {
            return MIN_QUERIES;
        }
        if (value < MIN_QUERIES) {
            return MIN_QUERIES;
        }
        return Math.min(value, MAX_QUERIES);
    }

    public static int randomWorld() {
        return ThreadLocalRandom.current().nextInt(WORLD_ROWS) + 1;
    }
}
