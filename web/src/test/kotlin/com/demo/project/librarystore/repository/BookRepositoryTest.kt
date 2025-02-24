package com.demo.project.librarystore.repository

import com.demo.project.librarystore.JooqIntegrationBase
import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.entity.Book
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class BookRepositoryTest(
    @Autowired private val repository: BookRepository,
    @Autowired private val context: DSLContext,
) : JooqIntegrationBase(context) {

    @Test
    fun `getBookById returns a book when found`() {
        val id = createBook(10, "Test Book", 100, true)
        logger.debug("Inserted book with id: $id")

        val book = repository.getBookById(10)

        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("Test Book")
        assertThat(book.price).isEqualTo(100)
        assertThat(book.publishedStatus).isTrue()
    }

    @Test
    fun `getBookById returns a book with multiple authors`() {
        val bookIdA = createBook(1, "Test Book A", 42, false)
        val bookIdB = createBook(2, "Test Book B", 42, false)
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        val authorId3 = createAuthor(3, "Test Author 3", LocalDate.of(1993, 3, 3))
        createBookAuthor(bookIdA, authorId1)
        createBookAuthor(bookIdA, authorId2)
        createBookAuthor(bookIdB, authorId3)

        val bookA = repository.getBookById(bookIdA)

        assertThat(bookA).isNotNull
        assertThat(bookA?.id).isEqualTo(1)
        assertThat(bookA?.authors).hasSize(2)
        assertThat(bookA?.authors?.get(0)).isEqualTo(
            Author(authorId1, "Test Author 1", LocalDate.of(1991, 1, 1)))
        assertThat(bookA?.authors?.get(1)).isEqualTo(
            Author(authorId2, "Test Author 2", LocalDate.of(1992, 2, 2)))

        val bookB = repository.getBookById(bookIdB)

        assertThat(bookB).isNotNull
        assertThat(bookB?.id).isEqualTo(2)
        assertThat(bookB?.authors).hasSize(1)
        assertThat(bookB?.authors?.get(0)).isEqualTo(
            Author(authorId3, "Test Author 3", LocalDate.of(1993, 3, 3)))
    }

    @Test
    fun `getBooksByAuthorId returns books by author`() {
        val bookId1 = createBook(1, "Test Book 1", 42, false)
        val bookId2 = createBook(2, "Test Book 2", 42, false)
        val authorId1 = createAuthor(1, "Test Author", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        createBookAuthor(bookId1, authorId1)
        createBookAuthor(bookId1, authorId2)
        createBookAuthor(bookId2, authorId1)

        val books = repository.getBooksByAuthorId(1)

        assertThat(books).hasSize(2)
        assertThat(books).contains(
            Book(1, "Test Book 1", 42, false,
                listOf(
                    Author(1, "Test Author", LocalDate.of(1991, 1, 1)),
                    Author(2, "Test Author 2", LocalDate.of(1992, 2, 2))
                ))
        )
        assertThat(books).contains(
            Book(2, "Test Book 2", 42, false, listOf(Author(1, "Test Author", LocalDate.of(1991, 1, 1))))
        )
    }

    @Test
    fun `createBook creates book successfully`() {
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))

        val bookId = repository.createBook(1, "New Book", 100, false, listOf(authorId1, authorId2))

        assertThat(bookId).isEqualTo(1)
        val book = repository.getBookById(1)
        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("New Book")
        assertThat(book.price).isEqualTo(100)
        assertThat(book.publishedStatus).isFalse()
        assertThat(book.authors).hasSize(2)
    }

    @Test
    fun `updateBook updates book successfully`() {
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        val bookId = createBook(1, "New Book", 100, true)
        createBookAuthor(bookId, authorId1)
        createBookAuthor(bookId, authorId2)

        repository.updateBook(1, "Updated Book", 200, false, null)

        val book = repository.getBookById(1)
        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("Updated Book")
        assertThat(book.price).isEqualTo(200)
        assertThat(book.publishedStatus).isFalse()
        assertThat(book.authors).hasSize(2)
    }

    @Test
    fun `updateBook updates only published status`() {
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val bookId = createBook(1, "New Book", 100, false)
        createBookAuthor(bookId, authorId1)

        repository.updateBook(1, null, null, true, null)

        val book = repository.getBookById(1)
        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("New Book")
        assertThat(book.price).isEqualTo(100)
        assertThat(book.publishedStatus).isTrue()
        assertThat(book.authors).hasSize(1)
    }

    @Test
    fun `updateBook updates book attributes and authors`() {
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        val authorId3 = createAuthor(3, "Test Author 3", LocalDate.of(1993, 3, 3))
        val bookId = createBook(1, "New Book", 100, true)
        createBookAuthor(bookId, authorId1)
        createBookAuthor(bookId, authorId2)

        repository.updateBook(1, "Updated Book", 200, false, listOf(authorId3))

        val book = repository.getBookById(1)
        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("Updated Book")
        assertThat(book.price).isEqualTo(200)
        assertThat(book.publishedStatus).isFalse()
    }

    @Test
    fun `deleteBookById deletes book successfully`() {
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        val bookId = createBook(1, "New Book", 100, true)
        createBookAuthor(bookId, authorId1)
        createBookAuthor(bookId, authorId2)

        repository.deleteBookById(bookId)

        val book = repository.getBookById(1)
        assertThat(book).isNull()
    }

    @Test
    fun `deleteBookById deletes book, book-author relations as well`() {
        val authorId1 = createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        val authorId2 = createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        val bookId = createBook(1, "New Book", 100, true)
        createBookAuthor(bookId, authorId1)
        createBookAuthor(bookId, authorId2)

        repository.deleteBookById(bookId)

        val exist = repository.getBookById(bookId)
        assertThat(exist).isNull()
        val authorsCountOfBook = getBookAuthorsRecordCount(bookId)
        assertThat(authorsCountOfBook).isEqualTo(0)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BookRepositoryTest::class.java)
    }
}