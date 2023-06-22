package com.dlim2012.booking.controller;

import com.dlim2012.booking.config.PayPalConfig;
import com.dlim2012.booking.service.BookingService;
import com.dlim2012.booking.service.PaypalService;
//import com.dlim2012.security.service.JwtService;
import com.dlim2012.clients.dto.booking.BookingItem;
import com.dlim2012.clients.entity.BookingStatus;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.NotAuthorizedException;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.dlim2012.clients.security.service.JwtService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dlim2012.booking.config.PayPalConfig.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final JwtService jwtService;
    private final BookingService bookingService;
    private final PaypalService paypalService;

    private final PayPalConfig payPalConfig;

    @GetMapping(path = "/hello")
    public String hello(){
        return "hello";
    }

    @PostMapping(path = "/hotel/{hotelId}/room/{roomId}/book")
    public String book(
            @PathVariable Integer hotelId,
            @PathVariable Integer roomId,
            @RequestBody @Validated(value = {BookingItem.Post.class}) BookingItem bookingItem){
        log.info("Book requested for room {} of hotel {} (start date: {}, end date: {}, quantity: {})",
                roomId, hotelId, bookingItem.getStartDateTime().toLocalDate(), bookingItem.getEndDateTime().toLocalDate(),
                bookingItem.getQuantity());
        Integer userId = jwtService.getId();

        // reserve booking before payment
        Booking booking = bookingService.reserveBook(userId, hotelId, roomId, bookingItem);


        // payment using paypal
        // case 1) payment approval request fail: cancelled in this function
        // case 2) payment timeout: cancelled by redis listener
        // case 3) payment cancelled: cancelled by paypal controller
        // case 4) payment success before timeout: approved by paypal controller
        // case 5) payment success after timeout: cancelled by paypal controller
        // case 6) payment success when booking status is not "RESERVED": cancelled by paypal controller

        try {
            Payment payment = paypalService.createPayment(
                    booking.getPriceInCents(),
                    CONCURRENCY,
                    METHOD,
                    INTENT,
                    String.format("Booking room %s.", booking.getRoomId()),
                    payPalConfig.getCancelUrl(),
                    payPalConfig.getSuccessUrl()
            );
            for(Links link: payment.getLinks()) {
                if (link.getRel().equals("approval_url")){
                    // save invoice ID and redirect for user approval
                    bookingService.saveInvoiceId(booking, payment.getId());;
                    return "redirect:/" + link.getHref();
                }
            }
        } catch (PayPalRESTException e){
            log.error(e.getMessage());
        }

        bookingService.revertBook(booking, BookingStatus.CANCELLED_PAYMENT_FAIL);
        return "redirect:/payment/error";
    }
    @PostMapping("/booking/{bookingId}/cancel/{userRole}")
    public void cancel(
            @PathVariable("bookingId") Long bookingId,
            @PathVariable("userRole") String userRoleString){

        UserRole userRole = UserRole.valueOf(userRoleString);
        log.info("Booking {} cancel request from user {}", bookingId, userRoleString);

        List<String> scopes = SecurityContextHolder
                .getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
        if (!scopes.contains(userRole)){
            throw new NotAuthorizedException("User not authorized.") {
            };
        }

        // Get booking if booked
        Booking booking = bookingService.getBookingByIdAndStatus(bookingId, BookingStatus.BOOKED);

        // Cancel payment
        paypalService.cancelPayment(booking.getInvoiceId(), booking.getPriceInCents());

        // Revert booking
        BookingStatus newBookingStatus;
        if (userRole == UserRole.APP_USER){
            newBookingStatus = BookingStatus.CANCELLED_BY_APP_USER;
        } else if (userRole == UserRole.HOTEL_MANAGER){
            newBookingStatus = BookingStatus.CANCELLED_BY_HOTEL_MANAGER;
        } else if (userRole == UserRole.ADMIN){
            newBookingStatus = BookingStatus.CANCELLED_BY_ADMIN;
        } else {
            throw new RuntimeException("New booking status not found.");
        }

        bookingService.revertBook(booking, newBookingStatus);
    }

}
