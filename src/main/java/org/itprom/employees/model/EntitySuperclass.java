package org.itprom.employees.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class EntitySuperclass {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
}
