package com.demo.project.librarystore.exception

import org.springframework.http.HttpStatus

class ResourceNotFoundException(message: String) : AppBaseException(message, HttpStatus.NOT_FOUND) {
    companion object {
        @java.io.Serial
        private const val serialVersionUID = -44725771087799L
    }
}
