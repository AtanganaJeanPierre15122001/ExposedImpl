package com.example

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val description: String
) {
    override fun toString(): String {
        return "$id|$title|$author|$description"
    }

    companion object {
        fun fromString(str: String): Book {
            val parts = str.split("|")
            return Book(parts[0].toInt(), parts[1], parts[2], parts[3])
        }
    }
}