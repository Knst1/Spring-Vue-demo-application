package org.itprom.employees.controller;

import lombok.RequiredArgsConstructor;
import org.itprom.employees.dto.ProfessionNameResponseDto;
import org.itprom.employees.dto.ProfessionRequestDto;
import org.itprom.employees.dto.ProfessionResponseDto;
import org.itprom.employees.service.ProfessionService;
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
@RequestMapping("/api/v1/professions")
@RequiredArgsConstructor
public class ProfessionResource {

	private final ProfessionService professionService;

	@GetMapping
	public List<ProfessionResponseDto> getAllProfessions() {
		return professionService.getAllProfessions();
	}

	@GetMapping("/all_names")
	public List<ProfessionNameResponseDto> getAllNames() {
		return professionService.getAllNames();
	}

	@PostMapping
	public ProfessionResponseDto createProfession(@RequestBody ProfessionRequestDto professionRequestDto) {
		return professionService.createProfession(professionRequestDto);
	}

	@PatchMapping("/{id}")
	public ProfessionResponseDto updateProfession(@PathVariable Long id, @RequestBody ProfessionRequestDto professionRequestDto) {
		return professionService.updateProfession(id, professionRequestDto);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		professionService.deleteProfession(id);
	}
}
