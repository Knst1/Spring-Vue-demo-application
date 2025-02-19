package org.itprom.employees.dto;

/**
 * DTO for {@link org.itprom.employees.model.Profession}
 */
public record ProfessionResponseDto(Long id, String name, String description) {
}