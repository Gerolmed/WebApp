package edu.hm.cs.projektstudium.findlunch.webapp.components;

/**
 * Class to delete a restaurant category.
 */
public class RestaurantDeleteCategory {

    private String name;
    private int restaurantId;

    public RestaurantDeleteCategory() {
        super();
    }

    public RestaurantDeleteCategory(int restaurantId) {
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
