package com.demo.project.librarystore.service

import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository
) {
    fun getAllBooks(): List<BookModel> {
        return bookRepository.getAllBooks().map { it.toModel(listOf()) }
    }

    fun getBookById(bookId: Int): BookModel {
        return bookRepository.getBookById(bookId)?.toModel(listOf())
            ?: throw IllegalArgumentException("Book not found")
    }

    fun createBook(
        title: String,
        price: Int,
        publishStatus: Boolean,
        authorIds: List<Int>
    ): Int {
        return bookRepository.createBook(title, price, publishStatus, authorIds)
            ?: throw IllegalArgumentException("Book not created")
    }

    fun updateBook(
        id: Int,
        title: String?,
        price: Int?,
        publishStatus: Boolean?,
        authorIds: List<Int>?,
    ) {
        // TODO: check if book is already published
        bookRepository.updateBook(id, title, price, publishStatus, authorIds)
    }

    fun deleteBookById(id: Int) {
        bookRepository.deleteBookById(id)
    }

    fun getBooksByAuthorId(authorId: Int): List<BookModel> {
        return bookRepository.getBooksByAuthorId(authorId).map { it.toModel(listOf()) }
    }
}