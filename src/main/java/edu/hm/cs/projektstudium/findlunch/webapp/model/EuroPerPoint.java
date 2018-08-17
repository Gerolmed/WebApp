package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * Defines the value of a point in euro.
 */
@Entity
@Table(name="euro_per_point")
@ApiModel(
		description = "Definiert, wie viel ein Punkt wert ist."
)
@Getter
@Setter
public class EuroPerPoint {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The euro per point.*/
	@ApiModelProperty(notes = "Euro pro Punkt")
	@NumberFormat(style=Style.DEFAULT)
	private double euro;
	
	/** The reservations*/
	@ApiModelProperty(notes = "Reservierungen")
	@OneToMany(mappedBy="euroPerPoint")
	private List<Reservation> reservations;

	public EuroPerPoint() { super(); }
}
