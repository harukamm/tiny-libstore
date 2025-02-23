package com.demo.project.librarystore.repository

import com.demo.project.librarystore.entity.Book
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK
import com.demo.project.librarystore.jooq.generated.tables.references.BOOK_AUTHORS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val context: DSLContext) {
    fun getBookById(id: Int): Book? {
        return context.select()
            .from(BOOK)
            //.innerJoin(BOOK_AUTHORS).on(BOOK_AUTHORS.BOOK_ID.eq(BOOK.ID))
            .where(BOOK.ID.eq(id))
            .fetchOne()
            ?.into(Book::class.java)
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
        return context.selectFrom(BOOK)
            .where(BOOK.TITLE.eq(title))
            .fetchInto(Book::class.java)
    }

    fun getAllBooks(): List<Book> {
        return context.selectFrom(BOOK)
            .fetchInto(Book::class.java)
    }

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
                .set(BOOK.PUBLISHED_STATUS, if (publishStatus) 1 else 0)
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
                    publishStatus?.let { query.set(BOOK.PUBLISHED_STATUS, if (it) 1 else 0) }
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
}