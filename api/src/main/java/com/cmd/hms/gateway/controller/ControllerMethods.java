package com.cmd.hms.gateway.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;


public class ControllerMethods {
    public static ResponseEntity<String> forwardRequest(HttpServletRequest request, String externalApiPath, String requestBody){
        /* Build request url */
        String requestPath = StringUtils.substringAfter(request.getRequestURI(), "/api/patient");
        String requestQuery = "";
        String forwardUrl = externalApiPath;
        
        // URL decode the query
        try {
            requestQuery = URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8.name());
        } catch(Exception e){
            requestQuery = null;
        }

        // Add query to request if not null
        if(requestQuery == null){
            forwardUrl += requestPath;
        } else {
            forwardUrl += requestPath + "?" + requestQuery;
        }

        
    
        
        // Build request
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> httpRequest = null;
        if(requestBody == null){
            httpRequest = new HttpEntity<String>(headers);
        } else {
            httpRequest = new HttpEntity<String>(requestBody, headers);
        }
       
        request.getMethod();
        
        // Send request. Return exception if bad request
        try {
             return restTemplate.exchange(forwardUrl, HttpMethod.valueOf(request.getMethod()), httpRequest, String.class);
        } catch(Exception e){
           System.out.println(e.getMessage());
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public static ResponseEntity<String> customGetRequest(HttpServletRequest request, String modifiedRequestPath, String externalApiPath){
        /* Build request url */
        String requestPath = StringUtils.substringAfter(modifiedRequestPath, "/api/patient");
        String forwardUrl = externalApiPath + requestPath;
        
        // Build request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", request.getHeader("Authorization"));
       
        HttpEntity<Object> httpRequest = new HttpEntity<Object>(headers);
        
       
        // Send request. Return exception if bad request 
        try {
             return restTemplate.exchange(forwardUrl, HttpMethod.GET, httpRequest, String.class);
        } catch(Exception e){
           System.out.println(forwardUrl);
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    
}
