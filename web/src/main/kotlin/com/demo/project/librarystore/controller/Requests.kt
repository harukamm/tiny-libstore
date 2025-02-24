package com.demo.project.librarystore.controller

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class CreateBookRequest(
    @field:Min(1)
    val id: Int,
    @field:NotBlank
    val title: String,
    @field:Min(0)
    val price: Int,
    val publishStatus: Boolean,
    @field:NotNull
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    val birthDay: LocalDate,
) {
    @AssertTrue(message = "Only past dates are allowed.")
    fun isPastDate(): Boolean {
        return birthDay.isBefore(LocalDate.now())
    }
}

data class UpdateAuthorRequest(
    @field:NotBlank
    val name: String?,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val birthDay: LocalDate?,
) {
    @AssertTrue(message = "Only past dates are allowed.")
    fun isPastDate(): Boolean {
        return birthDay == null || birthDay.isBefore(LocalDate.now())
    }
}
