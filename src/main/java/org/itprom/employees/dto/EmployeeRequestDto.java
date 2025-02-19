package org.itprom.employees.dto;

/**
 * DTO for {@link org.itprom.employees.model.Employee}
 */
public record EmployeeRequestDto(String name, Long profession, Long department, String description) {
}