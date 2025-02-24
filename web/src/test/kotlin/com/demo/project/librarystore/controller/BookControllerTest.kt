package com.demo.project.librarystore.controller

import com.demo.project.librarystore.JooqIntegrationBase
import com.demo.project.librarystore.config.ExceptionHandler
import com.demo.project.librarystore.service.AuthorService
import com.demo.project.librarystore.service.BookService
import java.time.LocalDate
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@ActiveProfiles("TEST")
class BookControllerTest(
    @Autowired private val controller: BookController,
    @Autowired private val bookService: BookService,
    @Autowired private val authorService: AuthorService,
    @Autowired private val dslContext: DSLContext,
) : JooqIntegrationBase(dslContext) {

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(ExceptionHandler())
            .build()
    }

    @Test
    fun `should return a book`() {
        authorService.createAuthor(5, "Test Author 1", LocalDate.of(1991, 1, 1))
        authorService.createAuthor(10, "Test Author 2", LocalDate.of(1992, 2, 2))
        bookService.createBook(1, "Title1", 100, true, listOf(5, 10))

        mockMvc.perform(
            get("/lib-store/v1.0/books/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Title1"))
            .andExpect(jsonPath("$.price").value(100))
            .andExpect(jsonPath("$.publishedStatus").value(true))
            .andExpect(jsonPath("$.authors.length()").value(2))
            .andExpect(jsonPath("$.authors[0].name").value("Test Author 1"))
            .andExpect(jsonPath("$.authors[0].birthDay").value("1991-01-01"))
            .andExpect(jsonPath("$.authors[1].name").value("Test Author 2"))
            .andExpect(jsonPath("$.authors[1].birthDay").value("1992-02-02"))
    }

    @Test
    fun `book by id fails if book does not exist`() {
        mockMvc.perform(
            get("/lib-store/v1.0/books/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create a book and return new ID`() {
        authorService.createAuthor(1, "Test Author", LocalDate.of(1991, 1, 1))

        mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\":1,\"title\":\"New Book\",\"price\":150,\"publishStatus\":true,\"authorIds\":[1]}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun `create book fails with minus price`() {
        val res = mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\":1,\"title\":\"New Book\",\"price\":-1,\"publishStatus\":true,\"authorIds\":[1]}")
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        logger.info("result: {}", res.response.contentAsString)
    }

    @Test
    fun `create book fails with empty author`() {
        mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\":1,\"title\":\"New Book\",\"price\":-1,\"publishStatus\":true,\"authorIds\":[]}")
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `create book fails with unknown author id`() {
        mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\":1,\"title\":\"New Book\",\"price\":150,\"publishStatus\":true,\"authorIds\":[99]}")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Includes non-existent author."))
    }

    @Test
    fun `update book fails with empty author id`() {
        mockMvc.perform(
            put("/lib-store/v1.0/books/{bookId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\":1,\"title\":\"x\",\"price\":1,\"publishStatus\":true,\"authorIds\":[]}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("authorIds: size must be between 1 and 50"))
            .andReturn()
    }

    @Test
    fun `update book fails with unknown author id`() {
        mockMvc.perform(
            put("/lib-store/v1.0/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"title\":\"New Book\",\"price\":150,\"publishStatus\":true,\"authorIds\":[99]}")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("Includes non-existent author."))
    }

    @Test
    fun `should delete book by ID`() {
        authorService.createAuthor(1, "Test Author", LocalDate.of(1991, 1, 1))
        bookService.createBook(1, "Title1", 100, true, listOf(1))

        mockMvc.perform(
            delete("/lib-store/v1.0/books/{bookId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/lib-store/v1.0/books/{bookId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookControllerTest::class.java)
    }
}