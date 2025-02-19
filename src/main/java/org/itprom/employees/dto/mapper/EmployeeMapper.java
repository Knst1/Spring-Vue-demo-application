package org.itprom.employees.dto.mapper;

import org.itprom.employees.dto.EmployeeRequestDto;
import org.itprom.employees.dto.EmployeeResponseDto;
import org.itprom.employees.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {
	@Mapping(target = "department", ignore = true)
	@Mapping(target = "profession", ignore = true)
	Employee toEntityWithoutProfessionAndDepartment(EmployeeRequestDto employeeRequestDto);

	@Mapping(target = "department", source = "department.id")
	@Mapping(target = "profession", source = "profession.id")
	EmployeeResponseDto toResponseDto(Employee employee);

	@Mapping(target = "department", ignore = true)
	@Mapping(target = "profession", ignore = true)
	void updateFromDto(@MappingTarget Employee employee, EmployeeRequestDto employeeRequestDto);
}