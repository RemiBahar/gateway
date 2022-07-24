package com.cmd.hms.gateway.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloWorldController {
    @RequestMapping({ "/Patients/*" })
    public String firstPage() {
        return "Hello World";
    }
}