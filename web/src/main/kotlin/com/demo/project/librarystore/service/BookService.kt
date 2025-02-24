package com.demo.project.librarystore.service

import com.demo.project.librarystore.entity.Book
import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.BookRepository
import org.apache.commons.lang3.NotImplementedException
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {
    fun getAllBooks(): List<BookModel> {
        throw NotImplementedException("tobe")
    }

    fun getBookByIdOrThrow(bookId: Int): BookModel {
        bookRepository.getBookById(bookId)?.let {
            return it.toModel()
        } ?: throw IllegalArgumentException("Book not found.")
    }

    fun createBook(
        id: Int,
        title: String,
        price: Int,
        publishStatus: Boolean,
        authorIds: List<Int>
    ): Int {
        val exist: Book? = bookRepository.getBookById(id)
        if (exist != null) {
            throw IllegalArgumentException("Book id already used.")
        }
        if (authorIds.isEmpty()) {
            throw IllegalArgumentException("Book must have at least one author.")
        }
        return bookRepository.createBook(id, title, price, publishStatus, authorIds)
            ?: throw IllegalArgumentException("Book not created")
    }

    fun updateBook(
        id: Int,
        title: String?,
        price: Int?,
        publishStatus: Boolean?,
        authorIds: List<Int>?,
    ) {
        if (authorIds != null && authorIds.isEmpty()) {
            throw IllegalArgumentException("Book must have at least one author.")
        }
        val exist = getBookByIdOrThrow(id)
        if (exist.publishedStatus && publishStatus == false) {
            throw IllegalArgumentException("Book cannot changed to unpublished status.")
        }

        bookRepository.updateBook(id, title, price, publishStatus, authorIds)
    }

    fun deleteBookById(id: Int) {
        getBookByIdOrThrow(id)
        bookRepository.deleteBookById(id)
    }

    fun getBooksByAuthorId(authorId: Int): List<BookModel> {
        return bookRepository.getBooksByAuthorId(authorId).map { it.toModel() }
    }
}