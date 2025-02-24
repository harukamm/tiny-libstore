package com.demo.project.librarystore.model

import com.demo.project.librarystore.entity.Author
import java.time.LocalDate
import org.springframework.format.annotation.DateTimeFormat

data class AuthorModel (
    val id: Int,
    val name: String,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val birthDay: LocalDate,
)

fun Author.toModel() = AuthorModel(
    id = id,
    name = name,
    birthDay = birthDay,
)
