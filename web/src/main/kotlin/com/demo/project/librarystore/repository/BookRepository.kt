package com.demo.project.librarystore.repository

import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.entity.Book
import com.demo.project.librarystore.jooq.generated.tables.daos.BookDao
import com.demo.project.librarystore.jooq.generated.tables.references.AUTHOR
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK_AUTHOR
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class BookRepository(
    private val context: DSLContext,
    private val bookDao: BookDao,
) {
    fun getBookById(id: Int): Book? {
        val record = context.select(
            BOOK.ID,
            BOOK.TITLE,
            BOOK.PRICE,
            BOOK.PUBLISHED_STATUS,
            multiset(
                context.select(AUTHOR.ID, AUTHOR.NAME, AUTHOR.BIRTH_DAY)
                    .from(AUTHOR)
                    .join(BOOK_AUTHOR).on(AUTHOR.ID.eq(BOOK_AUTHOR.AUTHOR_ID))
                    .where(BOOK_AUTHOR.BOOK_ID.eq(BOOK.ID))
            ).`as`("authors"))
            .from(BOOK)
            .where(BOOK.ID.eq(id))
            .fetch {
                val authors = it.into(AUTHOR).into(Author::class.java)
                val book = it.into(BOOK).into(Book::class.java)
                logger.debug("Book by id: {}", book)
                logger.debug("Book by id: {}", authors)
                return@fetch book
            }
        return record.firstOrNull()
    }

    fun getBooksByAuthorId(authorId: Int): List<Book> {
        val x = context.select(
            BOOK.ID,
            BOOK.TITLE,
            BOOK.PRICE,
            BOOK.PUBLISHED_STATUS,
            multiset(
                context.select(BOOK_AUTHOR.AUTHOR_ID)
                    .from(BOOK_AUTHOR)
                    .where(BOOK_AUTHOR.BOOK_ID.eq(BOOK.ID))
            ).`as`("authors")
        ).from(BOOK)
            .fetch()

        logger.debug("Books by author: {}", x)

        return listOf()
    }

    fun getBulkBooksById(ids: List<Int>): List<Book> {
        return context.dsl().select()
                .from(BOOK)
                .where(BOOK.ID.`in`(ids))
                .fetchInto(Book::class.java)
    }

    fun createBook(
        id: Int,
        title: String,
        price: Int,
        publishStatus: Boolean,
        authorIds: List<Int>,
    ): Int? {
        return context.transactionResult { trx ->
            val newId = trx.dsl().insertInto(BOOK)
                .set(BOOK.ID, id)
                .set(BOOK.TITLE, title)
                .set(BOOK.PRICE, price)
                .set(BOOK.PUBLISHED_STATUS, publishStatus)
                .returning(BOOK.ID)
                .fetch()
                .getValue(0, BOOK.ID)

            trx.dsl().insertInto(BOOK_AUTHOR)
                .columns(BOOK_AUTHOR.BOOK_ID, BOOK_AUTHOR.AUTHOR_ID)
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
                trx.dsl().deleteFrom(BOOK_AUTHOR)
                    .where(BOOK_AUTHOR.BOOK_ID.eq(id))
                    .execute()

                trx.dsl().insertInto(BOOK_AUTHOR)
                    .columns(BOOK_AUTHOR.BOOK_ID, BOOK_AUTHOR.AUTHOR_ID)
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
            trx.dsl().deleteFrom(BOOK_AUTHOR)
                .where(BOOK_AUTHOR.BOOK_ID.eq(id))
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