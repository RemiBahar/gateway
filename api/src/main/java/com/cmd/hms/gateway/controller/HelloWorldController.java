package com.cmd.hms.gateway.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmd.hms.gateway.model.MyUserDetails;
import com.cmd.hms.gateway.model.Privilege;
import com.cmd.hms.gateway.model.Role;
import com.cmd.hms.gateway.model.User;
@RestController
public class HelloWorldController {
    String api = "api";

  
    public Boolean checkCondition(String request){
        System.out.println(request);
        return true;
    }

    public Boolean checkWriteRequest(){
        return true;
    }

    public String replacePlaceholders(String condition, User user){
        Map<String, String> map= new HashMap();
        map.put("PatientId", user.getPatientId().toString());
       
        String result = map.keySet()
                   .stream()
                   .reduce(condition, (s, e) -> s.replaceAll("%"+e+"\\b", map.get(e)));
        
        return result;

    }

    public String allowedRequest(HttpServletRequest httpServletRequest, User user){
        // Get requested resource name
        String originalRequest = httpServletRequest.getRequestURI();

        String[] test = httpServletRequest.getRequestURI().split("/");


        String route = httpServletRequest.getRequestURI().split("/")[3];
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(route);
        matcher.find();
        String resource = matcher.group();
        
        String filter = "$filter=";
        HashMap<String, Boolean> allowedWriteFields = new HashMap<String, Boolean>();

        // Check privileges
        List<Role> roles = user.getRoles();

        // Iterate over roles
        for(Role role : roles){
            // Admin can perform any action
            if(role.getTitle().toLowerCase().contains("admin")){
                return originalRequest;
            }

            List<Privilege> privileges = role.getPrivileges();

            // Iterate over privileges
            for(Privilege privilege : privileges){
                // If privilege is for requested resource
                if(resource.equals(privilege.getPrivilegeClass().getName() + "s")){ 

                    // If privilege allows for requested method
                    if(privilege.allowedMethod(httpServletRequest.getMethod())) {
                        String condition = privilege.getCondition();

                        System.out.println(replacePlaceholders(condition, user));
                        // Authorising read requests
                        if(httpServletRequest.getMethod().contains("GET")){
                            // No condition needed
                            if(condition == null || condition.length() == 0 || checkCondition(originalRequest + condition)){
                                // Applies to all fields
                                if(privilege.getAllFields()){
                                    return originalRequest;
                                } else {
                                    filter += privilege.getField().getName();
                                }
                            }
                            
                        } 
                        // Authorising write requests
                        else {
                            // No condition needed
                            if(condition == null || condition.length() == 0 || checkCondition(originalRequest + condition)){
                                // Applies to all fields
                                if(privilege.getAllFields()){
                                    return originalRequest;
                                } else {
                                    // Field now allowed
                                    allowedWriteFields.put(privilege.getField().getName(), true);
                                }
                            }


                        }

                    }
               
               
                }// == checks reference to object not the actual string

              

            }


        }

        
        if(httpServletRequest.getMethod().contains("GET")){
            return originalRequest + filter;
        }
        else {
           if(checkWriteRequest()){
            return originalRequest;
           } 
           else {
            return "";
           }

        }

    }

    @RequestMapping({ "/api/patient/**" })
    public String firstPage(HttpServletRequest httpServletRequest) {
        String serviceUrl = "http://130.211.227.209:8090/odata/";


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();

        AuthoriseRequest AuthoriseRequest = new AuthoriseRequest(httpServletRequest, principal.getUser(), serviceUrl);
        String response = AuthoriseRequest.forwardRequest();

        

        //String forwardRequest = allowedRequest(httpServletRequest, principal.getUser());

        
        return response;
    }
}