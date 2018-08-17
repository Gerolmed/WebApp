package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import edu.hm.cs.projektstudium.findlunch.webapp.components.ProfileForm;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantTimeContainer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.*;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.*;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbWriterService;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the interface DbWriterService.
 */
@Service
public class DbWriterServiceImpl implements DbWriterService {

    @Autowired
    private DbReaderService dbReaderService;

    @Autowired
    private SalesPersonRepository salesPersonRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CourseTypeRepository courseTypeRepository;

    @Autowired
    private KitchenTypeRepository kitchenTypeRepository;

    @Autowired
    private RestaurantTypeRepository typeRepository;

    @Autowired
    private DayOfWeekRepository dayOfWeekRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OfferHasAdditivesRepository offerHasAdditivesRepository;

    @Autowired
    private OfferHasAllergenicRepository offerHasAllergenicRepository;

    @Autowired
    private OfferPhotoRepository offerPhotoRepository;

    @Autowired
    private AdditiveRepository additiveRepository;

    @Autowired
    private AllergenicRepository allergenicRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void setNewPassword(String userEmail, String encodedPassword) {
        SalesPerson salesPerson = salesPersonRepository.findByEmail(userEmail);
        salesPerson.setPassword(encodedPassword);
        salesPersonRepository.saveAndFlush(salesPerson);
    }

    @Override
    @Transactional
    public void setNewPassword(Integer userId, String encodedPassword) {
        SalesPerson salesPerson = salesPersonRepository.findById(userId);
        salesPerson.setPassword(encodedPassword);
        salesPersonRepository.saveAndFlush(salesPerson);
    }

    @Override
    @Transactional
    public void saveProfileChange(ProfileForm profileForm) {
        SalesPerson salesPerson = salesPersonRepository.findById(profileForm.getId());
        salesPerson.setFirstName(profileForm.getFirstName());
        salesPerson.setSecondName(profileForm.getSecondName());
        salesPerson.setStreet(profileForm.getStreet());
        salesPerson.setStreetNumber(profileForm.getStreetNumber());
        salesPerson.setZip(profileForm.getZip());
        salesPerson.setCity(profileForm.getCity());
        salesPerson.setPhone(profileForm.getPhone());

        Country country = new Country();
        country.setCountryCode(profileForm.getCountry().getCountryCode());
        country.setName(profileForm.getCountry().getName());
        salesPerson.setCountry(country);

        salesPerson.setEmail(profileForm.getEmail());
        salesPerson.setIban(profileForm.getIban());
        salesPerson.setBic(profileForm.getBic());

        salesPersonRepository.saveAndFlush(salesPerson);
        logger.debug("SalesPerson`s (Sales-Person ID: " + profileForm.getId() +") profile has been saved.");
    }

    @Override
    @Transactional
    public void addCategoryToRestaurant(List courseTypes, int restaurantId) {
        List<String> courseTypesToAdd = courseTypes;
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        List<CourseType> courseTypesOfRestaurant = restaurant.getCourseTypeList();

        for (String courseTypeAsString : courseTypesToAdd) {
                CourseType courseType = new CourseType();
                courseType.setName(courseTypeAsString);
                courseType.setRestaurantId(restaurant.getId());

                courseTypesOfRestaurant.add(courseType);
        }
        restaurant.setCourseTypeList(courseTypesOfRestaurant);

        restaurantRepository.saveAndFlush(restaurant);
    }

    @Override
    @Transactional
    public void deleteCategoryFromRestaurant(CourseType courseType) {
        courseTypeRepository.deleteById(courseType.getId());
    }

    @Override
    @Transactional
    public void saveRestaurant(Restaurant restaurantData) {
        int restaurantId = restaurantData.getId();
        Restaurant restaurantToSave;

        if (restaurantId == 0) {
            restaurantToSave = new Restaurant();
            restaurantToSave.setSalesPerson(salesPersonRepository.findById(restaurantData.getIdOfSalesPerson()));
        } else {
            restaurantToSave = restaurantRepository.findById(restaurantId);
        }

        restaurantToSave.setCustomerId(restaurantData.getCustomerId());
        restaurantToSave.setName(restaurantData.getName());
        restaurantToSave.setStreet(restaurantData.getStreet());
        restaurantToSave.setStreetNumber(restaurantData.getStreetNumber());
        restaurantToSave.setZip(restaurantData.getZip());
        restaurantToSave.setCity(restaurantData.getCity());
        restaurantToSave.setCountry(restaurantData.getCountry());
        restaurantToSave.setEmail(restaurantData.getEmail());
        restaurantToSave.setPhone(restaurantData.getPhone());
        restaurantToSave.setUrl(restaurantData.getUrl());
        restaurantToSave.setRestaurantUuid(restaurantData.getRestaurantUuid());
        restaurantToSave.setQrUuid(restaurantData.getQrUuid());
        restaurantToSave.setOfferModifyPermission(restaurantData.isOfferModifyPermission());
        restaurantToSave.setBlocked(restaurantData.isBlocked());

        //Sets the restaurant type
        RestaurantType restaurantType = typeRepository.findByName(restaurantData.getRestaurantTypeAsString());
        restaurantToSave.setRestaurantType(restaurantType);

        //Sets the kitchen types
        List<KitchenType> restaurantKitchenTypes = new ArrayList<>();

        for (String kitchenTypeName : restaurantData.getKitchenTypesAsString()) {
            restaurantKitchenTypes.add(kitchenTypeRepository.findByName(kitchenTypeName).get(0));
        }
        restaurantToSave.setKitchenTypes(restaurantKitchenTypes);

        //Sets coordinates
        restaurantToSave = getCoordinates(restaurantData, restaurantToSave);

        //Offer/Opening times
        List<TimeSchedule> timeSchedules = new ArrayList<>(getTimeScheduleList(restaurantData, restaurantToSave));
        // Setzen der Liste als neue Liste, um CondurrentModificationException zu vermeiden.
        restaurantToSave.setTimeSchedules(new ArrayList<>());
        // Direktes Setzen der Liste führt dazu, dass die Liste im Resataurant leer wird.
        for (TimeSchedule t : timeSchedules) {
            restaurantToSave.addTimeSchedule(t);
        }

        restaurantRepository.saveAndFlush(restaurantToSave);

        if(restaurantId == 0) {
            logger.debug("Restaurant (Customer-ID: " + restaurantToSave.getCustomerId() + ") has been added.");
        } else {
            logger.debug("Restaurant (Restaurant-ID: " + restaurantId + ") has been saved.");
        }
    }

    //Part of saveRestaurant
    private Restaurant getCoordinates (Restaurant restaurantData, Restaurant restaurantToSave)   {

        //Sets the coordinates with GoogleMaps if left empty
        if (restaurantData.getLocationLatitudeAsString() == "" || restaurantData.getLocationLongitudeAsString() == "") {
            //No Valid  Google API-Key for the google service
            //HashMap<String, Float> googleMapsLocationValues = getLocationOfRestaurant(restaurantData);
            HashMap<String, Float> googleMapsLocationValues = null;

            if(googleMapsLocationValues == null) {

                if(restaurantData.getLocationLatitudeAsString() == "") {
                    restaurantToSave.setLocationLatitude(new Float(0.0f));
                } else {
                    restaurantToSave.setLocationLatitude(Float.parseFloat(restaurantData.getLocationLatitudeAsString()));
                }

                if(restaurantData.getLocationLongitudeAsString() == "") {
                    restaurantToSave.setLocationLongitude(new Float(0.0f));
                } else {
                    restaurantToSave.setLocationLongitude(Float.parseFloat(restaurantData.getLocationLongitudeAsString()));
                }
            }

            if (googleMapsLocationValues != null && restaurantData.getLocationLongitudeAsString() == null) {
                restaurantToSave.setLocationLongitude(googleMapsLocationValues.get("locationLongitude"));
            }

            if (googleMapsLocationValues != null && restaurantData.getLocationLatitudeAsString() == null) {
                restaurantToSave.setLocationLatitude(googleMapsLocationValues.get("locationLatitude"));
            }
        } else {
            restaurantToSave.setLocationLatitude(Float.parseFloat(restaurantData.getLocationLatitudeAsString()));
            restaurantToSave.setLocationLongitude(Float.parseFloat(restaurantData.getLocationLongitudeAsString()));
        }
        return restaurantToSave;
    }

    //Part of saveRestaurant - Transfers the opening and offer times from the RestaurantTimeContainer`s back to the DB-Schema.
    private List<TimeSchedule> getTimeScheduleList(Restaurant restaurantData, Restaurant restaurantToSave) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        List<TimeSchedule> timeSchedulesToBeSaved = restaurantToSave.getTimeSchedules();
        if (timeSchedulesToBeSaved == null) {
            timeSchedulesToBeSaved = new ArrayList<>();

            for(int i = 0; i < 7; i++) {
                timeSchedulesToBeSaved.add(new TimeSchedule());
            }
        }

        for(int i = 1; i < 8; i++) {
            // The TimeContainers are ordered and the list.index reflects their day number.
            // Therefore we have to transfer this information to the variable dayNumber.
            RestaurantTimeContainer timeContainerOfferTimes = restaurantData.getOfferTimes().get(i-1);
            RestaurantTimeContainer timeContainerOpeningTimes = restaurantData.getOpeningTimes().get(i-1);
            DayOfWeek dayOfWeek = dayOfWeekRepository.findById(i);

            TimeSchedule timeSchedule = timeSchedulesToBeSaved.get(i-1);
            timeSchedule.setDayOfWeek(dayOfWeek);
            timeSchedule.setRestaurant(restaurantToSave);

            if(timeContainerOfferTimes.getStartTime() != "") {
                try {
                    timeSchedule.setOfferStartTime(sdf.parse(timeContainerOfferTimes.getStartTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                timeSchedule.setOfferStartTime(null);
            }

            if(timeContainerOfferTimes.getEndTime() != "") {
                try {
                    timeSchedule.setOfferEndTime(sdf.parse(timeContainerOfferTimes.getEndTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                timeSchedule.setOfferEndTime(null);
            }

            OpeningTime openingTime;
            try {
                openingTime = timeSchedule.getOpeningTimes().get(0);
            } catch (Exception e) {
                //no existing opening time
                openingTime = new OpeningTime();
            }

            if (timeContainerOpeningTimes.getStartTime() != "") {
                try {
                    openingTime.setOpeningTime(sdf.parse(timeContainerOpeningTimes.getStartTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                openingTime.setOpeningTime(null);
            }

            if (timeContainerOpeningTimes.getEndTime() != "") {
                try {
                    openingTime.setClosingTime(sdf.parse(timeContainerOpeningTimes.getEndTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                openingTime.setClosingTime(null);
            }


            openingTime.setTimeSchedule(timeSchedule);
            timeSchedule.addOpeningTime(openingTime);
        }
        return timeSchedulesToBeSaved;
    }

    //Not used because an activ/valid google ApiKey is needed.
    private HashMap<String, Float> getLocationOfRestaurant(Restaurant restaurantData) {

        // Replace the API key below with a valid API key.
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAvO9bl1Yi2hn7mkTSniv5lXaPRii1JxjI");
        GeocodingApiRequest req = GeocodingApi.newRequest(context).address(String.format("%1$s %2$s, %3$s %4$s", restaurantData.getStreetNumber(), restaurantData.getStreet(), restaurantData.getZip(), restaurantData.getCity()));
        HashMap<String, Float> googleMapsLocationValues = new HashMap<>();

        try {
            GeocodingResult[] result = req.await();
            if (result != null && result.length > 0) {
                // Handle successful request.
                GeocodingResult firstMatch = result[0];
                if (firstMatch.geometry != null && firstMatch.geometry.location != null) {
                    googleMapsLocationValues.put("locationLatitude", (float) firstMatch.geometry.location.lat);
                    googleMapsLocationValues.put("locationLongitude", (float) firstMatch.geometry.location.lng);
                } else {
                    logger.debug("Restaurant address for restaurantID: " + restaurantData.getId() + " could not be found be by GoogleMaps. Location Latitude/Longitude are not set by Google.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return googleMapsLocationValues;
    }

    @Override
    @Transactional
    public void deleteOffer(int offerId) {
        offerHasAllergenicRepository.deleteByOfferId(offerId);
        offerHasAdditivesRepository.deleteByOfferId(offerId);
        offerPhotoRepository.deleteByOfferId(offerId);
        offerRepository.deleteById(offerId);

        logger.debug("Offer (Offer-ID: " + offerId +") has been deleted.");
    }

    @Override
    @Transactional
    public void saveOffer(Offer offer, boolean isOfferChangeRequest) {
        int offerId = offer.getId();
        Offer offerToSave = new Offer();

        if (offerId == 0) {
            offerToSave.setId(0);
        } else {
            offerToSave.setId(offerId);
        }

        offerToSave.setRestaurant(restaurantRepository.findById(offer.getIdOfRestaurant()));

        offerToSave.setTitle(offer.getTitle());
        offerToSave.setDescription(offer.getDescription());
        offerToSave.setPrice(Double.valueOf(offer.getPriceAsString()));
        offerToSave.setPreparationTime(Integer.parseInt(offer.getPreparationTimeAsString()));
        offerToSave.setChangeRequestId(offer.getChangeRequestId());

        //Start- + Enddate
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            offerToSave.setStartDate(sdf.parse(offer.getStartDateAsString()));
            offerToSave.setEndDate(sdf.parse(offer.getEndDateAsString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        offerToSave.setNeededPoints(Integer.parseInt(offer.getNeededPointsAsString()));
        offerToSave.setCourseType(offer.getCourseType());

        if(!offer.getNewChangeComment().equals("")) {
            offerToSave.setCommentOfLastChange(offer.getNewChangeComment());
        } else {
            offerToSave.setCommentOfLastChange(offer.getCommentOfLastChange());
        }

        offerToSave.setSalesPerson(offerToSave.getRestaurant().getSalesPerson());

        List<String> additiveNames = offer.getAdditivesAsString();
        List<Additive> additives = additiveNames
                .stream().map(a -> additiveRepository.findByName(a))
                .collect(Collectors.toList());
        offerToSave.setAdditives(new ArrayList<>(additives));

        List<String> allergenNames = offer.getAllergenicsAsString();
        List<Allergenic> allergens = allergenNames
                .stream()
                .map(a -> allergenicRepository.findByName(a))
                .collect(Collectors.toList());
        offerToSave.setAllergenic(new ArrayList<>(allergens));

        offerToSave.setDayOfWeeks(offer.getDayOfWeeks());

        //Photos
        if(isOfferChangeRequest == false) {
            List<OfferPhoto> offerPhotos = null;
            try {
                offerPhotos = offerRepository.findById(offerId).getOfferPhotos();
            } catch (Exception e) {
                //Offer has no offerPhotos or the offer is new.
            }

            if(offerPhotos == null) {
                offerPhotos = new ArrayList<>();
            }

            if(offer.getFirstOfferImage() != null) {
                if(offer.getFirstOfferImage().getSize() > 0) {
                    OfferPhoto offerPhoto = new OfferPhoto();

                    try {
                        offerPhoto = offerPhotos.get(0);
                    } catch (Exception e) {
                        //firstOfferImage is null
                    }

                    try {
                        offerPhoto.setPhoto(offer.getFirstOfferImage().getBytes());
                        offerPhoto.setOffer(offerToSave);
                        offerPhoto.setThumbnail(createThumbnail(offer.getFirstOfferImage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    offerPhotos.add(offerPhoto);
                }
            }

            if(offer.getSecondOfferImage() != null) {
                if(offer.getSecondOfferImage().getSize() > 0) {
                    OfferPhoto offerPhoto = new OfferPhoto();

                    try {
                        offerPhoto = offerPhotos.get(1);
                    } catch (Exception e) {
                        //firstOfferImage is null
                    }

                    try {
                        offerPhoto.setPhoto(offer.getSecondOfferImage().getBytes());
                        offerPhoto.setOffer(offerToSave);
                        offerPhoto.setThumbnail(createThumbnail(offer.getSecondOfferImage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    offerPhotos.add(offerPhoto);
                }
            }

            if(offer.getThirdOfferImage() != null) {
                if(offer.getThirdOfferImage().getSize() > 0) {
                    OfferPhoto offerPhoto = new OfferPhoto();

                    try {
                        offerPhoto = offerPhotos.get(2);
                    } catch (Exception e) {
                        //firstOfferImage is null
                    }

                    try {
                        offerPhoto.setPhoto(offer.getThirdOfferImage().getBytes());
                        offerPhoto.setOffer(offerToSave);
                        offerPhoto.setThumbnail(createThumbnail(offer.getThirdOfferImage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    offerPhotos.add(offerPhoto);
                }
            }
            offerToSave.setOfferPhotos(offerPhotos);
        }

        if(isOfferChangeRequest == true) {
            offerToSave.setOfferPhotos(offer.getOfferPhotos());
        }

        offerRepository.saveAndFlush(offerToSave);

        if(offerId == 0) {
            logger.debug("Offer for restaurant (Customer-ID: " + offerToSave.getRestaurant().getCustomerId() + ") has been added.");
        } else {
            logger.debug("Offer (Offer-ID: " + offerId + ") has been saved.");
        }
    }

    @Override
    @Transactional
    public void deleteOfferPhoto(int offerPhotoId) {
        offerPhotoRepository.deleteById(offerPhotoId);
    }

    @Override
    @Transactional
    public void deleteToDo(int toDoId) {
        toDoRepository.deleteById(toDoId);
        logger.debug("ToDo (ToDo-ID: " + toDoId + ") has been deleted.");
    }

    @Override
    @Transactional
    public void deleteOfferChangeRequest(int offerToDeleteId, int offerToUpdateId, int toDoId) {
        //Sets the changeRequestId of the existing offer to 0 because the corresponding changeRequest gets deleted.
        Offer offerToUpdate = offerRepository.findById(offerToUpdateId);
        offerToUpdate.setChangeRequestId(0);
        offerRepository.save(offerToUpdate);

        toDoRepository.deleteById(toDoId);

        offerHasAllergenicRepository.deleteByOfferId(offerToDeleteId);
        offerHasAdditivesRepository.deleteByOfferId(offerToDeleteId);
        offerPhotoRepository.deleteByOfferId(offerToDeleteId);
        offerRepository.deleteById(offerToDeleteId);

        logger.debug("OfferChangeRequest (Offer-ID: " + offerToDeleteId + ") has been deleted.");
    }

    @Override
    @Transactional
    public void saveOfferChangeRequest(int offerChangeRequestId, Offer changedOffer, Offer existingOffer, int toDoId) {

        changedOffer.setOfferPhotos(null);
        List<OfferPhoto> originalOfferChangeRequestPictures = offerRepository.findById(offerChangeRequestId).getOfferPhotos(); //contains the images which were uploaded with the offerChangeRequest
        List<OfferPhoto> existingOfferPictures = existingOffer.getOfferPhotos();
        List<OfferPhoto> offerPhotosToAdd = new ArrayList<>();
        List<Integer> redundantOfferPhotosIds = new ArrayList<>();

        int numberOfOriginalOfferChangeRequestPictures = originalOfferChangeRequestPictures.size();
        int numberOfExistingPictures = existingOfferPictures.size();
        int numberOfNewUploadedPictures = 0;
        if(changedOffer.getFirstOfferImage().getSize() > 0) {
            numberOfNewUploadedPictures++;
        }
        if(changedOffer.getSecondOfferImage().getSize() > 0) {
            numberOfNewUploadedPictures++;
        }
        if(changedOffer.getThirdOfferImage().getSize() > 0) {
            numberOfNewUploadedPictures++;
        }

        //Orders the list of newly uploaded images
        long sizeOfFirstImage = changedOffer.getFirstOfferImage().getSize();
        long sizeOfSecondImage = changedOffer.getSecondOfferImage().getSize();

        boolean keepFirstImage = changedOffer.isKeepFirstImage();
        boolean keepSecondImage = changedOffer.isKeepSecondImage();
        boolean keepThirdImage = changedOffer.isKeepThirdImage();
        boolean tempKeepFlag;

        MultipartFile firstImage = changedOffer.getFirstOfferImage();
        MultipartFile secondImage = changedOffer.getSecondOfferImage();
        MultipartFile thirdImage = changedOffer.getThirdOfferImage();
        MultipartFile tempMultipartFile = null;

        if(sizeOfFirstImage == 0 && sizeOfSecondImage != 0) {
            tempMultipartFile = firstImage;
            firstImage = secondImage;
            secondImage = thirdImage;
            thirdImage = tempMultipartFile; //Is now filled with an empty first image.

            tempKeepFlag = keepFirstImage;
            keepFirstImage = keepSecondImage;
            keepSecondImage = keepThirdImage;
            keepThirdImage = tempKeepFlag;
        }

        if(sizeOfFirstImage == 0 && sizeOfSecondImage == 0) {
            tempMultipartFile = firstImage;
            firstImage = thirdImage;
            thirdImage = tempMultipartFile;

            tempKeepFlag = keepFirstImage;
            keepFirstImage = keepThirdImage;
            keepThirdImage = tempKeepFlag;
        }

        if(sizeOfFirstImage != 0 && sizeOfSecondImage == 0) {
            tempMultipartFile = secondImage;
            secondImage = thirdImage;
            thirdImage = tempMultipartFile;

            tempKeepFlag = keepSecondImage;
            keepSecondImage = keepThirdImage;
            keepThirdImage = tempKeepFlag;
        }

        changedOffer.setFirstOfferImage(firstImage);
        changedOffer.setSecondOfferImage(secondImage);
        changedOffer.setThirdOfferImage(thirdImage);

        changedOffer.setKeepFirstImage(keepFirstImage);
        changedOffer.setKeepSecondImage(keepSecondImage);
        changedOffer.setKeepThirdImage(keepThirdImage);

        //Manages the first offerChangeRequest Photo
        if(changedOffer.isKeepFirstImage() == false) {
            if (numberOfNewUploadedPictures > 0 && changedOffer.getFirstOfferImage().getSize() > 0) {
                OfferPhoto offerPhoto = new OfferPhoto();

                try {
                    offerPhoto.setOffer(changedOffer);
                    offerPhoto.setPhoto(changedOffer.getFirstOfferImage().getBytes());
                    offerPhoto.setThumbnail(createThumbnail(changedOffer.getFirstOfferImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                offerPhotosToAdd.add(offerPhoto);

                if(numberOfExistingPictures > 0) {
                    redundantOfferPhotosIds.add(existingOfferPictures.get(0).getId());
                }

                if(numberOfOriginalOfferChangeRequestPictures > 0) {
                    redundantOfferPhotosIds.add(originalOfferChangeRequestPictures.get(0).getId());
                }

            } else if (numberOfNewUploadedPictures <= 0 && numberOfOriginalOfferChangeRequestPictures > 0) {
                OfferPhoto offerPhoto = originalOfferChangeRequestPictures.get(0);
                offerPhoto.setOffer(changedOffer);
                offerPhotosToAdd.add(offerPhoto);

                if (numberOfExistingPictures > 0) {
                    redundantOfferPhotosIds.add(existingOfferPictures.get(0).getId());
                }
            } else if (numberOfExistingPictures > 0 && numberOfOriginalOfferChangeRequestPictures == 0) {
                redundantOfferPhotosIds.add(existingOfferPictures.get(0).getId());
            }
        }

        if(changedOffer.isKeepFirstImage() == true) {
            if (numberOfExistingPictures > 0) {
                changedOffer.setFirstOfferImage(null);

                OfferPhoto offerPhoto = new OfferPhoto();
                offerPhoto.setOffer(changedOffer);
                offerPhoto.setPhoto(existingOfferPictures.get(0).getPhoto());
                offerPhoto.setThumbnail(existingOfferPictures.get(0).getThumbnail());
                offerPhotosToAdd.add(offerPhoto);

                redundantOfferPhotosIds.add(existingOfferPictures.get(0).getId());
            }
        }

        //Manages the second offerChangeRequest Photo
        if(changedOffer.isKeepSecondImage() == false) {
            if(numberOfNewUploadedPictures > 1 && changedOffer.getSecondOfferImage().getSize() > 0) {
                OfferPhoto offerPhoto = new OfferPhoto();

                try {
                    offerPhoto.setOffer(changedOffer);
                    offerPhoto.setPhoto(changedOffer.getSecondOfferImage().getBytes());
                    offerPhoto.setThumbnail(createThumbnail(changedOffer.getSecondOfferImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                offerPhotosToAdd.add(offerPhoto);

                if(numberOfExistingPictures > 1) {
                    redundantOfferPhotosIds.add(existingOfferPictures.get(1).getId());
                }

                if(numberOfOriginalOfferChangeRequestPictures > 1) {
                    redundantOfferPhotosIds.add(originalOfferChangeRequestPictures.get(1).getId());
                }

            } else if(numberOfNewUploadedPictures <= 1 && numberOfOriginalOfferChangeRequestPictures > 1) {
                OfferPhoto offerPhoto = originalOfferChangeRequestPictures.get(1);
                offerPhoto.setOffer(changedOffer);
                offerPhotosToAdd.add(offerPhoto);

                if(numberOfExistingPictures > 1) {
                    redundantOfferPhotosIds.add(existingOfferPictures.get(1).getId());
                }
            } else if (numberOfExistingPictures > 1 && numberOfOriginalOfferChangeRequestPictures == 1) {
                redundantOfferPhotosIds.add(existingOfferPictures.get(1).getId());
            }
        }

        if(changedOffer.isKeepSecondImage() == true) {
            if(numberOfExistingPictures > 1){
                changedOffer.setSecondOfferImage(null);

                OfferPhoto offerPhoto = new OfferPhoto();
                offerPhoto.setOffer(changedOffer);
                offerPhoto.setPhoto(existingOfferPictures.get(1).getPhoto());
                offerPhoto.setThumbnail(existingOfferPictures.get(1).getThumbnail());
                offerPhotosToAdd.add(offerPhoto);

                redundantOfferPhotosIds.add(existingOfferPictures.get(1).getId());
            }
        }

        //Manages the third offerChangeRequest Photo
        if(changedOffer.isKeepThirdImage() == false) {
            if(numberOfNewUploadedPictures > 2 && changedOffer.getThirdOfferImage().getSize() > 0) {
                OfferPhoto offerPhoto = new OfferPhoto();

                try {
                    offerPhoto.setOffer(changedOffer);
                    offerPhoto.setPhoto(changedOffer.getThirdOfferImage().getBytes());
                    offerPhoto.setThumbnail(createThumbnail(changedOffer.getThirdOfferImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                offerPhotosToAdd.add(offerPhoto);

                if(numberOfExistingPictures > 2) {
                    redundantOfferPhotosIds.add(existingOfferPictures.get(2).getId());
                }

                if(numberOfOriginalOfferChangeRequestPictures > 2) {
                    redundantOfferPhotosIds.add(originalOfferChangeRequestPictures.get(2).getId());
                }

            } else if(numberOfNewUploadedPictures <= 2 && numberOfOriginalOfferChangeRequestPictures > 2) {
                OfferPhoto offerPhoto = originalOfferChangeRequestPictures.get(2);
                offerPhoto.setOffer(changedOffer);
                offerPhotosToAdd.add(offerPhoto);

                if(numberOfExistingPictures > 2) {
                    redundantOfferPhotosIds.add(existingOfferPictures.get(2).getId());
                }
            } else if (numberOfExistingPictures > 2 && numberOfOriginalOfferChangeRequestPictures == 2) {
                redundantOfferPhotosIds.add(existingOfferPictures.get(2).getId());
            }
        }

        if(changedOffer.isKeepThirdImage() == true) {
            if(numberOfExistingPictures > 2){
                changedOffer.setThirdOfferImage(null);

                OfferPhoto offerPhoto = new OfferPhoto();
                offerPhoto.setOffer(changedOffer);
                offerPhoto.setPhoto(existingOfferPictures.get(2).getPhoto());
                offerPhoto.setThumbnail(existingOfferPictures.get(2).getThumbnail());
                offerPhotosToAdd.add(offerPhoto);

                redundantOfferPhotosIds.add(existingOfferPictures.get(2).getId());
            }
        }

        changedOffer.setOfferPhotos(offerPhotosToAdd);

        saveOffer(changedOffer, true);

        for(Integer offerPhotosIdsToDelete : redundantOfferPhotosIds) {
            offerPhotoRepository.deleteById(offerPhotosIdsToDelete);
        }

        toDoRepository.deleteById(toDoId);
        offerHasAllergenicRepository.deleteByOfferId(offerChangeRequestId);
        offerHasAdditivesRepository.deleteByOfferId(offerChangeRequestId);
        offerPhotoRepository.deleteByOfferId(offerChangeRequestId);
        offerRepository.deleteById(offerChangeRequestId);

        logger.debug("OfferChangeRequest (Offer-ID: " + changedOffer.getId() + ") has been saved.");
    }

    private byte[] createThumbnail(MultipartFile multipartFile) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());;
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.close();

        BufferedImage thumbnail = Scalr.resize(image, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpeg", baos);
        baos.flush();
        byte[] thumbnailAsByte = baos.toByteArray();
        baos.close();

        return  thumbnailAsByte;
    }

}

