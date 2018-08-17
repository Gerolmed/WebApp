package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.components.HomeRestaurantForm;
import edu.hm.cs.projektstudium.findlunch.webapp.components.HomeToDoForm;

import java.util.List;

/**
 * Interface for services related to home services (TODO's of sales person).
 */

public interface HomeService {

    List<HomeRestaurantForm> createHomeRestaurantFormListForSalesPerson(String email);

    List<HomeToDoForm> getAllToDosForSalesPerson(String email);

    boolean toDoAssignedToSalePerson(int toDoId);

    void deleteToDo(int toDoId);

    String getCurrentPaymentOfSalesPerson(String email);
}
