package com.dlim2012.booking.controller;

import com.dlim2012.booking.service.BookingKafkaProducerService;
import com.dlim2012.booking.service.BookingService;
import com.dlim2012.booking.service.CacheService;
import com.dlim2012.booking.service.PaypalService;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.security.service.JwtService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.dlim2012.booking.config.PayPalConfig.*;

@Controller
@RequestMapping("/api/v1/booking")
@Slf4j
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;
    private final BookingService bookingService;
    private final BookingKafkaProducerService bookingKafkaProducerService;
    private final CacheService cacheService;
    private final JwtService jwtService;

    @GetMapping(value = SUCCESS_URL)
    public String successPay(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId){
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            Booking booking = bookingService.getBookingByInvoiceId(payment.getId());

            if (payment.getState().equals("approved")){

                if (booking.getStatus() == BookingStatus.RESERVED) {
                    log.info("Payment {} approved.", payment.getId());
                    bookingService.confirmBooking(booking);

                    String userEmail = jwtService.getEmail();
                    bookingKafkaProducerService.sendBookingItem(booking, userEmail);
                    cacheService.cacheBookingEvict(booking);
                    return "success";
                } else {
                    log.info("Payment {} approved for invalid booking status of {}. Proceeding to cancellation of payment {}.",
                            payment.getId(), booking.getStatus().name(), payment.getId());
                    paypalService.cancelPayment(payment.getId());
                }
            }
            bookingService.saveBookingStatus(booking, BookingStatus.CANCELLED_PAYMENT_FAIL);
            cacheService.cacheBookingEvict(booking);
            return "cancel";
        } catch (PayPalRESTException e){
            log.error(e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping(value = CANCEL_URL)
    public String cancelPay(){
        // let
        return "cancel";
    }

    @GetMapping(path = ERROR_URL)
    public String paymentError(){
        log.info("payment-error page");
        return "payment-error";
    }
}
