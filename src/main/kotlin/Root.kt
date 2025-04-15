package com.example

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import com.example.configureElasticSearch
import com.example.configureRedis
import io.ktor.http.*
import io.ktor.server.request.*
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import redis.clients.jedis.Jedis

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

        post("/index") {
            val book = call.receive<Book>() // Récupérer le livre envoyé en JSON

//            call.respondText (""+book)
            val elasticSearch = configureElasticSearch()

            elasticSearch.indexBook(book) // Ajouter dans Elasticsearch

            call.respond(HttpStatusCode.Created, "Book indexed successfully")
        }

        get("/search") {
            val query = call.request.queryParameters["q"] ?: return@get call.respondText("Query parameter 'q' is required", status = HttpStatusCode.BadRequest)

            val redis = configureRedis()
            val cachedResults = redis.getCachedSearchResults(query)
//            call.respondText (""+cachedResults)

            if (cachedResults != null) {
                call.respondText("yo")
            } else {
                val elasticSearch = configureElasticSearch()
                val results = elasticSearch.searchBooks(query)
                call.respondText("yo$results")

                if (results.isNotEmpty()) {
                    redis.cacheSearchResults(query, results)
                }

                call.respond(results)
            }
        }

    }

    
}