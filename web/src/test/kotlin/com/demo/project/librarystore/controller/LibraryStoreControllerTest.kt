package com.demo.project.librarystore.controller

import com.demo.project.librarystore.service.BookService
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@WebMvcTest(LibraryStoreController::class)
@ActiveProfiles("TEST")
class LibraryStoreControllerTest{

    private lateinit var authService: BookService
}