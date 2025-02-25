package com.demo.project.librarystore.exception

import org.springframework.http.HttpStatus

class NotSupportedOperation(message: String) : AppBaseException(message, HttpStatus.FORBIDDEN) {
    companion object {
        private const val serialVersionUID = -108L
    }
}
