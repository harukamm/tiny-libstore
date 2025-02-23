package com.demo.project.librarystore.entity

data class Book (
    val id: Int,
    val title: String,
    val price: Int,
    val publishedStatus: Boolean,
    val authors: List<Author>,
)