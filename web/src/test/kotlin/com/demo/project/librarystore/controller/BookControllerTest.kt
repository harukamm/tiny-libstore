package com.demo.project.librarystore.controller

import com.demo.project.librarystore.exception.ResourceNotFoundException
import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.service.BookService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDate
import org.apache.coyote.BadRequestException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

@ActiveProfiles("TEST")
class BookControllerTest {
    private lateinit var bookService: BookService

    private lateinit var controller: BookController

    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        bookService = mock(BookService::class.java)
        controller = BookController(bookService)

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `should return all books`() {
        val author1 = AuthorModel(5, "Test Author 1", LocalDate.of(1991, 1, 1))
        val author2 = AuthorModel(10, "Test Author 2", LocalDate.of(1992, 2, 2))
        val book = BookModel(1, "Title1", 100, true, listOf(author1, author2))
        lenient().doReturn(book)
            .`when`(bookService)
            .getBookByIdOrThrow(1)

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
            .andExpect(jsonPath("$.authors[0].birthDay").value("Test Author 2"))
            .andExpect(jsonPath("$.authors[1].name").value("Test Author 1"))
            .andExpect(jsonPath("$.authors[1].birthDay").value("Test Author 2"))
    }

    @Test
    fun `book by id fails if book does not exist`() {
        lenient().doThrow(ResourceNotFoundException::class.java)
            .`when`(bookService)
            .getBookByIdOrThrow(1)

        mockMvc.perform(
            get("/lib-store/v1.0/books/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create a book and return new ID`() {
        val request = CreateBookRequest(1, "New Book", 150, true, listOf(1))
        val response = NewIdCreatedResponse(1)
        lenient().doReturn(1)
            .`when`(bookService)
            .createBook(1, "New Book", 150, true, listOf(1))

        mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(response.id))
    }

    @Test
    fun `create book fails with bad request`() {
        val request = CreateBookRequest(1, "New Book", 150, true, listOf(1))
        lenient().doThrow(BadRequestException::class.java)
            .`when`(bookService)
            .createBook(1, "New Book", 150, true, listOf(1))

        mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `create book fails with invalid parameters`() {
        val request = CreateBookRequest(1, "", -1, true, listOf(10))
        val res = mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        logger.info("result: {}", res.response.contentAsString)
    }

    @Test
    fun `create book fails with empty author`() {
        val request = CreateBookRequest(1, "Foo", 1, true, listOf())
        mockMvc.perform(
            post("/lib-store/v1.0/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `update book fails with invalid parameters`() {
        val request = UpdateBookRequest("", -1, true, listOf())
        mockMvc.perform(
            put("/lib-store/v1.0/books/{bookId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andReturn()
    }

    @Test
    fun `should delete book by ID`() {
        lenient().doThrow(ResourceNotFoundException::class.java)
            .`when`(bookService)
            .deleteBookById(1)

        mockMvc.perform(
            delete("/lib-store/v1.0/books/{bookId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookControllerTest::class.java)
    }
}