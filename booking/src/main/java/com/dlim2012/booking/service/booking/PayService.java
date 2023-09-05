package com.dlim2012.booking.service.booking;

import com.dlim2012.booking.config.PayPalConfig;
import com.dlim2012.clients.mysql_booking.entity.Booking;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayService {

    private final PayPalConfig payPalConfig;
    private final APIContext apiContext;

    public Payment createPaypalPayment(
            Booking booking
    ) throws PayPalRESTException {
        if (booking == null) {
            return null;
        }
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.format("%d.%02d", booking.getPriceInCents() / 100, booking.getPriceInCents() % 100));

        String description = String.format("Booking rooms from hotel %d.", booking.getHotelId());
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(payPalConfig.getSuccessUrl(booking.getId()));
        redirectUrls.setReturnUrl(payPalConfig.getCancelUrl(booking.getId()));

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        log.info("Executing payment (ID: {}, payer ID: {})", paymentId, payerId);
        return payment.execute(apiContext, paymentExecution);
    }
}
