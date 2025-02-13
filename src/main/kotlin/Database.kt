package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime


object Books : Table("books") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 100)
    val author = varchar("author", 100)
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val email = varchar("email", 100)

    override val primaryKey = PrimaryKey(id)
}

object Borrowings : Table("borrowings") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val bookId = integer("book_id").references(Books.id)
//    val borrowedAt = datetime("borrowed_at")
//    val returnedAt = datetime("returned_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

fun Application.configureDatabase() {
    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/library",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "lemeilleur"
    )

    transaction {
        SchemaUtils.create(Books, Users, Borrowings)
    }
}