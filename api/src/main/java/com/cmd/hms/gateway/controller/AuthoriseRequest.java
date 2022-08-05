package com.cmd.hms.gateway.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import com.cmd.hms.gateway.model.Privilege;
import com.cmd.hms.gateway.model.Role;
import com.cmd.hms.gateway.model.User;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

public class AuthoriseRequest {
    // Fields
    private HttpServletRequest originalRequest;
    private User user;
    private String serviceUrl;
    private String unmodifiedRequest;
    private String resource;
    private HashMap<String, Boolean> allowedWriteFields = new HashMap<String, Boolean>();
    private String filter;
    private String select;
    private String condition;
    private String fields;
    private String queryString;

 

    // Constructors
    AuthoriseRequest(HttpServletRequest httpServletRequest, User user, String serviceUrl){
        this.originalRequest = httpServletRequest;
        this.user = user;
        this.serviceUrl = serviceUrl;
    }

    public String replace(String string, String[] find, String[] replace){
        String result = "";

        if(find.length == replace.length){
            Map<String, String> map= new HashMap();

            for(int i=0; i < find.length; i++){
                map.put(find[i], replace[i]);
            }

            result = map.keySet()
                    .stream()
                    .reduce(string, (s, e) -> s.replaceAll(e+"\\b", map.get(e)));

            if(string.length() == result.length()){
                return "";
            } 
            else {
                return result;
            }
        } else {
            return "";
        }
        

    }

    public String setQueryString(Boolean addFilter, Boolean addSelect){
        
        if(this.originalRequest.getQueryString() == null){
            String returnValue = "";
            if(addFilter){
                returnValue += "$filter=" + this.condition;

                if(addSelect){
                    returnValue += "&$select=" + this.fields;
                }
            } else if(addSelect){
                returnValue += "$select=" + this.fields;
            } 
            return returnValue;
        } else {
        
           
            // http://130.211.227.209:8090/odata/Patients(1)/AddressDetails?$filter=AddressId%20eq%202PatientId eq 1 & (PatientStatusId eq 1 or PatientStatusId eq 2)&$select=City

           String result = "";
        
            result = this.queryString; // Encoding url twice causes bug
                

            if(addFilter){
                if(this.filter.equals("$filter=")){
                    result += this.filter + this.condition;

                } else {
                    result = result.replace(this.filter, this.filter + " and " + this.condition);
                }
            }

            if(addSelect){
                if(this.filter.equals("$seelct=")){
                    result += this.select + this.condition;

                } else {
                    result = result.replace(this.select, this.select + ","  + this.condition);    
                }
            }

           
            
            return result;
        }
            

    }

    public String replacePlaceholders(String condition){
        String[] findArray =  {"%PatientId"};
        String[] replaceArray = {this.user.getPatientId().toString()};

        return replace(condition, findArray, replaceArray);
    }

    public Boolean checkCondition(String condition) {
        // Make GET request
        String checkUrl = this.serviceUrl + this.resource + "?$filter=" + replacePlaceholders(condition);
       
       
        WebClient client = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
            .build();

        WebClient.ResponseSpec responseSpec = client.get()
            .uri(checkUrl)
            .retrieve();

        // Check if request contains entity
        String responseBody = responseSpec.bodyToMono(String.class).block();
        
        return responseBody.contains("<entry>");
    }
   
    public Boolean checkWriteRequest(){
        if(this.allowedWriteFields.isEmpty()){
           return false; 
        } else {
            return true;
        }
        
    }

    public String forwardRequest(){
        String forwardUrl = allowedRequest();


        if(forwardUrl != ""){
            try{ 
                System.out.println(this.originalRequest.getMethod());
                WebClient client = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
                .build();
                String requestType = this.originalRequest.getMethod().toString();

                // Prepare request
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                RestTemplate restTemplate = new RestTemplate();
                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                restTemplate.setRequestFactory(requestFactory);

                if(requestType.equals("GET")){
                    System.out.println("forwardUrl: " + forwardUrl);
                    WebClient.ResponseSpec responseSpec = client.get()
                    .uri(forwardUrl)
                    .retrieve();

                    String responseBody = responseSpec.bodyToMono(String.class).block();
                    return responseBody;
                } else if(requestType.equals("PATCH")){
                    // Prepare request 
                    String requestBody = IOUtils.toString(this.originalRequest.getReader());
                   
                    // Patch request
                    HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
                    String patchResponse = restTemplate.patchForObject(forwardUrl, request, String.class);

                    return patchResponse;
                } else if(requestType.equals("POST")){
                    // Prepare request 
                    String requestBody = IOUtils.toString(this.originalRequest.getReader());

                    // Post request
                    HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
                    String postResponse = restTemplate.postForObject(forwardUrl, request, String.class);

                    return postResponse;
                } else if(requestType.equals("DELETE")){
                    // Delete request
                    restTemplate.delete(forwardUrl, String.class);
                    return "Deleted";
                }
                else {
                    System.out.println("Invalid request type");
                    return "Not authorised";
                }
               

              

            } catch(Exception e) {
                System.out.println(e.getMessage());
                System.out.println("URL failed: " + forwardUrl);
                return "Not Authorised";
            }

        } 
        else {
            return "Not Authorised";
        } //http://130.211.227.209:8090/odata/Patients(1)/AddressDetails?$filter=AddressId%2520eq%25202&PatientId%20eq%201%20&%20(PatientStatusId%20eq%201%20or%20PatientStatusId%20eq%202)&$select=City
        
    }

    public String getSystemQuery(String queryVariable){
        queryVariable += "=";
        String returnValue = "";

        if(queryString != null){
            if(queryString.contains(queryVariable))
            {
                String between = StringUtils.substringBetween(this.queryString, queryVariable, "&$");

                if(between != null){
                    returnValue = between;
                } else {
                    String after = StringUtils.substringAfter(this.queryString, queryVariable);

                    if(after != null){
                        returnValue = after;
                    }  

                }

                
            }
        }
        System.out.println("returnValue: " + returnValue);
        return returnValue;
    }



    public String allowedRequest(){
        // Get requested resource name
        String[] array = this.originalRequest.getRequestURI().split("/");
        
        Integer routeIndex = 3;
        String route = array[routeIndex];

        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(route);
        matcher.find();
        this.resource = matcher.group();


        String resourceUrl = "";
        String remainingUrl = "";

        for (int i = routeIndex; i < array.length; i++) {
            if(i > (routeIndex)){
                resourceUrl += "/";
            }  
            
            if(i >= (routeIndex + 1)){
                remainingUrl += "/" + array[i];
            }
            resourceUrl += array[i];
        }

        this.unmodifiedRequest = this.serviceUrl + resourceUrl;

        System.out.println("Query String: " + this.originalRequest.getQueryString());
        System.out.println(this.originalRequest.getQueryString() != null);
        if (this.originalRequest.getQueryString() != null){
            System.out.println("Query string not null");
            try {
            
                this.queryString = URLDecoder.decode(this.originalRequest.getQueryString(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                this.queryString = null;
            }
        } else {
            this.queryString = null;
        }

        this.select = "$select=" + getSystemQuery("$select");
        this.filter = "$filter=" + getSystemQuery("$filter");
        this.fields = "";
        

        // Check privileges
        List<Role> roles = user.getRoles();

        // Iterate over roles
        for(Role role : roles){
            // Admin can perform any action
            if(role.getTitle().toLowerCase().contains("admin")){
                System.out.println("Is Admin");
                System.out.println(role.getTitle());
                return this.serviceUrl + route + remainingUrl;
            }

            List<Privilege> privileges = role.getPrivileges();

            // Iterate over privileges
            for(Privilege privilege : privileges){
                // If privilege is for requested resource
                if(this.resource.equals(privilege.getPrivilegeClass().getName() + "s")){ 
                    // If privilege allows for requested method
                    if(privilege.allowedMethod(this.originalRequest.getMethod())) {
                        String condition = privilege.getCondition();

                        // Authorising read requests
                        if(this.originalRequest.getMethod().contains("GET")){
                            // No condition needed
                            if(condition == null || condition.length() == 0 || checkCondition(condition)){
                                // Applies to all fields
                                if(privilege.getAllFields()){
                                    this.condition = replacePlaceholders(condition);
                                    return this.serviceUrl + route + remainingUrl + "?" + setQueryString(true, false);
                                } else {
                                    select += privilege.getField().getName() + ",";
                                }
                            }
                            
                        } 
                        // Authorising write requests
                        else {
                            // No condition needed
                            if(condition == null || condition.length() == 0 || checkCondition(condition)){
                                // Applies to all fields
                                if(privilege.getAllFields()){
                                    return this.unmodifiedRequest;
                                } else {
                                    // Field now allowed
                                   this.allowedWriteFields.put(privilege.getField().getName(), true);
                                }
                            }


                        }

                    }
               
               
                }// == checks reference to object not the actual string

              

            }


        }

        
        if(this.originalRequest.getMethod().contains("GET")){
           
            if(select != "$select="){
                return this.serviceUrl + route + remainingUrl + "?" + setQueryString(true, true);
            } else {
                return "";
            }
           
        }
        else {
           if(checkWriteRequest()){
            
            return this.unmodifiedRequest;
           } 
           else {
           
            return "";
           }

        }

    }

    
}
