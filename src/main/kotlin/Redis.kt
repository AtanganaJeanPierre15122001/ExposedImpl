package com.example

import io.ktor.server.application.*
import redis.clients.jedis.Jedis

fun Application.configureRedis() {
    val jedis = Jedis("localhost", 6379)

    // Mettre en cache les résultats de recherche
    fun cacheSearchResults(query: String, results: List<Book>) {
        jedis.setex("search:$query", 3600, results.joinToString("\n") { it.toString() })
    }

    // Récupérer les résultats de recherche depuis le cache
    fun getCachedSearchResults(query: String): List<Book>? {
        val cachedResults = jedis.get("search:$query")
        return cachedResults?.split("\n")?.map { Book.fromString(it) }
    }
}