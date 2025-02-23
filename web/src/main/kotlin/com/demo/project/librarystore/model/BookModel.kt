package com.demo.project.librarystore.model

import com.demo.project.librarystore.entity.Book

data class BookModel(
    val id: Int,
    val title: String,
    val price: Double,
    val publishStatus: Boolean,
    val publishDate: String,
    val authors: List<AuthorModel>,
)

fun Book.toModel(authorModels: List<AuthorModel>): BookModel {
    return BookModel(
        id = id,
        title = title,
        price = price,
        publishStatus = publishStatus,
        publishDate = publishDate,
        authors = authorModels
    )
}
