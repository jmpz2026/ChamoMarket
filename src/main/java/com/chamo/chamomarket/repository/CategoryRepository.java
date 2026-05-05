package com.chamo.chamomarket.repository;

import com.chamo.chamomarket.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    public boolean existsByName(String name);
    public List<CategoryEntity> findAllByStatusTrueOrderByNameAsc();
}
