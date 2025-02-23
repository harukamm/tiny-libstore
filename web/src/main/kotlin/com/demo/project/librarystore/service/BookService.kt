package com.demo.project.librarystore.service

import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.repository.BookRepository
import org.apache.commons.lang3.NotImplementedException
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {
    fun getAllBooks(): List<BookModel> {
       // return bookRepository.getAllBooks().map { it.toModel(listOf()) }
        throw NotImplementedException("tobe")
    }

    fun getBookById(bookId: Int): BookModel {
        throw NotImplementedException("tobe")
    }

    fun createBook(
        id: Int,
        title: String,
        price: Int,
        publishStatus: Boolean,
        authorIds: List<Int>
    ): Int {
        val newBookId = bookRepository.createBook(id, title, price, publishStatus, authorIds)
            ?: throw IllegalArgumentException("Book not created")
     //   bookAuthorsDao.insert(authorIds.map { BookAuthors(newBookId, it) })
        return newBookId
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
        throw NotImplementedException("tobe")
    }
}