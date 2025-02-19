package org.itprom.employees.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "profession")
public class Profession extends EntitySuperclass {
	@Column(unique = true, nullable = false)
	@NotNull
	@Size(max = 255)
	private String name;

	@Column(length = 5000)
	@Size(max = 5000)
	private String description;
}