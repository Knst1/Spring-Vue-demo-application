package org.itprom.employees.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee extends EntitySuperclass {
	@Column(nullable = false)
	@NotNull
	@Size(max = 255)
	private String name;

	@ManyToOne
	@JoinColumn(name = "profession_id", nullable = false)
	@NotNull
	private Profession profession;

	@ManyToOne
	@JoinColumn(name = "department_id", nullable = false)
	@NotNull
	private Department department;

	@Column(length = 5000)
	@Size(max = 5000)
	private String description;
}