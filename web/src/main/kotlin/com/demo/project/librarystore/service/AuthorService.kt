package com.demo.project.librarystore.service

import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.model.toModel
import com.demo.project.librarystore.repository.AuthorRepository
import java.time.LocalDate
import org.springframework.stereotype.Service


@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
) {
    fun getAllAuthors(): List<AuthorModel> {
        return authorRepository.getAllAuthors().map { it.toModel() }
    }

    fun getAuthorById(id: Int): AuthorModel {
        return authorRepository.getAuthorById(id)?.toModel()
            ?: throw IllegalArgumentException("Author not found.")
    }

    fun getAuthorByName(name: String): List<AuthorModel> {
        return authorRepository.getAuthorByName(name).map { it.toModel() }
    }

    fun createAuthor(name: String, birthDate: LocalDate): Int {
        return authorRepository.createAuthor(name, birthDate)
            ?: throw IllegalArgumentException("Author not created.")
    }

    fun updateAuthor(id: Int, name: String?, birthDate: LocalDate?) {
        authorRepository.updateAuthor(id, name, birthDate)
    }

    fun deleteAuthorById(id: Int) {
        // TODO: Safely delete author relationship with books
        authorRepository.deleteAuthor(id)
    }
}