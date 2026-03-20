package com.chamo.chamomarket.repository;

import com.chamo.chamomarket.entity.EmployeeEntity;
import com.chamo.chamomarket.entity.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    boolean existsByDocument(String document);

    List<EmployeeEntity> findByRole(EmployeeRole role);

    List<EmployeeEntity> findByHireDateBetween(LocalDate start, LocalDate end);
}