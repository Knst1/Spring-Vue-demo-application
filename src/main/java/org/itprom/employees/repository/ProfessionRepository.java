package org.itprom.employees.repository;

import org.itprom.employees.dto.ProfessionNameResponseDto;
import org.itprom.employees.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {
	@Query("SELECT new org.itprom.employees.dto.ProfessionNameResponseDto(d.id, d.name)	FROM Profession d")
	List<ProfessionNameResponseDto> findAllNames();

}