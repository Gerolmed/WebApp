package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.model.*;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.*;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the interface DbReaderService.
 */
@Service
public class DbReaderServiceImpl implements DbReaderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SalesPersonRepository salesPersonRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private RestaurantTypeRepository restaurantTypeRepository;

    @Autowired
    private KitchenTypeRepository restaurantKitchenTypeRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CourseTypeRepository courseTypeRepository;

    @Autowired
    private AdditiveRepository additiveRepository;

    @Autowired
    private AllergenicRepository allergenicRepository;

    @Autowired
    private OfferPhotoRepository offerPhotoRepository;

    @Autowired
    private DonationPerMonthRepository donationPerMonthRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public SalesPerson getSalesPersonByEmail(String email) {
        return salesPersonRepository.findByEmail(email);
    }

    @Override
    public SalesPerson getSalesPersonById(int id) {
        return salesPersonRepository.findById(id);
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public List<Restaurant> getAllRestaurantsOfSalesPerson(String email) {
        int salesPersonId = salesPersonRepository.findByEmail(email).getId();
        return restaurantRepository.findBySalesPersonId(salesPersonId);
    }

    @Override
    public Restaurant getRestaurantById(int id) {
        return restaurantRepository.findById(id);
    }

    @Override
    public List<ToDo> getAllToDosOfSalesPerson(String email) {
        return toDoRepository.findBySalesPersonEmail(email);
    }

    @Override
    public List<RestaurantType> getAllRestaurantTypes() {
        return restaurantTypeRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Restaurant getRestaurantByCustomerId(int id) {
        return restaurantRepository.findByCustomerId(id);
    }

    @Override
    public List<KitchenType> getAllKitchenTypes() {
        return restaurantKitchenTypeRepository.findAll();
    }

    @Override
    public List<Offer> getAllOffersOfRestaurant(int restaurantId) {

        List<Offer> offerList = offerRepository.findByRestaurant_idOrderByOrderAsc(restaurantId);
        List<Offer> offerListToReturn = new ArrayList<>();

        for(Offer offer : offerList) {
            if(offer.isChangeRequest() == false) {
                offerListToReturn.add(offer);
            }
        }

        return offerListToReturn;
    }

    @Override
    public List<Offer> getAllOffersOfRestaurant(int restaurantId, String courseTypeAsString) {
        List<CourseType> courseTypeList = courseTypeRepository.findByNameAndRestaurantId(courseTypeAsString, restaurantId);
        List<Offer> offerList = offerRepository.findByRestaurantIdAndCourseType(restaurantId, courseTypeList.get(0));
        List<Offer> offerListToReturn = new ArrayList<Offer>();

        for(Offer offer : offerList) {
            if(offer.isChangeRequest() == false) {
                offerListToReturn.add(offer);
            }
        }

        return offerListToReturn;
    }

    @Override
    public List<Offer> getAllOffersOfRestaurantAndCourseTypeNull(int restaurantId) {
        List<Offer> offerList = offerRepository.findByCourseTypeIsNullAndRestaurantId(restaurantId);
        List<Offer> offerListToReturn = new ArrayList<Offer>();

        for(Offer offer : offerList) {
            if(offer.isChangeRequest() == false) {
                offerListToReturn.add(offer);
            }
        }

        return offerListToReturn;
    }

    @Override
    public List<CourseType> getAllCourseTypesOfRestaurant(int restaurantId) {
        return courseTypeRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public Offer getOffer(int offerId) {
        return offerRepository.findById(offerId);
    }

    @Override
    public List<Additive> getAllAdditives() {
        return additiveRepository.findAll();
    }

    @Override
    public List<Allergenic> getAllAllergenic() {
        return allergenicRepository.findAll();
    }

    @Override
    public OfferPhoto getOfferPhoto(int offerPhotoId) {
        return offerPhotoRepository.findById(offerPhotoId);
    }

    @Override
    public ToDo getToDoById(int toDoId) {
        return toDoRepository.findById(toDoId);
    }

    @Override
    public List<Reservation> getAllOfferReservationsByRestaurantId(int restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public List<DonationPerMonth> getAllDonationsByRestaurantId(int restaurantId) {
        return donationPerMonthRepository.findByRestaurantId(restaurantId);
    }

}
