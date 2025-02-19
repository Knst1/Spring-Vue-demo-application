package org.itprom.employees.repository;

import org.itprom.employees.dto.DepartmentNameResponseDto;
import org.itprom.employees.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	List<Department> findByParent(Department parent);

	@Query("SELECT new org.itprom.employees.dto.DepartmentNameResponseDto(d.id, d.name)	FROM Department d")
	List<DepartmentNameResponseDto> findAllNames();
}