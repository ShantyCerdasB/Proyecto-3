package com.project.demo.logic.entity.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.demo.logic.entity.category.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
