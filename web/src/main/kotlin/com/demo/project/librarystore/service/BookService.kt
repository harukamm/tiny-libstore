package com.demo.project.librarystore.service

import com.demo.project.librarystore.entity.Book
import com.demo.project.librarystore.exception.ResourceNotFoundException
import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.AuthorRepository
import com.demo.project.librarystore.repository.BookRepository
import org.apache.coyote.BadRequestException
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
        publishStatus: Boolean,
        authorIds: List<Int>,
    ): Int {
        val exist: Book? = bookRepository.getBookById(id)
        if (exist != null) {
            throw BadRequestException("Book id already used.")
        }
        validateBookAuthorIds(authorIds, false)
        return bookRepository.createBook(id, title, price, publishStatus, authorIds)
            ?: throw RuntimeException("Book not created")
    }

    fun updateBook(
        id: Int,
        title: String?,
        price: Int?,
        publishStatus: Boolean?,
        authorIds: List<Int>?,
    ) {
        validateBookAuthorIds(authorIds, true)
        val exist = getBookByIdOrThrow(id)
        if (exist.publishedStatus && publishStatus == false) {
            throw BadRequestException("Book cannot be changed to unpublished status.")
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
        if (!authorRepository.checkAllAuthorsExist(authorIds)) {
            throw ResourceNotFoundException("Includes non-existent author.")
        }
    }
}
