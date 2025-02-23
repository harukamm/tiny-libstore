package com.demo.project.librarystore.repository

import com.demo.project.librarystore.entity.Author
import com.demo.project.librarystore.jooq.generated.tables.references.AUTHOR
import java.time.LocalDate
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(private val context: DSLContext) {
    fun getAllAuthors(): List<Author> {
        return context.selectFrom(AUTHOR)
            .fetchInto(Author::class.java)
    }

    fun getAuthorById(id: Int): Author? {
        return context.select()
            .from(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOne()
            ?.into(Author::class.java)
    }

    fun getAuthorByName(name: String): List<Author> {
        return context.selectFrom(AUTHOR)
            .where(AUTHOR.NAME.eq(name))
            .fetchInto(Author::class.java)
    }

    fun createAuthor(id: Int, name: String, birthDate: LocalDate): Int? {
        return context.insertInto(AUTHOR)
            .set(AUTHOR.ID, id)
            .set(AUTHOR.NAME, name)
            .set(AUTHOR.BIRTH_DAY, birthDate)
            .returning(AUTHOR.ID)
            .fetch()
            .getValue(0, AUTHOR.ID)
    }

    fun updateAuthor(id: Int, name: String?, birthDate: LocalDate?) {
        context.update(AUTHOR)
            .let { query ->
                name?.let { query.set(AUTHOR.NAME, it) }
                birthDate?.let { query.set(AUTHOR.BIRTH_DAY, it) }
            }
            ?.where(AUTHOR.ID.eq(id))
            ?.execute()
    }

    fun deleteAuthor(id: Int) {
        context.deleteFrom(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .execute()
    }
}