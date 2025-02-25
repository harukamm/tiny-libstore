package com.demo.project.librarystore.controller

import com.demo.project.librarystore.JooqIntegrationBase
import com.demo.project.librarystore.config.ExceptionHandler
import com.demo.project.librarystore.service.AuthorService
import com.demo.project.librarystore.service.BookService
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete

@SpringBootTest
@ActiveProfiles("TEST")
class AuthorControllerTest(
    @Autowired private val controller: AuthorController,
    @Autowired private val authorService: AuthorService,
    @Autowired private val bookService: BookService,
    @Autowired private val dslContext: DSLContext,
) : JooqIntegrationBase(dslContext) {
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc =
            MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(ExceptionHandler())
                .build()
    }

    @Test
    fun `should return author`() {
        authorService.createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))

        mockMvc.perform(
            get("/lib-store/v1.0/authors/1")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Author 1"))
            .andExpect(jsonPath("$.birthDay").value("1991-01-01"))
    }

    @Test
    fun `should create a author and return new ID`() {
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"Test Author\", \"birthDay\": \"1991-01-01\"}"),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(123))
    }

    @Test
    fun `create author fails with missing parameters`() {
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123}"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", Matchers.containsString("JSON property name due to missing")))
    }

    @Test
    fun `create author fails with invalid parameters`() {
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"\", \"birthDay\": \"1991\"}"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", Matchers.containsString("Text '1991' could not be parsed")))
    }

    @Test
    fun `create author fails if set birth day today`() {
        val today = LocalDate.now()
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"Test Author\", \"birthDay\": \"${today}\"}"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("pastDate: Only past dates are allowed."))
    }

    @Test
    fun `create author fails if the id is already in use`() {
        authorService.createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))

        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"name\": \"Test Author\", \"birthDay\": \"1991-01-01\"}"),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error").value("Author id already used."))
    }

    @Test
    fun `should updates author birthday`() {
        authorService.createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))

        mockMvc.perform(
            put("/lib-store/v1.0/authors/{authorId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"birthDay\": \"1999-12-31\"}"),
        )
            .andExpect(status().isOk)

        val author = authorService.getAuthorByIdOrThrow(1)
        assertThat(author.id).isEqualTo(1)
        assertThat(author.name).isEqualTo("Test Author 1")
        assertThat(author.birthDay).isEqualTo(LocalDate.of(1999, 12, 31))
    }

    @Test
    fun `update author fails with blank title`() {
        mockMvc.perform(
            put("/lib-store/v1.0/authors/{authorId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\", \"birthDay\": \"1999-01-01\"}"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("name: size must be between 1 and 200"))
    }

    @Test
    fun `delete author fails with unknown author id`() {
        mockMvc.perform(
            delete("/lib-store/v1.0/authors/999")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Author not found."))
    }

    @Test
    fun `delete author only if no associated books`() {
        authorService.createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        authorService.createAuthor(2, "Test Author 2", LocalDate.of(1992, 2, 2))
        bookService.createBook(1, "Title1", 100, true, listOf(1))

        mockMvc.perform(
            delete("/lib-store/v1.0/authors/1")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isForbidden)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Author has associated books."))

        mockMvc.perform(
            delete("/lib-store/v1.0/authors/2")
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookControllerTest::class.java)
    }
}
