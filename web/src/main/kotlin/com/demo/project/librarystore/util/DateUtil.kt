package com.demo.project.librarystore.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DateUtil {
    private fun parseDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format. Please use YYYYMMDD format.", e)
        }
    }
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}