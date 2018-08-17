package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


/**
 * Wrapper class to have access to the reservation object.
 */
/*
 * Wrapper Klasse, da man sonst keinen zugriff auf die reservation Objekt hat
 * , da java.util.ArrayList nicht erkannt wird von Thymeleaf*/
@ApiModel(
		description = "Wrapper-Klasse, da man sonst keinen zugriff auf das Reservation-Objekt hat."
)
public class ReservationList {

	public ReservationList() { super(); }
	
	public ReservationList(ArrayList<Reservation> reservations) {
		this.reservations = reservations;
	}

	/** The list of reservations.*/
	@Getter
	@Setter
	private ArrayList<Reservation> reservations = new ArrayList<Reservation>();
}
