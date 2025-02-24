package com.demo.project.librarystore.controller

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate
import org.springframework.format.annotation.DateTimeFormat

data class CreateBookRequest(
    @field:Min(1)
    val id: Int,
    @field:NotBlank
    val title: String,
    @field:Min(0)
    val price: Int,
    val publishStatus: Boolean,
    @field:NotBlank
    @field:Size(min = 1, max = 50)
    val authorIds: List<Int>,
)

data class UpdateBookRequest(
    @field:NotBlank
    val title: String?,
    @field:Min(0)
    val price: Int?,
    val publishStatus: Boolean?,
    @field:Size(min = 1, max = 50)
    val authorIds: List<Int>?,
)

data class CreateAuthorRequest(
    @field:Min(1)
    val id: Int,
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