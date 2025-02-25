package com.demo.project.librarystore.service

import com.demo.project.librarystore.exception.IdAlreadyExistsBaseException
import com.demo.project.librarystore.exception.ResourceNotFoundBaseException
import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.AuthorRepository
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.LocalDate
import org.springframework.dao.DuplicateKeyException

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
) {
    fun getAllAuthors(): List<AuthorModel> {
        return authorRepository.getAllAuthors().map { it.toModel() }
    }

    fun getAuthorByIdOrThrow(id: Int): AuthorModel {
        return authorRepository.getAuthorById(id)?.toModel()
            ?: throw ResourceNotFoundBaseException("Author not found.")
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
            throw IdAlreadyExistsBaseException("Author id already used.")
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
        val deletedCount = authorRepository.deleteAuthor(id)
        if (deletedCount == 0) {
            throw ResourceNotFoundBaseException("Author not found.")
        }
    }
}
