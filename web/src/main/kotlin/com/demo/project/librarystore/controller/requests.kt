package com.demo.project.librarystore.controller

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate
import org.springframework.format.annotation.DateTimeFormat

data class CreateBookRequest(
    val title: String,
    val price: Int,
    val publishStatus: Boolean,
    @field:NotBlank
    @field:Size(min = 1)
    val authorIds: List<Int>,
)

data class UpdateBookRequest(
    val title: String?,
    val price: Int?,
    val publishStatus: Boolean?,
    @field:Size(min = 1)
    val authorIds: List<Int>?,
)

data class CreateAuthorRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    @DateTimeFormat(pattern = "yyyyMMdd")
    val birthDay: LocalDate,
)

data class UpdateAuthorRequest(
    @field:NotBlank
    val name: String?,
    @field:NotBlank
    @DateTimeFormat(pattern = "yyyyMMdd")
    val birthDay: LocalDate?,
)