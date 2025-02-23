package com.demo.project.librarystore.repository

import com.demo.project.librarystore.JooqIntegrationBase
import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK
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
        val id = context.insertInto(BOOK)
            .columns(BOOK.ID, BOOK.TITLE, BOOK.PRICE, BOOK.PUBLISHED_STATUS)
            .values(10, "Test Book", 100, true)
            .returningResult(BOOK.ID)
            .fetchOne()
            ?.getValue(BOOK.ID)

        logger.debug("Inserted book with id: $id")

        val book = repository.getBookById(10)
        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("Test Book")
        assertThat(book.price).isEqualTo(100)
        assertThat(book.publishedStatus).isTrue()
    }

    @Test
    fun `getBookById returns a book when found 2`() {
        val id = context.insertInto(BOOK)
            .columns(BOOK.ID, BOOK.TITLE, BOOK.PRICE, BOOK.PUBLISHED_STATUS)
            .values(10, "Test Book 2", 42, false)
            .returningResult(BOOK.ID)
            .fetchOne()
            ?.getValue(BOOK.ID)

        logger.debug("Inserted book with id: $id")

        val book = repository.getBookById(10)
        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("Test Book 2")
        assertThat(book.price).isEqualTo(42)
        assertThat(book.publishedStatus).isFalse()
    }

    @Test
    fun `get book and its authors by book id`() {
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
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BookRepositoryTest::class.java)
    }
}