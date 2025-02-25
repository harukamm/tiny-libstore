package com.demo.project.librarystore.service

import com.demo.project.librarystore.exception.IdAlreadyExistsException
import com.demo.project.librarystore.exception.ResourceNotFoundException
import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.AuthorRepository
import com.demo.project.librarystore.repository.BookRepository
import org.apache.coyote.BadRequestException
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {
    fun getBookByIdOrThrow(bookId: Int): BookModel {
        bookRepository.getBookById(bookId)?.let {
            return it.toModel()
        } ?: throw ResourceNotFoundException("Book not found.")
    }

    fun createBook(
        id: Int,
        title: String,
        price: Int,
        publishedStatus: Boolean,
        authorIds: List<Int>,
    ): Int {
        validateBookAuthorIds(authorIds, false)
        try {
            return bookRepository.createBook(id, title, price, publishedStatus, authorIds)
                ?: throw RuntimeException("Book not created.")
        } catch (e: DuplicateKeyException) {
            throw IdAlreadyExistsException("Book id already used.")
        }
    }

    fun updateBook(
        id: Int,
        title: String?,
        price: Int?,
        publishedStatus: Boolean?,
        authorIds: List<Int>?,
    ) {
        validateBookAuthorIds(authorIds, true)
        val exist = getBookByIdOrThrow(id)
        if (exist.publishedStatus && publishedStatus == false) {
            throw BadRequestException("Book cannot be changed to unpublished status.")
        }
        bookRepository.updateBook(id, title, price, publishedStatus, authorIds)
    }

    fun deleteBookById(id: Int) {
        val deletedCount = bookRepository.deleteBookById(id)
        if (deletedCount == 0) {
            throw ResourceNotFoundException("Book not found.")
        }
    }

    fun getBooksByAuthorId(authorId: Int): List<BookModel> {
        return bookRepository.getBooksByAuthorId(authorId).map { it.toModel() }
    }

    private fun validateBookAuthorIds(
        authorIds: List<Int>?,
        acceptNull: Boolean,
    ) {
        if (authorIds == null) {
            if (acceptNull) {
                return
            }
            throw BadRequestException("Book must have at least one author.")
        }
        if (authorIds.isEmpty()) {
            throw BadRequestException("Book must have at least one author.")
        }
        if (authorIds.toSet().size != authorIds.size) {
            throw BadRequestException("Includes duplicate author.")
        }
        if (!authorRepository.checkAllAuthorsExist(authorIds)) {
            throw ResourceNotFoundException("Includes non-existent author.")
        }
    }
}
