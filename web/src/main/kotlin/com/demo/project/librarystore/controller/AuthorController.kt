package com.demo.project.librarystore.controller

import com.demo.project.librarystore.model.AuthorModel
import com.demo.project.librarystore.service.AuthorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController("Author_Controller_v1")
@RequestMapping("/lib-store/v1.0/")
@Tag(name = "Author", description = "Author API")
class AuthorController(
    private val authorService: AuthorService,
) {
    @Operation(
        summary = "Get all authors",
        description = "Get all authors."
    )
    @GetMapping("/authors")
    fun getAllAuthors(): List<AuthorModel> {
        return authorService.getAllAuthors()
    }

    @Operation(
        summary = "Get author by ID",
        description = "Get author by ID. Fails if the author does not exist."
    )
    @GetMapping("/authors/{authorId}")
    fun getAuthorById(@PathVariable authorId: Int): AuthorModel {
        return authorService.getAuthorByIdOrThrow(authorId)
    }

    @Operation(
        summary = "Create author",
        description = "Create a new author. Fails if the ID is already taken."
    )
    @PostMapping("/authors")
    fun createAuthor(@Valid @RequestBody request: CreateAuthorRequest): NewIdCreatedResponse {
        return NewIdCreatedResponse(authorService.createAuthor(request.id, request.name, request.birthDay))
    }

    @Operation(
        summary = "Update author",
        description = "Update author by ID. Fails if the author does not exist."
    )
    @PutMapping("/authors/{authorId}")
    fun updateAuthor(
        @PathVariable authorId: Int,
        @Valid @RequestBody request: UpdateAuthorRequest
    ) {
        return authorService.updateAuthor(authorId, request.name, request.birthDay)
    }

    @Operation(
        summary = "Delete author",
        description = "Delete author by ID. Fails if the author does not exist."
    )
    @DeleteMapping("/authors/{authorId}")
    fun deleteAuthor(@PathVariable authorId: Int) {
        authorService.deleteAuthorById(authorId)
    }
}