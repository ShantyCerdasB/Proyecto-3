package com.project.demo.logic.entity.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.demo.logic.entity.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
