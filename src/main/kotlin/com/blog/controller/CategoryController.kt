package com.blog.controller

import com.blog.controller.view.CategoryView
import com.blog.domain.Author
import com.blog.domain.CategoryId
import com.blog.service.CategoryService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/categories")
class CategoryController(
    val categoryService: CategoryService
) {

    @GetMapping
    fun getAllCategory(): Flux<CategoryView> {
        return categoryService.getAllCategories()
            .map { CategoryView.from(it) }
    }

    @PostMapping
    fun addNewCategory(@RequestBody categoryRequest: CategoryRequest, author: Author): Mono<CategoryView> {
        return categoryService.addNewCategory(categoryRequest, author)
            .map { CategoryView.from(it) }
    }

    @PostMapping("/categories")
    fun getCategory(@RequestBody categories: List<CategoryId>): Flux<CategoryView> {
        return categoryService.getAllCategories(categories).map { CategoryView.from(it) }
    }
}

data class CategoryRequest(val name: String, val parentCategory: CategoryId? = null)
