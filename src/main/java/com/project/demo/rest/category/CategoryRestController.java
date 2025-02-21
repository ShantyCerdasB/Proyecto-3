package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.category.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Category entities.
 * <p>
 * This controller provides endpoints to list, retrieve, create, update, and delete categories.
 * GET operations are available to any authenticated user, whereas POST, PUT, PATCH, and DELETE
 * operations are restricted to users with the {@code SUPER_ADMIN_ROLE}.
 * </p>
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Retrieves a paginated list of all categories.
     *
     * @param page    the page number (default is 1)
     * @param size    the number of categories per page (default is 10)
     * @param request the current HTTP servlet request
     * @return a ResponseEntity containing the list of categories, pagination metadata, and HTTP status
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoryPage.getTotalPages());
        meta.setTotalElements(categoryPage.getTotalElements());
        meta.setPageNumber(categoryPage.getNumber() + 1);
        meta.setPageSize(categoryPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categories retrieved successfully",
                categoryPage.getContent(), HttpStatus.OK, meta);
    }

    /**
     * Retrieves a single category by its ID.
     *
     * @param id      the ID of the category to retrieve
     * @param request the current HTTP servlet request
     * @return a ResponseEntity containing the category if found or a NOT_FOUND status otherwise
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Category retrieved successfully",
                    category.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    /**
     * Creates a new category.
     * <p>
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param category the category object to be created
     * @param request  the current HTTP servlet request
     * @return a ResponseEntity containing the created category and HTTP status CREATED
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> createCategory(@RequestBody Category category, HttpServletRequest request) {
        Category savedCategory = categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse("Category created successfully",
                savedCategory, HttpStatus.CREATED, request);
    }

    /**
     * Fully updates an existing category identified by its ID.
     * <p>
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param id       the ID of the category to update
     * @param category the category object containing updated information
     * @param request  the current HTTP servlet request
     * @return a ResponseEntity containing the updated category if found, or a NOT_FOUND status otherwise
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            category.setId(existingCategory.get().getId());
            Category updatedCategory = categoryRepository.save(category);
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    updatedCategory, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    /**
     * Partially updates an existing category identified by its ID.
     * <p>
     * Only non-null fields in the provided category object will be updated.
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param id       the ID of the category to update
     * @param category the category object containing fields to be updated
     * @param request  the current HTTP servlet request
     * @return a ResponseEntity containing the updated category if found, or a NOT_FOUND status otherwise
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> patchCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            Category c = existingCategory.get();
            if (category.getName() != null) c.setName(category.getName());
            if (category.getDescription() != null) c.setDescription(category.getDescription());
            categoryRepository.save(c);
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    c, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    /**
     * Deletes a category identified by its ID.
     * <p>
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param id      the ID of the category to delete
     * @param request the current HTTP servlet request
     * @return a ResponseEntity confirming deletion if the category is found,
     *         or a NOT_FOUND status otherwise
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            categoryRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Category deleted successfully",
                    category.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
