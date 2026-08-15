package io.techempower.benchmark.ktor.model

import kotlinx.serialization.Serializable

@Serializable
data class World(val id: Int, val randomNumber: Int)
