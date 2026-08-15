package io.techempower.benchmark.kora.util

import java.util.concurrent.ThreadLocalRandom

object QueryUtils {
    private const val MIN_QUERIES = 1
    private const val MAX_QUERIES = 500
    private const val WORLD_ROWS = 10001

    fun parseCount(value: String?): Int {
        val parsed = value?.toIntOrNull() ?: return MIN_QUERIES
        if (parsed < MIN_QUERIES) {
            return MIN_QUERIES
        }
        return parsed.coerceAtMost(MAX_QUERIES)
    }

    fun randomWorld(): Int = ThreadLocalRandom.current().nextInt(1, WORLD_ROWS)

    fun randomWorld(exclusion: Int): Int {
        val nextWorldId = randomWorld()
        return if (nextWorldId == exclusion) randomWorld(exclusion) else nextWorldId
    }
}
