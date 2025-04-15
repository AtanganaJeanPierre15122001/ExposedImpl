package com.example


import io.ktor.server.application.*
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport

import org.apache.http.HttpHost

import org.elasticsearch.client.RestClient


class configureElasticSearch {

    val restClient = RestClient.builder(HttpHost("localhost", 9200, "http")).build()
    val transport = RestClientTransport(restClient, JacksonJsonpMapper())
    val client = ElasticsearchClient(transport)
    // Indexer un livre dans ElasticSearch
    fun indexBook(book: Book) {
        client.index { i -> i
            .index("books")
            .id(book.id.toString())
            .document(book)
        }
    }

    // Rechercher des livres dans ElasticSearch
    fun searchBooks(query: String): List<Book> {
        val response = client.search({ s ->
            s.index("books")
                .query { q -> q
                    .multiMatch { mm -> mm
                        .fields("title", "author", "description")
                        .query(query)
                    }
                }
        }, Book::class.java)

        return response.hits().hits().map { hit -> hit.source()!! }
    }



}