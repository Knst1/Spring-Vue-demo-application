package org.itprom.employees.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "department")
public class Department extends EntitySuperclass {
	@Column(unique = true, nullable = false)
	@NotNull
	@Size(max = 255)
	private String name;

	@Column(length = 5000)
	@Size(max = 5000)
	private String description;

	@ManyToOne
	@JoinColumn(name = "parent_department_id")
	private Department parent;

	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Department> subDepartments;
}