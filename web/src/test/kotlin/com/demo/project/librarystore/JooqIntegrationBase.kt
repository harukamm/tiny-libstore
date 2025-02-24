package com.demo.project.librarystore

import com.demo.project.librarystore.jooq.generated.tables.references.AUTHOR
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK_AUTHOR
import java.lang.RuntimeException
import java.time.LocalDate
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("TEST")
abstract class JooqIntegrationBase(private val context: DSLContext) {
    @AfterEach
    fun tearDown() {
        context.deleteFrom(BOOK).execute()
        context.deleteFrom(BOOK_AUTHOR).execute()
        context.deleteFrom(AUTHOR).execute()
    }

    protected fun createAuthor(authorId: Int, name: String, birthDay: LocalDate): Int {
        return context.insertInto(AUTHOR)
            .columns(AUTHOR.ID, AUTHOR.NAME, AUTHOR.BIRTH_DAY)
            .values(authorId, name, birthDay)
            .returningResult(AUTHOR.ID)
            .fetchOne()
            ?.getValue(AUTHOR.ID)
            ?: throw RuntimeException("Author not created")
    }

    protected fun createBook(id: Int, title: String, price: Int, publishStatus: Boolean): Int {
        return context.insertInto(BOOK)
            .columns(BOOK.ID, BOOK.TITLE, BOOK.PRICE, BOOK.PUBLISHED_STATUS)
            .values(id, title, price, publishStatus)
            .returningResult(BOOK.ID)
            .fetchOne()
            ?.getValue(BOOK.ID)
            ?: throw RuntimeException("Book not created")
    }

    protected fun createBookAuthor(bookId: Int, authorId: Int) {
        context.insertInto(BOOK_AUTHOR)
            .columns(BOOK_AUTHOR.BOOK_ID, BOOK_AUTHOR.AUTHOR_ID)
            .values(bookId, authorId)
            .execute()
    }

    protected fun getBookAuthorsRecordCount(bookId: Int): Int {
        return context.select(
            BOOK_AUTHOR.BOOK_ID,
            BOOK_AUTHOR.AUTHOR_ID
        )
            .from(BOOK_AUTHOR)
            .count()
    }
}