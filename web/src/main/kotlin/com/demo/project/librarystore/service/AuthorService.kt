package com.demo.project.librarystore.service

import com.demo.project.librarystore.exception.IdAlreadyExistsException
import com.demo.project.librarystore.exception.NotSupportedOperation
import com.demo.project.librarystore.exception.ResourceNotFoundException
import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.AuthorRepository
import com.demo.project.librarystore.repository.BookRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.LocalDate

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository,
) {
    fun getAllAuthors(): List<AuthorModel> {
        return authorRepository.getAllAuthors().map { it.toModel() }
    }

    fun getAuthorByIdOrThrow(id: Int): AuthorModel {
        return authorRepository.getAuthorById(id)?.toModel()
            ?: throw ResourceNotFoundException("Author not found.")
    }

    fun getAuthorByName(name: String): List<AuthorModel> {
        return authorRepository.getAuthorByName(name).map { it.toModel() }
    }

    fun createAuthor(
        id: Int,
        name: String,
        birthDate: LocalDate,
    ): Int {
        try {
            return authorRepository.createAuthor(id, name, birthDate)
                ?: throw RuntimeException("Author not created.")
        } catch (e: DuplicateKeyException) {
            throw IdAlreadyExistsException("Author id already used.")
        }
    }

    fun updateAuthor(
        id: Int,
        name: String?,
        birthDate: LocalDate?,
    ) {
        getAuthorByIdOrThrow(id)
        authorRepository.updateAuthor(id, name, birthDate)
    }

    fun deleteAuthorById(id: Int) {
        if (bookRepository.isThereAssociatedBook(id)) {
            throw NotSupportedOperation("Author has associated books.")
        }
        val deletedCount = authorRepository.deleteAuthor(id)
        if (deletedCount == 0) {
            throw ResourceNotFoundException("Author not found.")
        }
    }
}
