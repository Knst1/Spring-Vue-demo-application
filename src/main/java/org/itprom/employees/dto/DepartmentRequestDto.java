package org.itprom.employees.dto;

/**
 * DTO for {@link org.itprom.employees.model.Department}
 */
public record DepartmentRequestDto(String name, String description, Long parent) {
}