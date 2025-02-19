package org.itprom.employees.service;

import org.itprom.employees.dto.DepartmentNameResponseDto;
import org.itprom.employees.dto.DepartmentWithSubDepartmentsResponseDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.itprom.employees.dto.DepartmentRequestDto;
import org.itprom.employees.dto.DepartmentResponseDto;
import org.itprom.employees.dto.mapper.DepartmentMapper;
import org.itprom.employees.model.Department;
import org.itprom.employees.repository.DepartmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class DepartmentService {
	private final DepartmentRepository departmentRepository;
	private final DepartmentMapper departmentMapper;

	public List<DepartmentWithSubDepartmentsResponseDto> getDepartmentsWithoutParent() {
		return departmentRepository.findByParent(null).stream()
				.map(departmentMapper::toDepartmentWithSubDepartmentsResponseDto)
				.toList();
	}

	private Department getDepartmentById(Long id) {
		return departmentRepository
				.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Department with id " + id + " not found"));
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto) {
		Department department = departmentMapper.toEntityWithoutParent(departmentRequestDto);
		if (departmentRequestDto.parent() != null) {
			Department parent = getDepartmentById(departmentRequestDto.parent());
			if (isNotLoop(department, parent)) {
				department.setParent(parent);
			}
		}
		try {
			return departmentMapper.toResponseDto(departmentRepository.saveAndFlush(department));
		} catch (DataIntegrityViolationException e) {
			String name = departmentRequestDto.name();
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Department with name " + name + " already exists");
		}
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto) {
		Department department = getDepartmentById(id);
		departmentMapper.updateFromDto(department, departmentRequestDto);
		Department parent = null;
		if (departmentRequestDto.parent() != null) {
			parent = getDepartmentById(departmentRequestDto.parent());
		}
		if (isNotLoop(department, parent)) {
			department.setParent(parent);
		}
		try {
			return departmentMapper.toResponseDto(departmentRepository.saveAndFlush(department));
		} catch (DataIntegrityViolationException e) {
			String name = departmentRequestDto.name();
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Department with name " + name + " already exists");
		}
	}

	public List<DepartmentNameResponseDto> getAllNames() {
		return departmentRepository.findAllNames();
	}

	public List<DepartmentNameResponseDto> getAllowedNames(Long id) {
		Department department = getDepartmentById(id);
		Set<Long> notAllowedIds = getAllChildDepartmentIds(department);
		return departmentRepository.findAllNames().stream()
				.filter(dto -> !notAllowedIds.contains(dto.id()))
				.toList();
	}

	private Set<Long> getAllChildDepartmentIds(Department root) {
		Set<Long> notAllowedIds = new HashSet<>();
		Queue<Department> queue = new LinkedList<>();
		queue.add(root);
		notAllowedIds.add(root.getId());
		while (!queue.isEmpty()) {
			Department current = queue.poll();
			for (Department sub : current.getSubDepartments()) {
				notAllowedIds.add(sub.getId());
				queue.add(sub);
			}
		}
		return notAllowedIds;
	}

	private boolean isNotLoop(Department department, Department parent) {
		while (parent != null) {
			if (department.equals(parent)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Loop in department hierarchy");
			}
			parent = parent.getParent();
		}
		return true;
	}

	public void deleteDepartment(Long id) {
		try {
		departmentRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Department with id " + id + " has employees");
		}
	}
}
