package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines different kitchen types.
 */
@Entity
@Table(name="kitchen_type")
@ApiModel(
		description = "Definiert verschiedene Küchentypen."
)
@Getter
@Setter
public class KitchenType {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private int id;

	/** The name. */
	@ApiModelProperty(notes = "Name")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private String name;

	/** The restaurants. */
	@ApiModelProperty(notes = "Restaurants")
	//bi-directional many-to-many association to Restaurant
	@ManyToMany(fetch = FetchType.LAZY, mappedBy="kitchenTypes")
	@JsonIgnore
	private List<Restaurant> restaurants;

	/**
	 * Instantiates a new kitchen type.
	 */
	public KitchenType() { super(); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		KitchenType other = (KitchenType) obj;
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