package com.dlim2012.test.utils;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Getter
@Setter
@Component
public class RestClient {
    private String gatewayAddress = "http://10.0.0.110:8001";
    private String jwt;

    public Object get(String path, Class<?> s) {
        RestTemplate restTemplate = new RestTemplate();
        return  restTemplate.getForObject(gatewayAddress + path, s);
    }

    public <T> Object post(String path, T t, Class<?> s) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<T> request = new HttpEntity<T>(t);
        return  restTemplate.postForObject(gatewayAddress + path, request, s);
    }

    public <T> Object postWithJwt(String path, T t, Class<?> s){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        HttpEntity<T> request = new HttpEntity<T>(t, headers);
//        System.out.println(request);
        return restTemplate.postForObject(gatewayAddress + path, request, s);
    }

    public <T> Object getWithJwt(String path, Class<?> s){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(gatewayAddress + path, HttpMethod.GET, request, s).getBody();
    }

    public <T> Object putWithJwt(String path, T t, Class<?> s){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        HttpEntity<T> request = new HttpEntity<T>(t, headers);
        return restTemplate.exchange(gatewayAddress + path, HttpMethod.PUT, request, s).getBody();
    }

    public ResponseEntity<?> deleteWithJwt(String path, Class<?> s){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwt);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(gatewayAddress + path, HttpMethod.DELETE, request, s);
    }

    public ResponseEntity<?> delete(String path, Class<?> s){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(gatewayAddress + path, HttpMethod.DELETE, request, s);

    }
}
