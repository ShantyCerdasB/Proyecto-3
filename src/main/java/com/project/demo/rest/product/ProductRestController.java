package com.project.demo.rest.product;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.product.ProductRepository;
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
 * REST controller for managing Product entities.
 * <p>
 * This controller provides endpoints to list, retrieve, create, update, and delete products.
 * GET operations are available to any authenticated user, whereas POST, PUT, PATCH, and DELETE
 * operations are restricted to users with the {@code SUPER_ADMIN_ROLE}.
 * </p>
 */
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retrieves a paginated list of all products.
     *
     * @param page    the page number (default is 1)
     * @param size    the number of products per page (default is 10)
     * @param request the current HTTP servlet request
     * @return a ResponseEntity containing the list of products along with pagination metadata and HTTP status OK
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productPage.getTotalPages());
        meta.setTotalElements(productPage.getTotalElements());
        meta.setPageNumber(productPage.getNumber() + 1);
        meta.setPageSize(productPage.getSize());

        return new GlobalResponseHandler().handleResponse("Products retrieved successfully",
                productPage.getContent(), HttpStatus.OK, meta);
    }

    /**
     * Retrieves a single product by its ID.
     *
     * @param id      the ID of the product to retrieve
     * @param request the current HTTP servlet request
     * @return a ResponseEntity containing the product if found, or HTTP status NOT_FOUND if not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                    product.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    /**
     * Creates a new product.
     * <p>
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param product the product object to be created
     * @param request the current HTTP servlet request
     * @return a ResponseEntity containing the created product and HTTP status CREATED
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> createProduct(@RequestBody Product product, HttpServletRequest request) {
        Product savedProduct = productRepository.save(product);
        return new GlobalResponseHandler().handleResponse("Product created successfully",
                savedProduct, HttpStatus.CREATED, request);
    }

    /**
     * Fully updates an existing product identified by its ID.
     * <p>
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param id      the ID of the product to update
     * @param product the product object containing updated information
     * @param request the current HTTP servlet request
     * @return a ResponseEntity containing the updated product if found, or HTTP status NOT_FOUND if not found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            product.setId(existingProduct.get().getId());
            Product updatedProduct = productRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    updatedProduct, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    /**
     * Partially updates an existing product identified by its ID.
     * <p>
     * Only non-null fields in the provided product object will be updated.
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param id      the ID of the product to update
     * @param product the product object containing fields to be updated
     * @param request the current HTTP servlet request
     * @return a ResponseEntity containing the updated product if found, or HTTP status NOT_FOUND if not found
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> patchProduct(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product p = existingProduct.get();
            if (product.getName() != null) p.setName(product.getName());
            if (product.getDescription() != null) p.setDescription(product.getDescription());
            if (product.getPrice() != 0.0) p.setPrice(product.getPrice());
            if (product.getStockQuantity() != 0) p.setStockQuantity(product.getStockQuantity());
            if (product.getCategory() != null) p.setCategory(product.getCategory());
            productRepository.save(p);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    p, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    /**
     * Deletes a product identified by its ID.
     * <p>
     * This operation is restricted to users with the {@code SUPER_ADMIN_ROLE}.
     * </p>
     *
     * @param id      the ID of the product to delete
     * @param request the current HTTP servlet request
     * @return a ResponseEntity confirming deletion if the product is found, or HTTP status NOT_FOUND if not found
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN_ROLE')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Product deleted successfully",
                    product.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
