package com.demo.project.librarystore.controller.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class CreateBookRequest(
    @field:Min(1)
    val id: Int,
    @field:Size(min = 1, max = 200)
    val title: String,
    @field:Min(0)
    val price: Int,
    val publishedStatus: Boolean,
    @field:NotNull
    @field:Size(min = 1, max = 50)
    val authorIds: List<Int>,
)

data class UpdateBookRequest(
    @field:Size(min = 1, max = 200)
    val title: String?,
    @field:Min(0)
    val price: Int?,
    val publishedStatus: Boolean?,
    @field:Size(min = 1, max = 50)
    val authorIds: List<Int>?,
)

data class CreateAuthorRequest(
    @field:Min(1)
    val id: Int,
    @field:Size(min = 1, max = 200)
    val name: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val birthDay: LocalDate,
) {
    @JsonIgnore
    @AssertTrue(message = "Only past dates are allowed.")
    fun isPastDate(): Boolean {
        return birthDay.isBefore(LocalDate.now())
    }
}

data class UpdateAuthorRequest(
    @field:Size(min = 1, max = 200)
    val name: String?,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val birthDay: LocalDate?,
) {
    @JsonIgnore
    @AssertTrue(message = "Only past dates are allowed.")
    fun isPastDate(): Boolean {
        return birthDay == null || birthDay.isBefore(LocalDate.now())
    }
}
