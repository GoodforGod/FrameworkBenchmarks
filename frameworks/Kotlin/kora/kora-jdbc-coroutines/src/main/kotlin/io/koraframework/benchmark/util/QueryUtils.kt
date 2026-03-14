package io.koraframework.benchmark.util

import java.util.concurrent.ThreadLocalRandom

object QueryUtils {
    private const val MIN_QUERIES = 1
    private const val MAX_QUERIES = 500
    private const val WORLD_ROWS = 10_000

    fun parseCount(value: Int?): Int {
        if (value == null || value < MIN_QUERIES) {
            return MIN_QUERIES
        }
        return minOf(value, MAX_QUERIES)
    }

    fun randomWorld(): Int = ThreadLocalRandom.current().nextInt(WORLD_ROWS) + 1
}
