package com.demo.project.librarystore.model

import com.demo.project.librarystore.jooq.generated.tables.pojos.Book

data class BookModel(
    val id: Int,
    val title: String,
    val price: Int,
    val publishStatus: Boolean,
    val authors: List<AuthorModel>,
)

fun Book.toModel(authorModels: List<AuthorModel>): BookModel {
    return BookModel(
        id = id!!,
        title = title!!,
        price = price!!,
        publishStatus = publishedStatus ?: false,
        authors = authorModels,
    )
}
