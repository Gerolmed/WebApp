package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * Includes a photo from a offer.
 */
@Entity
@Table(name="offer_photo")
@ApiModel(
		description = "Beinhaltet ein Foto zu einem Angebot."
)
@Getter
@Setter
public class OfferPhoto {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@JsonView(OfferView.OfferRest.class)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The photo. */
	@ApiModelProperty(notes = "Foto")
	@Lob
	@JsonView(OfferView.OfferPhotoFull.class)
	private byte[] photo;

	/** The offer. */
	@ApiModelProperty(notes = "Angebot")
	//bi-directional many-to-one association to Offer
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "offer_id")
	private Offer offer;
	
	/** The thumbnail. */
	@ApiModelProperty(notes = "Thumbnail")
	@Lob
	@JsonView(OfferView.OfferRest.class)
	private byte[] thumbnail;
	
	/** The base 64 encoded. */
	@ApiModelProperty(notes = "Base64-enkodiertes Bild.")
	@Transient
	@JsonIgnore
	private String base64Encoded;
	
	/** The image format. */
	@ApiModelProperty(notes = "Bildformat")
	@Transient
	@JsonIgnore
	private String imageFormat;

	/**
	 * Instantiates a new offer photo.
	 */
	public OfferPhoto() { super(); }
}