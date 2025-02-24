package com.demo.project.librarystore.service

import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.repository.AuthorRepository
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.mockito.Mockito.lenient
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("TEST")
@SpringBootTest
class AuthorServiceMockTest {
    private lateinit var authorRepository: AuthorRepository
    private lateinit var service: AuthorService

    @BeforeEach
    fun setup() {
        authorRepository = Mockito.mock(AuthorRepository::class.java)
        service = AuthorService(authorRepository)
    }

    @Test
    fun `getAuthorById returns an author when found`() {
        val author = Author(10, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(author)
            .`when`(authorRepository)
            .getAuthorById(10)

        val res = service.getAuthorByIdOrThrow(10)
        assertThat(res).isNotNull
        assertThat(res.id).isEqualTo(10)
        assertThat(res.birthDay).isEqualTo(LocalDate.of(1991, 1, 1))
        assertThat(res.name).isEqualTo("Test Author")
    }

    @Test
    fun `getAuthorById returns null when book not found`() {
        lenient().doReturn(null)
            .`when`(authorRepository)
            .getAuthorById(10)

        val e = Assertions.assertThrows(IllegalArgumentException::class.java) {
            service.getAuthorByIdOrThrow(10)
        }
        assertThat(e.message).isEqualTo("Author not found.")
    }

    @Test
    fun `getAuthorByName returns a list of authors when found`() {
        val author1 = Author(1, "Test Author", LocalDate.of(1991, 1, 1))
        val author2 = Author(2, "Test Author", LocalDate.of(1992, 2, 2))
        lenient().doReturn(listOf(author1, author2))
            .`when`(authorRepository)
            .getAuthorByName("Test Author")

        val res = service.getAuthorByName("Test Author")
        assertThat(res).hasSize(2)
    }

    @Test
    fun `createAuthor returns new author id`() {
        lenient().doReturn(10)
            .`when`(authorRepository)
            .createAuthor(10, "Test Author", LocalDate.of(1991, 1, 1))

        val res = service.createAuthor(10, "Test Author", LocalDate.of(1991, 1, 1))
        assertThat(res).isEqualTo(10)
        verify(authorRepository, times(1))
            .createAuthor(10, "Test Author", LocalDate.of(1991, 1, 1))
    }

    @Test
    fun `createAuthor throws exception when author not created`() {
        lenient().doReturn(null)
            .`when`(authorRepository)
            .createAuthor(10, "Test Author", LocalDate.of(1991, 1, 1))

        val e = Assertions.assertThrows(IllegalArgumentException::class.java) {
            service.createAuthor(10, "Test Author", LocalDate.of(1991, 1, 1))
        }
        assertThat(e.message).isEqualTo("Author not created.")
    }

    @Test
    fun `updateAuthor works correctly`() {
        val author = Author(1, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(author)
            .`when`(authorRepository)
            .getAuthorById(anyInt())

        service.updateAuthor(1, "Updated Author", LocalDate.of(1992, 2, 2))

        verify(authorRepository, times(1))
            .updateAuthor(1, "Updated Author", LocalDate.of(1992, 2, 2))
    }

    @Test
    fun `updateAuthor fails if author not found`() {
        lenient().doReturn(null)
            .`when`(authorRepository)
            .getAuthorById(anyInt())

        val e = Assertions.assertThrows(IllegalArgumentException::class.java) {
            service.updateAuthor(1, "Updated Author", LocalDate.of(1992, 2, 2))
        }
        assertThat(e.message).isEqualTo("Author not found.")
    }

    @Test
    fun `deleteAuthorById deletes existing author`() {
        val author = Author(1, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(author)
            .`when`(authorRepository)
            .getAuthorById(anyInt())

        service.deleteAuthorById(1)

        verify(authorRepository, times(1))
            .deleteAuthor(1)
    }

    @Test
    fun `deleteAuthorById fails when author not found`() {
        lenient().doReturn(null)
            .`when`(authorRepository)
            .getAuthorById(anyInt())

        val e = Assertions.assertThrows(IllegalArgumentException::class.java) {
            service.deleteAuthorById(1)
        }
        assertThat(e.message).isEqualTo("Author not found.")
    }
}