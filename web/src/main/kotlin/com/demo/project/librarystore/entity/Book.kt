package com.demo.project.librarystore.entity

data class Book (
    val id: Int,
    val title: String,
    val price: Double,
    val publishStatus: Boolean,
    val publishDate: String,
)