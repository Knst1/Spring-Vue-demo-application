package org.itprom.employees.controller;

import lombok.RequiredArgsConstructor;
import org.itprom.employees.dto.EmployeeRequestDto;
import org.itprom.employees.dto.EmployeeResponseDto;
import org.itprom.employees.service.EmployeeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeResource {

	private final EmployeeService employeeService;

	@GetMapping
	public List<EmployeeResponseDto> getAllEmployees() {
		return employeeService.findAllEmployees();
	}

	@PostMapping
	public EmployeeResponseDto createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
		return employeeService.createEmployee(employeeRequestDto);
	}

	@PatchMapping("/{id}")
	public EmployeeResponseDto patch(@PathVariable Long id, @RequestBody EmployeeRequestDto employeeRequestDto) {
		return employeeService.updateEmployee(id, employeeRequestDto);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
	}
}
