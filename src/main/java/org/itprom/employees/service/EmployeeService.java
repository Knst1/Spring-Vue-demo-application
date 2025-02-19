package org.itprom.employees.service;

import lombok.RequiredArgsConstructor;
import org.itprom.employees.dto.EmployeeRequestDto;
import org.itprom.employees.dto.EmployeeResponseDto;
import org.itprom.employees.dto.mapper.EmployeeMapper;
import org.itprom.employees.model.Employee;
import org.itprom.employees.repository.DepartmentRepository;
import org.itprom.employees.repository.EmployeeRepository;
import org.itprom.employees.repository.ProfessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeService {
	private final DepartmentRepository departmentRepository;
	private final EmployeeRepository employeeRepository;
	private final ProfessionRepository professionRepository;
	private final EmployeeMapper employeeMapper;


	public List<EmployeeResponseDto> findAllEmployees() {
		return employeeRepository.findAll().stream()
				.map(employeeMapper::toResponseDto)
				.toList();
	}

	private Employee getEmployeeById(Long id) {
		return employeeRepository
				.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Profession with id " + id + " not found"));
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto) {
		Employee employee = employeeMapper.toEntityWithoutProfessionAndDepartment(employeeRequestDto);
		employee.setProfession(professionRepository.findById(employeeRequestDto.profession())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Profession with id " + employeeRequestDto.profession() + " not found")));
		employee.setDepartment(departmentRepository.findById(employeeRequestDto.department())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Department with id " + employeeRequestDto.department() + " not found")));
		return employeeMapper.toResponseDto(employeeRepository.saveAndFlush(employee));
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
		Employee employee = getEmployeeById(id);
		employeeMapper.updateFromDto(employee, employeeRequestDto);
		employee.setProfession(professionRepository.findById(employeeRequestDto.profession())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Profession with id " + employeeRequestDto.profession() + " not found")));
		employee.setDepartment(departmentRepository.findById(employeeRequestDto.department())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Department with id " + employeeRequestDto.department() + " not found")));
		return employeeMapper.toResponseDto(employeeRepository.saveAndFlush(employee));
	}

	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id);
	}
}