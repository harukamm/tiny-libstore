package com.demo.project.librarystore.repository

import com.demo.project.librarystore.JooqIntegrationBase
import com.demo.project.librarystore.entity.Author
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AuthorRepositoryTest(
    @Autowired private val repository: AuthorRepository,
    @Autowired private val context: DSLContext,
) : JooqIntegrationBase(context) {
    @Test
    fun `getAuthorById returns an author when found`() {
        val id = createAuthor(1, "Test Author", LocalDate.of(1991, 1, 1))
        val author = repository.getAuthorById(1)
        assertThat(author).isNotNull
        assertThat(author!!.id).isEqualTo(1)
        assertThat(author.name).isEqualTo("Test Author")
        assertThat(author.birthDay).isEqualTo(LocalDate.of(1991, 1, 1))
    }

    @Test
    fun `getAuthorById returns null when author not found`() {
        val author = repository.getAuthorById(999)
        assertThat(author).isNull()
    }

    @Test
    fun `getAllAuthors returns all authors`() {
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        val authors = repository.getAllAuthors()
        assertThat(authors).hasSize(2)
        assertThat(authors).contains(
            Author(
                id = authorId1,
                name = "Test Author 1",
                birthDay = LocalDate.of(1991, 1, 1),
            ),
            Author(
                id = authorId2,
                name = "Test Author 2",
                birthDay = LocalDate.of(1992, 2, 2),
            ),
        )
    }

    @Test
    fun `getAuthorByName returns author when found`() {
        val authorId = createAuthor(1, "Test Author", LocalDate.of(1991, 1, 1))
        val authors = repository.getAuthorByName("Test Author")
        assertThat(authors).hasSize(1)
        assertThat(authors[0].id).isEqualTo(authorId)
        assertThat(authors[0].name).isEqualTo("Test Author")
        assertThat(authors[0].birthDay).isEqualTo(LocalDate.of(1991, 1, 1))
    }

    @Test
    fun `getAuthorByName returns empty list when no authors found`() {
        val authors = repository.getAuthorByName("Not existent author")
        assertThat(authors).isEmpty()
    }

    @Test
    fun `createAuthor creates author successfully`() {
        val authorId = repository.createAuthor(42, "New Author", LocalDate.of(1990, 1, 1))
        assertThat(authorId).isEqualTo(42)
    }

    @Test
    fun `updateAuthor updates author successfully`() {
        val authorId = createAuthor(1, "Old Author", LocalDate.of(1980, 1, 1))
        repository.updateAuthor(authorId, "Updated Author", LocalDate.of(1985, 5, 5))
        val updatedAuthor = repository.getAuthorById(authorId)
        assertThat(updatedAuthor).isNotNull
        assertThat(updatedAuthor!!.id).isEqualTo(1)
        assertThat(updatedAuthor.name).isEqualTo("Updated Author")
        assertThat(updatedAuthor.birthDay).isEqualTo(LocalDate.of(1985, 5, 5))
    }

    @Test
    fun `updateAuthor does not update author when author not found`() {
        repository.updateAuthor(999, "Updated Author", LocalDate.of(1985, 5, 5))
        val updatedAuthor = repository.getAuthorById(999)
        assertThat(updatedAuthor).isNull()
    }

    @Test
    fun `updateAuthor does nothing when all parameters are null`() {
        val authorId = createAuthor(1, "Old Author", LocalDate.of(1980, 1, 1))
        repository.updateAuthor(authorId, null, null)
        val updatedAuthor = repository.getAuthorById(authorId)
        assertThat(updatedAuthor).isNotNull
        assertThat(updatedAuthor!!.id).isEqualTo(1)
        assertThat(updatedAuthor.name).isEqualTo("Old Author")
        assertThat(updatedAuthor.birthDay).isEqualTo(LocalDate.of(1980, 1, 1))
    }

    @Test
    fun `deleteAuthor deletes author successfully`() {
        val authorId = createAuthor(1, "Author to Delete", LocalDate.of(1970, 1, 1))
        repository.deleteAuthor(authorId)
        val deletedAuthor = repository.getAuthorById(authorId)
        assertThat(deletedAuthor).isNull()
    }

    @Test
    fun `deleteAuthor deletes author, book-author relations as well`() {
        val authorIdToDelete = createAuthor(1, "Author to Delete", LocalDate.of(1970, 1, 1))
        val authorId = createAuthor(2, "Author", LocalDate.of(1972, 2, 2))
        val bookId = createBook(1, "Book with Author", 100, true)
        createBookAuthor(bookId, authorId)
        createBookAuthor(bookId, authorIdToDelete)

        repository.deleteAuthor(authorIdToDelete)

        assertThat(repository.getAuthorById(authorIdToDelete)).isNull()
        assertThat(repository.getAuthorById(authorId)).isNotNull
        val authorsCountOfBook = getBookAuthorsRecordCount(bookId)
        assertThat(authorsCountOfBook).isEqualTo(1)
    }
}