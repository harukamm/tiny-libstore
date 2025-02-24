package com.demo.project.librarystore.model

import com.demo.project.librarystore.entity.Author
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class AuthorModel(
    val id: Int,
    val name: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val birthDay: LocalDate,
)

fun Author.toModel() =
    AuthorModel(
        id = id,
        name = name,
        birthDay = birthDay,
    )
