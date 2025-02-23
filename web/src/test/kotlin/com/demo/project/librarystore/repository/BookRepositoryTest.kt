package com.demo.project.librarystore.repository

import com.demo.project.librarystore.jooq.generated.tables.references.BOOK
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
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

    @Test
    fun `getBookById returns a book when found`() {
        val id = context.insertInto(BOOK, BOOK.TITLE, BOOK.PRICE, BOOK.PUBLISHED_STATUS)
            .values("Test Book", 100, 1)
            .returning(BOOK.ID)
            .fetchOne()
            ?.getValue(BOOK.ID)

        val book = repository.getBookById(id!!)
        assertThat(book).isNotNull
        assertThat(book!!.title).isEqualTo("Test Book")
    }
}