package io.koraframework.benchmark.util

import java.util.concurrent.ThreadLocalRandom

object QueryUtils {
    private const val MIN_QUERIES = 1
    private const val MAX_QUERIES = 500
    private const val WORLD_ROWS = 10_000

    fun parseCount(value: String?): Int {
        if (value == null) {
            return MIN_QUERIES
        }

        val parsedValue = value.toIntOrNull() ?: return MIN_QUERIES
        if (parsedValue < MIN_QUERIES) {
            return MIN_QUERIES
        }

        return minOf(parsedValue, MAX_QUERIES)
    }

    fun randomWorld(): Int = ThreadLocalRandom.current().nextInt(WORLD_ROWS) + 1

    fun addNextRandomWorld(ids: MutableSet<Int>, randomOccupied: Int) {
        var next = randomOccupied
        do {
            next++
            if (next > WORLD_ROWS) {
                next = 1
            }
        } while (!ids.add(next))
    }
}
