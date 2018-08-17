package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferPhoto;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ToDo;
import edu.hm.cs.projektstudium.findlunch.webapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Implementation of the interface OfferChangeRequestService.
 */
@Service
public class OfferChangeRequestServiceImpl implements OfferChangeRequestService {

    @Autowired
    private DbReaderService dbReaderService;

    @Autowired
    private DbWriterService dbWriterService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private OfferService offerService;

    @Override
    public ToDo getToDoById(int id) {
        return dbReaderService.getToDoById(id);
    }

    @Override
    public List<ToDo> getAllToDosOfSalesPerson(String email) {
        return dbReaderService.getAllToDosOfSalesPerson(email);
    }

    @Override
    public List<CourseType> getCourseTypes(int restaurantId) {
        List<CourseType> courseTypes = null;
        try {
            courseTypes = restaurantService.getAllCourseTypesOfRestaurant(restaurantId);
        } catch (Exception e) {
            // Restaurant has no course types so far.
        }

        if(courseTypes == null) {
            courseTypes = new ArrayList<>();
        }

        return courseTypes;
    }


    @Override
    public Model prepareOfferPicturesForExistingOffer(Model model, Offer offer) {
        int numberOfExistingPictures = 0;
        List<OfferPhoto> offerPhotos = offer.getOfferPhotos();

        try {
            numberOfExistingPictures = offerPhotos.size();
        } catch (Exception e) {
            //zero existing pictures
        }

        String defaultImageBase64 = offerService.getDefaultOfferImageBase64();

        switch (numberOfExistingPictures) {
            case 0:
                model.addAttribute("existingOfferFirstPicture", defaultImageBase64);
                model.addAttribute("existingOfferSecondPicture", defaultImageBase64);
                model.addAttribute("existingOfferThirdPicture", defaultImageBase64);
                break;

            case 1:
                model.addAttribute("existingOfferFirstPicture", Base64.getEncoder().encodeToString(offerPhotos.get(0).getThumbnail()));
                model.addAttribute("existingOfferSecondPicture", defaultImageBase64);
                model.addAttribute("existingOfferThirdPicture", defaultImageBase64);
                break;

            case 2:
                model.addAttribute("existingOfferFirstPicture", Base64.getEncoder().encodeToString(offerPhotos.get(0).getThumbnail()));
                model.addAttribute("existingOfferSecondPicture", Base64.getEncoder().encodeToString(offerPhotos.get(1).getThumbnail()));
                model.addAttribute("existingOfferThirdPicture", defaultImageBase64);
                break;

            default: // 3 and more pics
                model.addAttribute("existingOfferFirstPicture", Base64.getEncoder().encodeToString(offerPhotos.get(0).getThumbnail()));
                model.addAttribute("existingOfferSecondPicture", Base64.getEncoder().encodeToString(offerPhotos.get(1).getThumbnail()));
                model.addAttribute("existingOfferThirdPicture", Base64.getEncoder().encodeToString(offerPhotos.get(2).getThumbnail()));
        }

        return model;
    }

    @Override
    public Offer prepareKeepImagesTags(Offer preparedExistingOffer, Offer preparedChangedOffer) {
        int numberOfExistingImages = preparedExistingOffer.getOfferPhotos().size();

        if(numberOfExistingImages == 1) {
            preparedChangedOffer.setKeepFirstImage(true);
        }

        if(numberOfExistingImages == 2) {
            preparedChangedOffer.setKeepFirstImage(true);
            preparedChangedOffer.setKeepSecondImage(true);
        }

        if(numberOfExistingImages >= 3) {
            preparedChangedOffer.setKeepFirstImage(true);
            preparedChangedOffer.setKeepSecondImage(true);
            preparedChangedOffer.setKeepThirdImage(true);
        }

        return preparedChangedOffer;
    }

    @Override
    public Model prepareIdsOfOfferChangeRequestImages(Model model, Offer preparedChangedOffer, Offer originalChangedOffer) {

        //The offerPhotos are null if this function is called after a Validation error
        if(preparedChangedOffer.getOfferPhotos() == null) {
            preparedChangedOffer.setOfferPhotos(originalChangedOffer.getOfferPhotos());
        }
        int numberOfExistingImages = preparedChangedOffer.getOfferPhotos().size();

        model.addAttribute("firstChangedPictureDeleteDisabled", true);
        model.addAttribute("secondChangedPictureDeleteDisabled", true);
        model.addAttribute("thirdChangedPictureDeleteDisabled", true);

        if(numberOfExistingImages >= 1) {
            model.addAttribute("idOfFirstChangedPicture", preparedChangedOffer.getOfferPhotos().get(0).getId());
            model.addAttribute("firstChangedPictureDeleteDisabled", false);
        }

        if(numberOfExistingImages >= 2) {
            model.addAttribute("idOfSecondChangedPicture", preparedChangedOffer.getOfferPhotos().get(1).getId());
            model.addAttribute("secondChangedPictureDeleteDisabled", false);
        }

        if(numberOfExistingImages >= 3) {
            model.addAttribute("idOfThirdChangedPicture", preparedChangedOffer.getOfferPhotos().get(2).getId());
            model.addAttribute("thirdChangedPictureDeleteDisabled", false);
        }

        return model;
    }

    @Override
    public void deleteOfferChangeRequest(int offerToDeleteId, int offerToUpdateId, int toDoId) {
        dbWriterService.deleteOfferChangeRequest(offerToDeleteId, offerToUpdateId, toDoId);
    }

    @Override
    public void saveOfferChangeRequest(int offerChangeRequestId, Offer changedOffer, Offer existingOffer, int toDoId) {
        dbWriterService.saveOfferChangeRequest(offerChangeRequestId, changedOffer, existingOffer, toDoId);
    }

    @Override
    public Model addAttribtueChangesToModel(Model model, boolean allFalse, Offer preparedExistingOffer, Offer preparedChangedOffer) {

        Offer existingOffer = preparedExistingOffer;
        Offer changedOffer = preparedChangedOffer;

        if (allFalse == true) {
            model.addAttribute("titleChanged", false);
            model.addAttribute("priceChanged", false);
            model.addAttribute("neededPointsChanged", false);
            model.addAttribute("preparationTimeChanged", false);
            model.addAttribute("startDateChanged", false);
            model.addAttribute("endDateChanged", false);
            model.addAttribute("descriptionChanged", false);

            return model;
        }

        //Compares the attributes of the two offer objects
        if (allFalse == false) {
            if (!existingOffer.getTitle().equals(changedOffer.getTitle())) {
                model.addAttribute("titleChanged", true);
            } else {
                model.addAttribute("titleChanged", false);
            }

            if (!existingOffer.getPriceAsString().equals(changedOffer.getPriceAsString())) {
                model.addAttribute("priceChanged", true);
            } else {
                model.addAttribute("priceChanged", false);
            }

            if (!existingOffer.getNeededPointsAsString().equals(changedOffer.getNeededPointsAsString())) {
                model.addAttribute("neededPointsChanged", true);
            } else {
                model.addAttribute("neededPointsChanged", false);
            }

            if (!existingOffer.getPreparationTimeAsString().equals(changedOffer.getPreparationTimeAsString())) {
                model.addAttribute("preparationTimeChanged", true);
            } else {
                model.addAttribute("preparationTimeChanged", false);
            }

            if (!existingOffer.getDescription().equals(changedOffer.getDescription())) {
                model.addAttribute("descriptionChanged", true);
            } else {
                model.addAttribute("descriptionChanged", false);
            }

            //Start date
            if (existingOffer.getStartDateAsString() != null && changedOffer.getStartDateAsString() == null
                    || existingOffer.getStartDateAsString() == null && changedOffer.getStartDateAsString() != null) {
                        model.addAttribute("startDateChanged", true);
            } else {
                model.addAttribute("startDateChanged", false);
            }

            if (existingOffer.getStartDateAsString() != null && changedOffer.getStartDateAsString() != null) {
                if (!existingOffer.getStartDateAsString().equals(changedOffer.getStartDateAsString())) {
                        model.addAttribute("startDateChanged", true);
                }
            } else {
                model.addAttribute("startDateChanged", false);
            }

            //End date
            if (existingOffer.getEndDateAsString() != null && changedOffer.getEndDateAsString() == null
                    || existingOffer.getEndDateAsString() == null && changedOffer.getEndDateAsString() != null) {
                        model.addAttribute("endDateChanged", true);
            } else {
                model.addAttribute("endDateChanged", false);
            }

            if (existingOffer.getEndDateAsString() != null && changedOffer.getEndDateAsString() != null) {
                if (!existingOffer.getEndDateAsString().equals(changedOffer.getEndDateAsString())) {
                        model.addAttribute("endDateChanged", true);
                } else {
                    model.addAttribute("endDateChanged", false);
                }
            }

        }
        return model;
    }

}
