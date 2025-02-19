package org.itprom.employees.dto;

import java.util.Set;

/**
 * DTO for {@link org.itprom.employees.model.Department}
 */
public record DepartmentWithSubDepartmentsResponseDto(Long id, String name, String description, Long parent,
													  Set<DepartmentWithSubDepartmentsResponseDto> subDepartments) {
}