package com.demo.project.librarystore.entity

import java.time.LocalDate

data class Author(
    val id: Int,
    val name: String,
    val birthDay: LocalDate,
)
