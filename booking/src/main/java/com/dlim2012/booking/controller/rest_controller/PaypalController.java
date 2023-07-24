package com.dlim2012.booking.controller.rest_controller;

import com.dlim2012.booking.config.PayPalConfig;
import com.dlim2012.booking.service.booking_entity.BookingService;
import com.dlim2012.clients.entity.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.dlim2012.booking.config.PayPalConfig.CANCEL_URL;
import static com.dlim2012.booking.config.PayPalConfig.SUCCESS_URL;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
class PaypalController{

    private final BookingService bookingService;

    private final PayPalConfig payPalConfig;

    @GetMapping(SUCCESS_URL + "/{bookingId}")
    public RedirectView processPaymentSuccess(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Payment success for booking {} requested: paymentId: {}, PayerID: {}",
                bookingId, paymentId, payerId);
        Boolean executed = bookingService.processPaymentSuccess(bookingId, paymentId, payerId);
        if (executed) {
            log.info("Payment {} for booking {} executed", paymentId, bookingId);
//            redirectAttributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
            redirectAttributes.addAttribute("attribute", "redirectWithRedirectView");
            return new RedirectView(payPalConfig.HOST + "/hotels/booking/payment/success/" + bookingId.toString());
        } else {
            log.info("Payment {} for booking {} ignored (not executed).", paymentId, bookingId);
//            redirectAttributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
            redirectAttributes.addAttribute("status", "timeout");
            return new RedirectView(payPalConfig.HOST + "/hotels/booking/payment/cancel/" + bookingId.toString());
        }
    }

    @GetMapping(CANCEL_URL + "/{bookingId}")
    public RedirectView processPaymentCancelled(
            @PathVariable("bookingId") Long bookingId,
            RedirectAttributes redirectAttributes
    ){
        log.info("Payment success for booking {} requested.",
                bookingId);
        bookingService.processPaymentCancelledIfStatusReservedForTimeOut(bookingId, BookingStatus.CANCELLED_PAYMENT);

//        redirectAttributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        redirectAttributes.addAttribute("status", "cancelled");
        return new RedirectView(payPalConfig.HOST + "/hotels/booking/payment/cancel/" + bookingId.toString());
    }
}