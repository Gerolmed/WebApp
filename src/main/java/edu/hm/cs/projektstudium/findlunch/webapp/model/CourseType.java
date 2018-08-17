package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.CourseTypeView;
/**
 * Describes different types of courses.
 */
@Entity
@Table(name="course_types")
@ApiModel(
		description = "Beschreibt die Art des Gangs."
)
@Getter
@Setter
public class CourseType {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView({CourseTypeView.CourseTypeRest.class})
	private int id;
	
	/** The name. */
	@ApiModelProperty(notes = "Name des Gangs")
	@Column(name="name")
	@JsonView({CourseTypeView.CourseTypeRest.class})
	@NotBlank(message="{courstype.name.notBlank}")
	@Size(min=2, max=60, message= "{coursetype.name.lengthInvalid}")
	private String name;

	@ApiModelProperty(notes = "Restaurant-ID")
	@Column(name="restaurant_id")
	@JsonView({CourseTypeView.CourseTypeRest.class})
	private int restaurantId;

	@ApiModelProperty(notes = "Sortierung")
	@Column(name="sort_by")
	@JsonView({CourseTypeView.CourseTypeRest.class})
	@NumberFormat(style = Style.NUMBER)
	@NotNull
	@Range(min=0, max=100, message= "{coursetype.sortby.Range}")
	private int sortBy;
	
	public CourseType(){ super(); }

	public CourseType(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		CourseType other = (CourseType) obj;
		if (Integer.valueOf(id) == null) {
			if (Integer.valueOf(other.id) != null)
				return false;
		}

		if (id != other.getId()) {
			return false;
		}

		if (!name.equals(other.getName())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Integer.valueOf(id) == null) ? 0 : (Integer.valueOf(id).hashCode()));

		return result;
	}
}
