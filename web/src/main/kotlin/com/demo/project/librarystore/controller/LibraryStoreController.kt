package com.demo.project.librarystore.controller

import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.service.AuthorService
import com.demo.project.librarystore.service.BookService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/lib-store/v1.0/")
class LibraryStoreController(
    private val bookService: BookService,
    private val authorService: AuthorService,
) {
    @GetMapping("/books")
    fun getAllBooks(): List<BookModel> {
        return bookService.getAllBooks()
    }

    @GetMapping("/books/{bookId}")
    fun getBookById(@PathVariable bookId: Int): BookModel {
        return bookService.getBookById(bookId)
    }

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

    @PutMapping("/books/{bookId}")
    fun updateBook(
        @PathVariable bookId: Int,
        @RequestBody request: UpdateBookRequest,
    ) {
        return bookService.updateBook(
            bookId,
            request.title,
            request.price,
            request.publishStatus,
            request.authorIds
        )
    }

    @DeleteMapping("/books/{bookId}")
    fun deleteBook(@PathVariable bookId: Int) {
        bookService.deleteBookById(bookId)
    }

    @GetMapping("/authors")
    fun getAllAuthors(): List<AuthorModel> {
        return authorService.getAllAuthors()
    }

    @GetMapping("/authors/{authorId}")
    fun getAuthorById(@PathVariable authorId: Int): AuthorModel {
        return authorService.getAuthorById(authorId)
    }

    @PostMapping("/authors")
    fun createAuthor(@RequestBody request: CreateAuthorRequest): NewIdCreatedResponse {
        return NewIdCreatedResponse(authorService.createAuthor(request.id, request.name, request.birthDay))
    }

    @PutMapping("/authors/{authorId}")
    fun updateAuthor(
        @PathVariable authorId: Int,
        @RequestBody request: UpdateAuthorRequest
    ) {
        return authorService.updateAuthor(authorId, request.name, request.birthDay)
    }

    @DeleteMapping("/authors/{authorId}")
    fun deleteAuthor(@PathVariable authorId: Int) {
        authorService.deleteAuthorById(authorId)
    }

    @GetMapping("/authors/{authorId}/books")
    fun getBooksByAuthor(@PathVariable authorId: Int): List<BookModel> {
        return bookService.getBooksByAuthorId(authorId)
    }
}