package com.demo.project.librarystore.exception

import org.springframework.http.HttpStatus

class IdAlreadyExistsBaseException(message: String) : AppBaseException(message, HttpStatus.CONFLICT) {
    companion object {
        private const  val serialVersionUID = -7883L
    }
}