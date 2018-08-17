package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.components.HomeRestaurantForm;
import edu.hm.cs.projektstudium.findlunch.webapp.components.HomeToDoForm;
import edu.hm.cs.projektstudium.findlunch.webapp.model.*;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbWriterService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeMath.round;

/**
 * Implementation of the interface HomeService.
 */
@Service
public class HomeServiceImpl implements HomeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbReaderService dbReaderService;

    @Autowired
    private DbWriterService dbWriterService;

    @Override
    public List<HomeRestaurantForm> createHomeRestaurantFormListForSalesPerson(String email) {
        List<Restaurant> restaurantList = dbReaderService.getAllRestaurantsOfSalesPerson(email);
        List<HomeRestaurantForm> homeRestaurantFormList = new ArrayList<>();

        for (Restaurant restaurant : restaurantList) {
            homeRestaurantFormList.add(new HomeRestaurantForm(restaurant));
        }

        return homeRestaurantFormList;
    }

    @Override
    public List<HomeToDoForm> getAllToDosForSalesPerson(String email) {
        List<ToDo> toDos = dbReaderService.getAllToDosOfSalesPerson(email);
        List<HomeToDoForm> homeToDoForms = new ArrayList<>();

        for (ToDo toDo : toDos) {
            homeToDoForms.add(new HomeToDoForm(toDo));
        }

        return homeToDoForms;
    }

    @Override
    public boolean toDoAssignedToSalePerson(int toDoId) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String salesPersonOfToDo;

        try {
            salesPersonOfToDo = dbReaderService.getToDoById(toDoId).getSalesPerson().getEmail();
        } catch (NullPointerException e) {
            logger.debug("Error - User: " + loggedInUser + " - There is no toDo with the requested id: " + toDoId + " in the DB.");
            //The user will see the alert-danger box which tells him that he has no access to the restaurant with this (faulty) restaurantID.
            return false;
        }

        if(loggedInUser.equals(salesPersonOfToDo)) {
            return true;
        }

        logger.debug("Error - User: " + loggedInUser + " - Tryed to access a toDo (ToDoId: " + toDoId + ") which he is not assigned to.");
        return false;
    }

    @Override
    public void deleteToDo(int toDoId) {
        dbWriterService.deleteToDo(toDoId);
    }

    @Override
    public String getCurrentPaymentOfSalesPerson(String email) {
        SalesPerson salesPerson = dbReaderService.getSalesPersonByEmail(email);
        List<Restaurant> allRestaurantsOfSalesPerson = salesPerson.getRestaurants();

        double percentageOfDonation = salesPerson.getSalaryPercentage();
        List<DonationPerMonth> donationsOfAllMonths = new ArrayList<>();
        List<Reservation> reservationsOfAllMonths = new ArrayList<>();
        List<DonationPerMonth> donationsOfCurrentMonth = new ArrayList<>();
        List<Reservation> reservationsOfCurrentMonth = new ArrayList<>();
        double donationOfRestaurants = 0;
        double donationOfCustomers = 0;
        double currentPaymentOfSalesPerson = 0;

        for(Restaurant restaurant : allRestaurantsOfSalesPerson) {
            donationsOfAllMonths.addAll(dbReaderService.getAllDonationsByRestaurantId(restaurant.getId()));
            reservationsOfAllMonths.addAll(dbReaderService.getAllOfferReservationsByRestaurantId(restaurant.getId()));
        }

        //int month = calendar.get(Calendar.MONTH); //returns 8 for September

        //Sorting out of the ones which do not belong to the current month
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        Calendar loopDate = Calendar.getInstance();

        for(DonationPerMonth donationPerMonth : donationsOfAllMonths) {
            loopDate.setTime(donationPerMonth.getDate());

            if(loopDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
                donationsOfCurrentMonth.add(donationPerMonth);
            }
        }

        for(Reservation reservation : reservationsOfAllMonths) {
            loopDate.setTime(reservation.getCollectTime());

            if(loopDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
                reservationsOfCurrentMonth.add(reservation);
            }
        }

        //Adding the donations together
        for(DonationPerMonth donationPerMonth : donationsOfCurrentMonth) {
            donationOfRestaurants = donationOfRestaurants + donationPerMonth.getAmount();
        }

        for(Reservation reservation : reservationsOfCurrentMonth) {
            if(reservation.isConfirmed()) {
                donationOfCustomers = donationOfCustomers + reservation.getDonation();
            }
        }

        //Multiply with the sales persons percentage
        currentPaymentOfSalesPerson = (donationOfRestaurants + donationOfCustomers) * percentageOfDonation;
        currentPaymentOfSalesPerson = round(0,currentPaymentOfSalesPerson * 100) / 100;

        return "€ " + currentPaymentOfSalesPerson;
    }

}
