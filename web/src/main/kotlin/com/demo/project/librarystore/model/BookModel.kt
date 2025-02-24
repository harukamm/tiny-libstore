package com.demo.project.librarystore.model

import com.demo.project.librarystore.entity.Book

data class BookModel(
    val id: Int,
    val title: String,
    val price: Int,
    val publishedStatus: Boolean,
    val authors: List<AuthorModel>,
)

fun Book.toModel(): BookModel {
    return BookModel(
        id = id,
        title = title,
        price = price,
        publishedStatus = publishedStatus,
        authors = authors.map { it.toModel() },
    )
}
