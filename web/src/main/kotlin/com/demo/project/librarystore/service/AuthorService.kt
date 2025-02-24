package com.demo.project.librarystore.service

import com.demo.project.librarystore.exception.ResourceNotFoundException
import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.AuthorRepository
import java.lang.RuntimeException
import java.time.LocalDate
import org.springframework.stereotype.Service


@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
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

    fun createAuthor(id: Int, name: String, birthDate: LocalDate): Int {
        authorRepository.getAuthorById(id)?.let {
            throw ResourceNotFoundException("Author id already used.")
        }
        return authorRepository.createAuthor(id, name, birthDate)
            ?: throw RuntimeException("Author not created.")
    }

    fun updateAuthor(id: Int, name: String?, birthDate: LocalDate?) {
        getAuthorByIdOrThrow(id)
        authorRepository.updateAuthor(id, name, birthDate)
    }

    fun deleteAuthorById(id: Int) {
        getAuthorByIdOrThrow(id)
        authorRepository.deleteAuthor(id)
    }
}