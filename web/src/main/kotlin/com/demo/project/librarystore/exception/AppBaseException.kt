package com.demo.project.librarystore.exception

import org.springframework.http.HttpStatus

open class AppBaseException(message: String, val httpStatusCode: HttpStatus) : Exception(message) {
    companion object {
        private const val serialVersionUID = -2232L
    }
}
