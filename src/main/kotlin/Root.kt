package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import com.example.configureElasticSearch
import com.example.configureRedis

fun Route.bookRoutes() {
    route("/books") {
        get {
            val books = transaction {
                Books.selectAll().map { row ->
                    Book(
                        id = row[Books.id],
                        title = row[Books.title],
                        author = row[Books.author],
                        description = row[Books.description]
                    )
                }
            }
            call.respond(books)
        }

        get("/search") {
            val query = call.request.queryParameters["q"] ?: ""
            val cachedResults = getCachedSearchResults(query)
            if (cachedResults != null) {
                call.respond(cachedResults)
            } else {
                val results = searchBooks(query)
                cacheSearchResults(query, results)
                call.respond(results)
            }
        }
    }

    
}