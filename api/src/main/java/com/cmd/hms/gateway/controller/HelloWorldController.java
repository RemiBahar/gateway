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
        String test = authentication.getAuthorities().toString();
        if(test.contains("USER")){
            System.out.println("hello" + test);
        }
        
        /*System.out.print(authentication.getAuthorities());
        boolean hasUserRole = authentication.getAuthorities().stream()
          .anyMatch(r -> r.getAuthority().equals("USER"));
          System.out.println(hasUserRole);*/
        return "Hello World";
    }
}