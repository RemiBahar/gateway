package com.cmd.hms.gateway.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmd.hms.gateway.model.MyUserDetails;
@RestController
public class HelloWorldController {
    @RequestMapping({ "/Patients/*" })
    public String firstPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();
        
        System.out.println(principal.getUser().getRoles().toString());
        
       
        return "Hello World";
    }
}