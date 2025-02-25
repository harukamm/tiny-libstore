package com.demo.project.librarystore.config

import com.demo.project.librarystore.exception.AppBaseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.apache.coyote.BadRequestException
import org.jooq.exception.DataAccessException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.sql.SQLException
import java.time.format.DateTimeParseException

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(AppBaseException::class)
    fun handleResourceNotFoundException(ex: AppBaseException): ResponseEntity<Any> {
        logger.info("app-defined exception", ex)
        return ResponseEntity.status(ex.httpStatusCode).body(mapOf("error" to ex.message))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<Any> {
        logger.info("Bad request", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to ex.message))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<Any> {
        logger.info("Method argument type mismatch", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Expected type ${ex.requiredType} for ${ex.parameter.parameterName}"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        logger.info("Method argument not valid", ex)
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to errors.joinToString(", ")))
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateTimeParseException(ex: DateTimeParseException): ResponseEntity<Any> {
        logger.info("Date time parse exception", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Invalid date format."))
    }

    @ExceptionHandler(MismatchedInputException::class)
    fun handleMismatchedInputException(ex: MismatchedInputException): ResponseEntity<Any> {
        logger.info("Mismatched input exception", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Missing input."))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageConversionException(ex: HttpMessageNotReadableException): ResponseEntity<Any> {
        logger.info("Http message conversion exception", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to ex.message))
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(ex: DataAccessException): ResponseEntity<Any> {
        logger.warn("Jooq Data Access exception", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "database access error."))
    }

    @ExceptionHandler(SQLException::class)
    fun handleSqlException(ex: SQLException): ResponseEntity<Any> {
        logger.warn("Sql exception", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "database access error."))
    }

    @ExceptionHandler(NoResourceFoundException::class, NoHandlerFoundException::class)
    fun handleNoResourceFoundException(ex: Exception): ResponseEntity<Any> {
        logger.info("No controller resource found exception", ex)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to ex.message))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<Any> {
        logger.info("Http request method not supported", ex)
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(mapOf("error" to ex.message))
    }

    @ExceptionHandler(HttpMediaTypeException::class)
    fun handleHttpMediaTypeException(ex: HttpMediaTypeException): ResponseEntity<Any> {
        logger.info("Http media type exception", ex)
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(mapOf("error" to ex.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception): ResponseEntity<Any> {
        logger.error("Unexpected error", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "An unexpected error occurred."))
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java)
    }
}
