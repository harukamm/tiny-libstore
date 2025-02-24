package com.demo.project.librarystore.exception

class ResourceNotFoundException(message: String) : RuntimeException(message) {
    companion object {
        @java.io.Serial
        private const  val serialVersionUID = -44725771087799L
    }
}