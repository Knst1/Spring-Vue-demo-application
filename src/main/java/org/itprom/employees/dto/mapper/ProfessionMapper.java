package org.itprom.employees.dto.mapper;

import org.itprom.employees.dto.ProfessionRequestDto;
import org.itprom.employees.dto.ProfessionResponseDto;
import org.itprom.employees.model.Profession;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfessionMapper {
	Profession toEntity(ProfessionRequestDto professionDto);

	ProfessionResponseDto toResponseDto(Profession profession);

	void updateFromDto(@MappingTarget Profession profession, ProfessionRequestDto professionRequestDto);
}