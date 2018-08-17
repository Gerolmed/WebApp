package edu.hm.cs.projektstudium.findlunch.webapp.components;

/**
 * Add a new category to a restaurant.
 */
public class RestaurantAddCategory {

    private String name; //Name of the category
    private int restaurantId;

    public RestaurantAddCategory() {
        super();
    }

    public RestaurantAddCategory(int restaurantId) {
        this.name = null;
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

}
