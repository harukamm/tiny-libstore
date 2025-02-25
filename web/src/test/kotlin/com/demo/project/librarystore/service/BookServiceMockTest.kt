package com.demo.project.librarystore.service

import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.entity.Book
import com.demo.project.librarystore.exception.IdAlreadyExistsException
import com.demo.project.librarystore.exception.ResourceNotFoundException
import com.demo.project.librarystore.repository.AuthorRepository
import com.demo.project.librarystore.repository.BookRepository
import org.apache.coyote.BadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("TEST")
@SpringBootTest
class BookServiceMockTest {
    private lateinit var bookRepository: BookRepository
    private lateinit var authorRepository: AuthorRepository
    private lateinit var service: BookService

    @BeforeEach
    fun setup() {
        bookRepository = mock(BookRepository::class.java)
        authorRepository = mock(AuthorRepository::class.java)
        service = BookService(bookRepository, authorRepository)
    }

    @Test
    fun `getBookById returns a book when found`() {
        val author = Author(1, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(Book(10, "Test Book", 100, true, listOf(author)))
            .`when`(bookRepository)
            .getBookById(10)

        val res = service.getBookByIdOrThrow(10)
        assertThat(res).isNotNull
        assertThat(res.title).isEqualTo("Test Book")
        assertThat(res.price).isEqualTo(100)
        assertThat(res.publishedStatus).isTrue()
        assertThat(res.authors).hasSize(1)
    }

    @Test
    fun `getBookById throws when book not found`() {
        lenient().doReturn(null)
            .`when`(bookRepository)
            .getBookById(anyInt())

        val e =
            Assertions.assertThrows(ResourceNotFoundException::class.java) {
                service.getBookByIdOrThrow(10)
            }

        assertThat(e.message).isEqualTo("Book not found.")
    }

    @Test
    fun `createBook returns new book id`() {
        lenient().doReturn(true)
            .`when`(authorRepository)
            .checkAllAuthorsExist(listOf(1))
        lenient().doReturn(100)
            .`when`(bookRepository)
            .createBook(100, "Test Book", 100, true, listOf(1))

        val res = service.createBook(100, "Test Book", 100, true, listOf(1))

        assertThat(res).isEqualTo(100)
    }

    @Test
    fun `createBook fails with empty author`() {
        val e =
            Assertions.assertThrows(BadRequestException::class.java) {
                service.createBook(100, "Test Book", 100, true, listOf())
            }

        assertThat(e.message).isEqualTo("Book must have at least one author.")
    }

    @Test
    fun `createBook fails if id is already taken`() {
        Author(1, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(true)
            .`when`(authorRepository)
            .checkAllAuthorsExist(listOf(1))
        lenient().doThrow(DuplicateKeyException::class.java)
            .`when`(bookRepository)
            .createBook(10, "Test Book", 100, true, listOf(1))

        val e =
            Assertions.assertThrows(IdAlreadyExistsException::class.java) {
                service.createBook(10, "Test Book", 100, true, listOf(1))
            }

        assertThat(e.message).isEqualTo("Book id already used.")
    }

    @Test
    fun `updateBook works correctly`() {
        val author = Author(1, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(Book(1, "Title", 50, false, listOf(author)))
            .`when`(bookRepository)
            .getBookById(anyInt())
        lenient().doReturn(true)
            .`when`(authorRepository)
            .checkAllAuthorsExist(listOf(1))

        service.updateBook(1, "Updated Title", 200, false, listOf(1))

        verify(bookRepository, times(1))
            .updateBook(1, "Updated Title", 200, false, listOf(1))
    }

    @Test
    fun `updateBook fails if book not found`() {
        lenient().doReturn(null)
            .`when`(bookRepository)
            .getBookById(1)
        lenient().doReturn(true)
            .`when`(authorRepository)
            .checkAllAuthorsExist(listOf(1))

        val e =
            Assertions.assertThrows(ResourceNotFoundException::class.java) {
                service.updateBook(1, "Updated Title", 200, false, listOf(1))
            }

        assertThat(e.message).isEqualTo("Book not found.")
    }

    @Test
    fun `updateBook fails with empty author`() {
        val author = Author(2, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(Book(1, "Title", 50, false, listOf(author)))
            .`when`(bookRepository)
            .getBookById(1)

        val e =
            Assertions.assertThrows(BadRequestException::class.java) {
                service.updateBook(1, "Updated Title", 200, true, listOf())
            }

        assertThat(e.message).isEqualTo("Book must have at least one author.")
    }

    @Test
    fun `updateBook fails if turn off published status`() {
        val author = Author(2, "Test Author", LocalDate.of(1991, 1, 1))
        lenient().doReturn(Book(1, "Title", 50, true, listOf(author)))
            .`when`(bookRepository)
            .getBookById(1)

        val e =
            Assertions.assertThrows(BadRequestException::class.java) {
                service.updateBook(1, null, null, false, null)
            }

        assertThat(e.message).isEqualTo("Book cannot be changed to unpublished status.")
    }

    @Test
    fun `deleteBookById works correctly`() {
        lenient().doReturn(1)
            .`when`(bookRepository)
            .deleteBookById(123)

        service.deleteBookById(123)

        verify(bookRepository, times(1))
            .deleteBookById(123)
    }

    @Test
    fun `deleteBookById throws error if there's nothing to delete`() {
        lenient().doReturn(0)
            .`when`(bookRepository)
            .deleteBookById(123)

        Assertions.assertThrows(ResourceNotFoundException::class.java) {
            service.deleteBookById(123)
        }

        verify(bookRepository, times(1))
            .deleteBookById(123)
    }
}
