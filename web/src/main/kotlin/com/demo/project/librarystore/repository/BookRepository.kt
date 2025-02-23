package com.demo.project.librarystore.repository

import com.demo.project.librarystore.jooq.generated.tables.daos.BookDao
import com.demo.project.librarystore.jooq.generated.tables.pojos.Book
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK_AUTHORS
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class BookRepository(
    private val context: DSLContext,
    private val bookDao: BookDao,
) {
    fun getBookById(id: Int): Book? {
        return bookDao.findOptionalById(id).orElse(null)
    }

    fun getBooksByAuthorId(authorId: Int): List<Book> {
        // TODO:
        return context.select()
            .from(BOOK)
            .innerJoin(BOOK_AUTHORS).on(BOOK_AUTHORS.BOOK_ID.eq(BOOK.ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetchInto(Book::class.java)
    }

    fun getBookByTitle(title: String): List<Book> {
        return bookDao.fetchByTitle(title)
    }

    fun getAllBooks(): List<Book> {
        return bookDao.findAll()
    }

    @Transactional
    fun createBook(
        title: String,
        price: Int,
        publishStatus: Boolean,
        authorIds: List<Int>
    ): Int? {
        return context.transactionResult { trx ->
            val newId = trx.dsl().insertInto(BOOK)
                .set(BOOK.TITLE, title)
                .set(BOOK.PRICE, price)
                .set(BOOK.PUBLISHED_STATUS, publishStatus)
                .returning(BOOK.ID)
                .fetch()
                .getValue(0, BOOK.ID)

            trx.dsl().insertInto(BOOK_AUTHORS)
                .columns(BOOK_AUTHORS.BOOK_ID, BOOK_AUTHORS.AUTHOR_ID)
                .values(authorIds.map { newId to it })
                .execute()

            return@transactionResult newId
        }
    }

    fun updateBook(
        id: Int,
        title: String?,
        price: Int?,
        publishStatus: Boolean?,
        authorIds: List<Int>?,
    ) {
        context.transaction { trx ->
            authorIds?.let {
                trx.dsl().deleteFrom(BOOK_AUTHORS)
                    .where(BOOK_AUTHORS.BOOK_ID.eq(id))
                    .execute()

                trx.dsl().insertInto(BOOK_AUTHORS)
                    .columns(BOOK_AUTHORS.BOOK_ID, BOOK_AUTHORS.AUTHOR_ID)
                    .values(it.map { id to it })
                    .execute()
            }
            trx.dsl().update(BOOK)
                .let { query ->
                    title?.let { query.set(BOOK.TITLE, it) }
                    price?.let { query.set(BOOK.PRICE, it) }
                    publishStatus?.let { query.set(BOOK.PUBLISHED_STATUS, publishStatus) }
                }
                ?.where(BOOK.ID.eq(id))
                ?.execute()
        }
    }

    fun deleteBookById(id: Int) {
        context.transaction { trx ->
            trx.dsl().deleteFrom(BOOK_AUTHORS)
                .where(BOOK_AUTHORS.BOOK_ID.eq(id))
                .execute()

            trx.dsl().deleteFrom(BOOK)
                .where(BOOK.ID.eq(id))
                .execute()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BookRepository::class.java)
    }
}