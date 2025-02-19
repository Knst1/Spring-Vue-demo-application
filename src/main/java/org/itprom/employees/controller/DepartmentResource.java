package org.itprom.employees.controller;

import lombok.RequiredArgsConstructor;
import org.itprom.employees.dto.DepartmentNameResponseDto;
import org.itprom.employees.dto.DepartmentRequestDto;
import org.itprom.employees.dto.DepartmentResponseDto;
import org.itprom.employees.dto.DepartmentWithSubDepartmentsResponseDto;
import org.itprom.employees.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentResource {

	private final DepartmentService departmentService;

	@GetMapping
	public List<DepartmentWithSubDepartmentsResponseDto> getDepartmentsWithoutParent() {
		return departmentService.getDepartmentsWithoutParent();
	}

	@GetMapping("/allowed_parents/{id}")
	public List<DepartmentNameResponseDto> getAllowedNames(@PathVariable Long id) {
		return departmentService.getAllowedNames(id);
	}

	@GetMapping("/all_names")
	public List<DepartmentNameResponseDto> getAllNames() {
		return departmentService.getAllNames();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DepartmentResponseDto createDepartment(@RequestBody DepartmentRequestDto departmentRequestDto) {
		return departmentService.createDepartment(departmentRequestDto);
	}

	@PatchMapping("/{id}")
	public DepartmentResponseDto updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequestDto departmentRequestDto) {
		return departmentService.updateDepartment(id, departmentRequestDto);
	}

	@DeleteMapping("/{id}")
	public void deleteDepartment(@PathVariable Long id) {
		departmentService.deleteDepartment(id);
	}
}
