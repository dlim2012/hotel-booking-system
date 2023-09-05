package com.dlim2012.booking.controller.rest_controller;

import com.dlim2012.booking.config.PayPalConfig;
import com.dlim2012.booking.service.booking.BookingService;
import com.dlim2012.booking.service.booking.PayService;
import com.dlim2012.booking.service.common.CacheService;
import com.dlim2012.clients.entity.BookingMainStatus;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.mysql_booking.entity.BookingRoom;
import com.dlim2012.clients.mysql_booking.entity.BookingRooms;
import com.dlim2012.clients.mysql_booking.repository.BookingRepository;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

import static com.dlim2012.booking.config.PayPalConfig.CANCEL_URL;
import static com.dlim2012.booking.config.PayPalConfig.SUCCESS_URL;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
class PaypalController {


    private final PayPalConfig payPalConfig;

    private final BookingService bookingService;
    private final PayService payService;
    private final CacheService cacheService;

    private final BookingRepository bookingRepository;

    @GetMapping(SUCCESS_URL + "/{bookingId}")
    public RedirectView processPaymentSuccess(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            RedirectAttributes redirectAttributes
    ) throws PayPalRESTException {
        log.info("Payment success for booking {} requested: paymentId: {}, PayerID: {}",
                bookingId, paymentId, payerId);

        cacheService.cacheBookingIdEvict(bookingId);
        Booking booking = bookingRepository.findByIdWithLock(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));

        try {
            payService.executePayment(paymentId, payerId);
            log.info("Payment {} for booking {} executed", paymentId, bookingId);
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
            bookingRepository.save(booking);
            redirectAttributes.addAttribute("status", "timeout");
            return new RedirectView(payPalConfig.FRONTEND + "/hotels/booking/payment/cancel/" + bookingId.toString());
        }

        booking.setMainStatus(BookingMainStatus.BOOKED);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setInvoiceConfirmTime(LocalDateTime.now());
        for (BookingRooms bookingRooms : booking.getBookingRooms()) {
            for (BookingRoom bookingRoom : bookingRooms.getBookingRoomSet()) {
                bookingRoom.setStatus(BookingStatus.BOOKED);
            }
        }

        bookingRepository.save(booking);
        return new RedirectView(payPalConfig.FRONTEND + "/hotels/booking/payment/success/" + bookingId.toString());
    }

    @GetMapping(CANCEL_URL + "/{bookingId}")
    public RedirectView processPaymentCancelled(
            @PathVariable("bookingId") Long bookingId,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Payment cancel for booking {} requested.", bookingId);

        cacheService.cacheBookingIdEvict(bookingId);
        Booking booking = bookingRepository.findByIdWithLock(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));


        booking.setMainStatus(BookingMainStatus.CANCELLED);
        booking.setStatus(BookingStatus.CANCELLED_PAYMENT_TIME_EXPIRED);
        booking.setInvoiceConfirmTime(LocalDateTime.now());
        for (BookingRooms bookingRooms : booking.getBookingRooms()) {
            for (BookingRoom bookingRoom : bookingRooms.getBookingRoomSet()) {
                bookingRoom.setStatus(BookingStatus.CANCELLED_PAYMENT_TIME_EXPIRED);
            }
        }

        bookingRepository.save(booking);
        redirectAttributes.addAttribute("status", "cancelled");
        return new RedirectView(payPalConfig.FRONTEND + "/hotels/booking/payment/cancel/" + bookingId.toString());
    }
}