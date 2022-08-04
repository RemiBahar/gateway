package com.cmd.hms.gateway.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;

import javax.servlet.http.HttpServletRequest;

import com.cmd.hms.gateway.model.Privilege;
import com.cmd.hms.gateway.model.Role;
import com.cmd.hms.gateway.model.User;

import reactor.netty.http.client.HttpClient;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
                returnValue += "?$filter=" + this.condition;

                if(addSelect){
                    returnValue += "&$select=" + this.fields;
                }
            } else if(addSelect){
                returnValue += "?$select=" + this.fields;
            } 
            return returnValue;
        } else {
        
            Map<String, String> map= new HashMap();

            if(addFilter){
                System.out.println("find: " + this.filter);
                System.out.println("replace: " + this.filter + this.condition);
                System.out.println("in: " + this.originalRequest.getQueryString());
                map.put(this.filter, this.filter + this.condition);    
            }

            if(addSelect){
                map.put(this.select, this.select + this.fields);    
            }

            String result = map.keySet()
                    .stream()
                    .reduce(this.originalRequest.getQueryString(), (s, e) -> s.replaceAll(e+"\\b", map.get(e)));
            System.out.println("result: " + result);
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
                WebClient client = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
                .build();
                
                WebClient.ResponseSpec responseSpec = client.get()
                .uri(forwardUrl)
                .retrieve();

                // Check if request contains entity
                String responseBody = responseSpec.bodyToMono(String.class).block();

                return responseBody;

            } catch(Exception e) {
                return "Not Authorised";
            }

        } 
        else {
            return "Not Authorised";
        }
        
    }

    public String getSystemQuery(String queryString, String queryVariable){
        queryVariable += "=";
        String returnValue = "";

        if(queryString != null){
            if(queryString.contains(queryVariable))
            {
                String between = StringUtils.substringBetween(this.originalRequest.getQueryString().toString(), queryVariable, "&$");

                if(between != null){
                    returnValue = between;
                } else {
                    String after = StringUtils.substringAfter(this.originalRequest.getQueryString().toString(), queryVariable);

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

        String[] test = this.originalRequest.getRequestURI().split("/");

        System.out.println(this.originalRequest.getContextPath());
        String[] array = this.originalRequest.getRequestURI().split("/");
        System.out.println("length: " + array.length);
        Integer routeIndex = 3;
        String route = array[routeIndex];

        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(route);
        matcher.find();
        this.resource = matcher.group();


        String resourceUrl = "";
        String remainingUrl = "";

        for (int i = routeIndex; i < array.length; i++) {
            //System.out.println(array[i] + " " + i + " " + routeIndex);
            if(i > (routeIndex)){
                resourceUrl += "/";
            }  
            
            if(i >= (routeIndex + 1)){
                remainingUrl += "/" + array[i];
            }
            resourceUrl += array[i];
        }

        this.unmodifiedRequest = this.serviceUrl + resourceUrl;
       
        System.out.println(StringUtils.substringBetween(this.originalRequest.getRequestURI(), this.resource + "(", ")"));
        System.out.println(this.originalRequest.getQueryString());


        

        //System.out.println(StringUtils.substringBetween(this.originalRequest.getQueryString().toString(), "$filter=", "&$"));
        String queryString = this.originalRequest.getQueryString();
        this.select = "$select=" + getSystemQuery(queryString, "$select");
        this.filter = "$filter=" + getSystemQuery(queryString, "$filter");
        this.fields = "";
        

        // Check privileges
        List<Role> roles = user.getRoles();

        // Iterate over roles
        for(Role role : roles){
            // Admin can perform any action
            if(role.getTitle().toLowerCase().contains("admin")){
                return this.originalRequest.getRequestURI();
            }

            List<Privilege> privileges = role.getPrivileges();

            // Iterate over privileges
            for(Privilege privilege : privileges){
                // If privilege is for requested resource
                if(this.resource.equals(privilege.getPrivilegeClass().getName() + "s")){ 
                    System.out.println("Privilege");

                    // If privilege allows for requested method
                    if(privilege.allowedMethod(this.originalRequest.getMethod())) {
                        String condition = privilege.getCondition();

                        System.out.println(replacePlaceholders(condition));
                        // Authorising read requests
                        if(this.originalRequest.getMethod().contains("GET")){
                            // No condition needed
                            if(condition == null || condition.length() == 0 || checkCondition(condition)){
                                // Applies to all fields
                                if(privilege.getAllFields()){
                                    this.condition = replacePlaceholders(condition);
                                    System.out.println(this.serviceUrl + route + remainingUrl + "?" + setQueryString(true, false));
                                  

                                    return this.serviceUrl + route + remainingUrl + "?$filter=" + replacePlaceholders(condition);
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
                return this.serviceUrl + this.resource + select + remainingUrl;
            } else {
                return "";
            }
           
        }
        else {
           if(checkWriteRequest()){
            System.out.println("dont");
           
            //return this.unmodifiedRequest;
            return "";
           } 
           else {
           
            return "";
           }

        }

    }

    
}
