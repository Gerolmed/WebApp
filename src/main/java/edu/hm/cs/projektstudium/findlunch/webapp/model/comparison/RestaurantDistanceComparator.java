package edu.hm.cs.projektstudium.findlunch.webapp.model.comparison;

import java.util.Comparator;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;

/**
 * Class to compare the distance between restaurants.
 */
public class RestaurantDistanceComparator implements Comparator<Restaurant> {


	/**
	 * Compares the service between two restaurants
	 * 
	 * @param restaurant1 the first restaurant
	 * @param restaurant2 the second restaurant
	 * @return the service between the two restaurants
	 */
	@Override
	public int compare(Restaurant restaurant1, Restaurant restaurant2) {
		return Double.compare(restaurant1.getDistance(), restaurant2.getDistance());
	}
}
