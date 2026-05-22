package com.chamo.chamomarket.repository;

import com.chamo.chamomarket.entity.employee.EmployeeEntity;
import com.chamo.chamomarket.entity.employee.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    boolean existsByDocument(String document);

    List<EmployeeEntity> findByRole(EmployeeRole role);

    List<EmployeeEntity> findByHireDateBetween(LocalDate start, LocalDate end);

    Optional<EmployeeEntity> findByUsername(String username);
}