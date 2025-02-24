package com.demo.project.librarystore.controller

import com.demo.project.librarystore.JooqIntegrationBase
import com.demo.project.librarystore.config.ExceptionHandler
import com.demo.project.librarystore.service.AuthorService
import java.time.LocalDate
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@ActiveProfiles("TEST")
class AuthorControllerTest(
    @Autowired private val controller: AuthorController,
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
    fun `should return author`() {
        authorService.createAuthor(1, "Test Author 1", LocalDate.of(1991, 1, 1))

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
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"Test Author\", \"birthDay\": \"1991-01-01\"}")
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
                .content("{\"id\": 123}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", Matchers.containsString("JSON property name due to missing")))
    }

    @Test
    fun `create author fails with invalid parameters`() {
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"\", \"birthDay\": \"1991\"}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error", Matchers.containsString("Text '1991' could not be parsed")))
    }

    @Test
    fun `create author fails with future date`() {
        mockMvc.perform(
            post("/lib-store/v1.0/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 123, \"name\": \"Test Author\", \"birthDay\": \"2991-01-01\"}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("pastDate: Only past dates are allowed."))
    }

    @Test
    fun `update author fails with blank title`() {
        mockMvc.perform(
            put("/lib-store/v1.0/authors/{authorId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\", \"birthDay\": \"1999-01-01\"}")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("name: must not be blank"))
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BookControllerTest::class.java)
    }
}