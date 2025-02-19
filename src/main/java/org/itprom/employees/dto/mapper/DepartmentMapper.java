package org.itprom.employees.dto.mapper;

import org.itprom.employees.dto.DepartmentRequestDto;
import org.itprom.employees.dto.DepartmentResponseDto;
import org.itprom.employees.dto.DepartmentWithSubDepartmentsResponseDto;
import org.itprom.employees.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DepartmentMapper {
	@Mapping(target = "parent", ignore = true)
	Department toEntityWithoutParent(DepartmentRequestDto departmentRequestDto);

	@Mapping(target = "parent", source = "parent.id")
	DepartmentResponseDto toResponseDto(Department department);

	@Mapping(target = "parent", ignore = true)
	void updateFromDto(@MappingTarget Department department, DepartmentRequestDto departmentRequestDto);

	@Mapping(target = "parent", source = "parent.id")
	DepartmentWithSubDepartmentsResponseDto toDepartmentWithSubDepartmentsResponseDto(Department department);

	default Set<DepartmentWithSubDepartmentsResponseDto> mapSubDepartments(Set<Department> subDepartments) {
		return subDepartments == null ? Set.of() : subDepartments.stream()
				.map(this::toDepartmentWithSubDepartmentsResponseDto)
				.collect(Collectors.toSet());
	}
}