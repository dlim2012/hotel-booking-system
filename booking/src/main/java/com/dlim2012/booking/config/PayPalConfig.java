package com.dlim2012.booking.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {
    @Value("${custom.paypal.client.id}")
    private String clientId;

    @Value("${custom.paypal.client.secret}")
    private String clientSecret;

    @Value("${custom.paypal.mode}")
    private String mode;


    @Value("${custom.paypal.host}")
    public String HOST;

    public static String PATH = "/api/v1/booking";
    public static final String INTENT = "sale";
    public static final String CONCURRENCY = "USD";
    public static final String METHOD = "paypal";
    public static final String SUCCESS_URL = "/payment/success";
    public static final String CANCEL_URL = "/payment/cancel";

//    @Value("${custom.paypal.success-redirect-url}")
//    public String SUCCESS_REDIRECT_URL;
//    @Value("${custom.paypal.cancel-redirect-url}")
//    public String CANCEL_REDIRECT_URL;
//    public static final String ERROR_URL = "/hotels/booking/payment/error";

    @Bean
    public Map<String, String> paypalSdkConfig(){
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential(){
        System.out.println(clientId);
        System.out.println(HOST);
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }

    public String getCancelUrl(Long bookingId){
        return HOST + PATH + CANCEL_URL + "/" + bookingId.toString();
    }

    public String getSuccessUrl(Long bookingId){
        return HOST + PATH + SUCCESS_URL + "/" + bookingId.toString();

//        return HOST + SUCCESS_URL + "/" + bookingId.toString();
    }

}
