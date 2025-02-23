package com.demo.project.librarystore.repository

import com.demo.project.librarystore.jooq.generated.tables.references.BOOK
import javax.sql.DataSource
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("TEST")
class BookRepositoryTest {

    @Autowired
    private lateinit var repository: BookRepository

    @Autowired
    private lateinit var context: DSLContext

    @AfterEach
    fun tearDown() {
        context.deleteFrom(BOOK).execute()
    }

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

    companion object {
        private val logger = LoggerFactory.getLogger(BookRepositoryTest::class.java)
    }
}