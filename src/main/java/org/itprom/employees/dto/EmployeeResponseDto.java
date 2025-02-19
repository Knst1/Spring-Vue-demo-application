package org.itprom.employees.dto;

/**
 * DTO for {@link org.itprom.employees.model.Employee}
 */
public record EmployeeResponseDto(Long id, String name, Long profession, Long department,
								  String description) {
}