package com.demo.project.librarystore.model

import com.demo.project.librarystore.entity.Author
import java.time.LocalDate

data class AuthorModel (
    val id: Int,
    val name: String,
    val birthDay: LocalDate,
)

fun Author.toModel() = AuthorModel(
    id = id,
    name = name,
    birthDay = birthDay,
)
