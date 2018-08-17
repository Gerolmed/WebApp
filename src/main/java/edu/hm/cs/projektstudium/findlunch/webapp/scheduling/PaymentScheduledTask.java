package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.BraintreeController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A scheduled task to create payments.
 */
@Component
public class PaymentScheduledTask {

    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentScheduledTask.class);

    /** The reservation repository. */
    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Checks for unprocessed payments.
     */
    //@Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(fixedRate = 200000)
    public void checkProcessedPayments() {
        //Log info
        LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Starting check for unfinished payments."));

        List<Reservation> reservations = reservationRepository.findByUsedPaypalTrueAndPpTransactionIdNotNullAndPpTransactionFinishedFalse();

        for(Reservation reservation : reservations){
            int statkey = reservation.getReservationStatus().getKey();
            if(statkey == 1){
                BraintreeController.confirmTransaction(reservation);
            }
            else if(statkey == 2 || statkey == 3){
                BraintreeController.voidTransaction(reservation);
            }

        }
        //Console log info
        LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), "Check for unfinished payments finished."));
    }
}
