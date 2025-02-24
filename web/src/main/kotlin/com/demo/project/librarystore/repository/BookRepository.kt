package com.demo.project.librarystore.repository

import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.entity.Book
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
) {
    fun getBookById(id: Int): Book? {
        val record =
            context.select(
                BOOK.ID,
                BOOK.TITLE,
                BOOK.PRICE,
                BOOK.PUBLISHED_STATUS,
                multiset(
                    context.select(AUTHOR.ID, AUTHOR.NAME, AUTHOR.BIRTH_DAY)
                        .from(AUTHOR)
                        .join(BOOK_AUTHOR).on(AUTHOR.ID.eq(BOOK_AUTHOR.AUTHOR_ID))
                        .where(BOOK_AUTHOR.BOOK_ID.eq(BOOK.ID)),
                ).`as`("authors").convertFrom { r -> r.into(Author::class.java) },
            )
                .from(BOOK)
                .where(BOOK.ID.eq(id))
                .fetchInto(Book::class.java)
        return record.firstOrNull()
    }

    fun getBooksByAuthorId(authorId: Int): List<Book> {
        return context.select(
            BOOK.ID,
            BOOK.TITLE,
            BOOK.PRICE,
            BOOK.PUBLISHED_STATUS,
            multiset(
                context.select(AUTHOR.ID, AUTHOR.NAME, AUTHOR.BIRTH_DAY)
                    .from(AUTHOR)
                    .join(BOOK_AUTHOR).on(AUTHOR.ID.eq(BOOK_AUTHOR.AUTHOR_ID))
                    .where(BOOK_AUTHOR.BOOK_ID.eq(BOOK.ID)),
            ).`as`("authors").convertFrom { r -> r.into(Author::class.java) },
        )
            .from(BOOK)
            .where(
                BOOK.ID.`in`(
                    context.select(BOOK_AUTHOR.BOOK_ID)
                        .from(BOOK_AUTHOR)
                        .where(BOOK_AUTHOR.AUTHOR_ID.eq(authorId)),
                ),
            )
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
            val newId =
                trx.dsl().insertInto(BOOK)
                    .set(BOOK.ID, id)
                    .set(BOOK.TITLE, title)
                    .set(BOOK.PRICE, price)
                    .set(BOOK.PUBLISHED_STATUS, if (publishStatus) 1 else 0)
                    .returningResult(BOOK.ID)
                    .fetchOne()
                    ?.getValue(BOOK.ID)

            if (newId == null || newId != id) {
                logger.error("Book not created")
                return@transactionResult null
            }

            val queries =
                authorIds.map {
                    trx.dsl().insertInto(BOOK_AUTHOR)
                        .columns(BOOK_AUTHOR.BOOK_ID, BOOK_AUTHOR.AUTHOR_ID)
                        .values(newId, it)
                }
            trx.dsl().batch(queries).execute()

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

                val queries =
                    authorIds.map {
                        trx.dsl().insertInto(BOOK_AUTHOR)
                            .columns(BOOK_AUTHOR.BOOK_ID, BOOK_AUTHOR.AUTHOR_ID)
                            .values(id, it)
                    }
                trx.dsl().batch(queries).execute()
            }

            val updateMap = mutableMapOf<Any, Any>()
            title?.let { updateMap[BOOK.TITLE] = it }
            price?.let { updateMap[BOOK.PRICE] = it }
            publishStatus?.let { updateMap[BOOK.PUBLISHED_STATUS] = if (it) 1 else 0 }

            if (updateMap.isNotEmpty()) {
                trx.dsl().update(BOOK)
                    .set(updateMap)
                    .where(BOOK.ID.eq(id))
                    .execute()
            }
        }
    }

    fun deleteBookById(id: Int) {
        // book-author entities are deleted by ON DELETE CASCADE
        context.deleteFrom(BOOK)
            .where(BOOK.ID.eq(id))
            .execute()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BookRepository::class.java)
    }
}
