package com.example

import redis.clients.jedis.Jedis

class configureRedis {
    private val jedis = Jedis("localhost", 6379)

    // Mettre en cache les résultats de recherche seulement s'ils existent
    fun cacheSearchResults(query: String, results: List<Book>) {
        if (results.isNotEmpty()) {
            jedis.setex("search:$query", 3600, results.joinToString("\n") { it.toString() })
        }
    }

    // Récupérer les résultats de recherche depuis le cache
    fun getCachedSearchResults(query: String): List<Book>? {
        val cachedResults = jedis.get("search:$query")
        return if (cachedResults != null) {
            cachedResults.split("\n").map { Book.fromString(it) }
        } else {
            null
        }
    }
}
