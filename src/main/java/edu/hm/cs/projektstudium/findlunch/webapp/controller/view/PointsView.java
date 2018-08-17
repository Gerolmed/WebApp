package edu.hm.cs.projektstudium.findlunch.webapp.controller.view;

/**
 * The class PointsView.
 *
 */
public class PointsView {

	/**
	 * The Interface PointsRest. If used, only the fields marked with the
	 * annotation "JsonView(PointsView.PointsRest.class)" will be returned
	 * when accessing a rest controller (GET).
	 */
	public interface PointsRest extends RestaurantView.RestaurantRest{} 
}
