package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import com.braintreegateway.*;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * The class is responsible for handling http calls related to Braintree.
 */
public final class BraintreeController {

    /** Gateway used to send payment nonces to Braintree. IDs and keys are currently for
     * the sandbox environment:
     * https://articles.braintreepayments.com/control-panel/important-gateway-credentials#api-credentials
     */
    public static BraintreeGateway gateway = new BraintreeGateway(
            Environment.SANDBOX,
            // Merchant ID
            "n84zv7kp8cym3stc",
            // Public key
            "wsmy2pdgtpsx74gz",
            // Private key. Secret!
            "13d0e621878730bf29bb84d4cb26cf8e"

    );

    private static BigDecimal fixedFee = new BigDecimal(0.35);

    private static BigDecimal percentageFee = new BigDecimal(0.019);

    public static BigDecimal getFixedFee() { return fixedFee; }

    public static BigDecimal getPercentageFee() { return percentageFee; }

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);


    private BraintreeController() {}

    /**
     * Voids a reservation transaction.
     * @param reservation the reservation
     * @return True if reservation has been successfully voided, false if it failed.
     */
    public static boolean voidTransaction(Reservation reservation){
        if(reservation.getPpTransactionId() != null){
            // Void transaction using its transaction ID
            Result<Transaction> voidResult = BraintreeController.gateway.transaction().voidTransaction(reservation.getPpTransactionId());
            if(voidResult.isSuccess()){
                LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1]
                        .getMethodName(),"Successfully voided transaction for" + reservation.getId()));
                reservation.setPpTransactionFinished(true);
                return true;
            }
            else{
                LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1]
                        .getMethodName(),"Failed to void transaction" + reservation.getId()));
                reservation.setPpTransactionFinished(false);
                for (ValidationError error : voidResult.getErrors().getAllDeepValidationErrors()) {
                    LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1]
                            .getMethodName(),error.getMessage()));
                }
                return false;

            }
        }
        else {
            return true;
        }
    }

    /**
     * Confirms a payment transaction.
     * @param reservation the reservation
     * @return True if the payment has been settled, false if it failed.
     */
    public static boolean confirmTransaction(Reservation reservation){
        if (reservation.getPpTransactionId() != null){
            try { //Because testdata has invalid transaction id`s => errors might pop up
                Result<Transaction> settlementResult = BraintreeController.gateway.transaction()
                        .submitForSettlement(reservation.getPpTransactionId());
                if (settlementResult.isSuccess()){
                    reservation.setPpTransactionFinished(true);
                    LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                            "Successfully settled payment for transaction "+ reservation.getId()));
                    return true;
                }
                else {
                    reservation.setPpTransactionFinished(false);
                    LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1]
                            .getMethodName(), "Failed settling payment for transaction "+ reservation.getId()));
                    for (ValidationError error : settlementResult.getErrors().getAllDeepValidationErrors()) {
                        LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1]
                                .getMethodName(),error.getMessage()));
                    }
                    return false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        else {
            return true;
        }
    }

}
