package com.dlim2012.booking.service.booking_entity.utils;

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
public class PaypalService {

    private final APIContext apiContext;

    public Payment createPayment(
            Long priceInCents,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%d.%02d", priceInCents / 100, priceInCents % 100));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        Payment payment = new Payment();
        payment.setIntent(intent);
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


    public void cancelPayment(
            String invoiceId,
            Long priceInCents,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) {
        String price = String.format("%d.%02d", priceInCents / 100, priceInCents % 100);
        log.info("Cancelling payment for invoice {} with amount of {}",
                invoiceId, price);
        boolean cancelled = false;
        // todo: implement payment cancellation
//        Amount amount = new Amount();
//        amount.setCurrency(currency);
//        amount.setTotal(String.format("%d.%02d", priceInCents / 100, priceInCents % 100));
//
//        Refund refund = new Refund();
//        refund.setAmount(amount);
//        refund.setId(invoiceId);

        if (!cancelled) {
            log.error("Payment cancellation for invoice {} with amount of {} failed.",
                    invoiceId, price);
        }
    }

    public void cancelPayment(String invoiceId){
        log.info("Cancelling payment (Invoice ID: {})", invoiceId);
        // todo cancel payment
    }

    public void cancelPayment(String invoiceId, Long priceInCents){
        log.info("Cancelling payment (Invoice ID: {}, amount: {} cents)", invoiceId, priceInCents);
    }

}
