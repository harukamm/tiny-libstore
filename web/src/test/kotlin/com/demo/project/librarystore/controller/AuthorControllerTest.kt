package com.demo.project.librarystore.controller

import com.demo.project.librarystore.exception.ResourceNotFoundException
import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.model.BookModel
import com.demo.project.librarystore.service.AuthorService
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
class AuthorControllerTest {
    private lateinit var authorService: AuthorService

    private lateinit var controller: AuthorController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        authorService = mock(AuthorService::class.java)
        controller = AuthorController(authorService)

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `should return author`() {
        val author = AuthorModel(1, "Test Author 1", LocalDate.of(1991, 1, 1))
        lenient().doReturn(author)
            .`when`(authorService)
            .getAuthorByIdOrThrow(1)

        mockMvc.perform(
            get("/lib-store/v1.0/authors/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Author 1"))
            .andExpect(jsonPath("$.birthDay").value("1991-01-01"))
    }

    @Test
    fun `should create a author and return new ID`() {
        val response = NewIdCreatedResponse(1)
        lenient().doReturn(1)
            .`when`(authorService)
            .createAuthor(1, "Test Author", LocalDate.of(1991, 1, 1))

        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"Test Author\", \"birthDay\": \"1991-01-01\"}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(response.id))
    }

    @Test
    fun `create book fails with invalid parameters`() {
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"\", \"birthDay\": \"1991\"}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("foo"))
    }

    @Test
    fun `update book fails with invalid parameters`() {
        mockMvc.perform(
            post("/lib-store/v1.0/authors/{authorId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"abc\", \"birthDay\": \"abc\"}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("foo"))
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookControllerTest::class.java)
    }
}