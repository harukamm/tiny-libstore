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
        val updateMap = mutableMapOf<Any, Any>()

        name?.let { updateMap[AUTHOR.NAME] = it }
        birthDate?.let { updateMap[AUTHOR.BIRTH_DAY] = it }

        if (updateMap.isNotEmpty()) {
            context.update(AUTHOR)
                .set(updateMap)
                .where(AUTHOR.ID.eq(id))
                .execute()
        }
    }

    fun deleteAuthor(id: Int) {
        // book-author entities are deleted by ON DELETE CASCADE
        context.deleteFrom(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .execute()
    }
}