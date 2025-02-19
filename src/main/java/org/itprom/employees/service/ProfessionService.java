package org.itprom.employees.service;

import lombok.RequiredArgsConstructor;
import org.itprom.employees.dto.ProfessionNameResponseDto;
import org.itprom.employees.dto.ProfessionRequestDto;
import org.itprom.employees.dto.ProfessionResponseDto;
import org.itprom.employees.dto.mapper.ProfessionMapper;
import org.itprom.employees.model.Profession;
import org.itprom.employees.repository.ProfessionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProfessionService {
	private final ProfessionRepository professionRepository;
	private final ProfessionMapper professionMapper;

	public List<ProfessionResponseDto> getAllProfessions() {
		return professionRepository.findAll().stream()
				.map(professionMapper::toResponseDto)
				.toList();
	}

	private Profession getProfessionById(Long id) {
		return professionRepository
				.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Profession with id " + id + " not found"));
	}

	public List<ProfessionNameResponseDto> getAllNames() {
		return professionRepository.findAllNames();
	}

	public ProfessionResponseDto createProfession(ProfessionRequestDto professionRequestDto) {
		Profession profession = professionMapper.toEntity(professionRequestDto);
		try {
			return professionMapper.toResponseDto(professionRepository.saveAndFlush(profession));
		} catch (DataIntegrityViolationException e) {
			String name = professionRequestDto.name();
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Profession with name " + name + " already exists");
		}
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public ProfessionResponseDto updateProfession(Long id, ProfessionRequestDto professionRequestDto) {
		Profession profession = getProfessionById(id);
		professionMapper.updateFromDto(profession, professionRequestDto);
		try {
			return professionMapper.toResponseDto(professionRepository.saveAndFlush(profession));
		} catch (DataIntegrityViolationException e) {
			String name = professionRequestDto.name();
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Profession with name " + name + " already exists");
		}
	}

	public void deleteProfession(Long id) {
		try {
			professionRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Profession with id " + id + " as it is referenced by employees");
		}

	}
}
