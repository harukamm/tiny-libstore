package com.demo.project.librarystore.controller

import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.service.BookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController("Book_Controller_v1")
@RequestMapping("/lib-store/v1.0/")
@Tag(name = "Book", description = "Book API")
class BookController(
    private val bookService: BookService,
) {
    @Operation(
        summary = "Get all books",
        description = "Get all books."
    )
    @GetMapping("/books")
    fun getAllBooks(): List<BookModel> {
        // TODO: Support offset and limit.
        return bookService.getAllBooks()
    }

    @Operation(
        summary = "Get book by ID",
        description = "Get book by ID. Fails if the book does not exist."
    )
    @GetMapping("/books/{bookId}")
    fun getBookById(@PathVariable bookId: Int): BookModel {
        return bookService.getBookByIdOrThrow(bookId)
    }

    @Operation(
        summary = "Create book",
        description = "Create a new book. Fails if the ID is already taken."
    )
    @PostMapping("/books")
    fun createBook(@RequestBody request: CreateBookRequest): NewIdCreatedResponse {
        return NewIdCreatedResponse(
            bookService.createBook(
                request.id,
                request.title,
                request.price,
                request.publishStatus,
                request.authorIds
            )
        )
    }

    @Operation(
        summary = "Update book",
        description = "Update book by ID. Fails if the book does not exist."
    )
    @PutMapping("/books/{bookId}")
    fun updateBook(
        @PathVariable bookId: Int,
        @Valid @RequestBody request: UpdateBookRequest,
    ) {
        return bookService.updateBook(
            bookId,
            request.title,
            request.price,
            request.publishStatus,
            request.authorIds
        )
    }

    @Operation(
        summary = "Delete book",
        description = "Delete book by ID. Fails if the book does not exist."
    )
    @DeleteMapping("/books/{bookId}")
    fun deleteBook(@PathVariable bookId: Int) {
        bookService.deleteBookById(bookId)
    }

    @Operation(
        summary = "Get books by author",
        description = "Get books by author id.\n"
                + "Event if the ID does not exist, the API will just return an empty list."
    )
    @GetMapping("/authors/{authorId}/books")
    fun getBooksByAuthor(@PathVariable authorId: Int): List<BookModel> {
        return bookService.getBooksByAuthorId(authorId)
    }
}