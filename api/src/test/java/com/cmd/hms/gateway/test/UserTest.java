package com.cmd.hms.gateway.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class UserTest extends IntegrationTest{
    
    @Test
    @Order(2)
    public void addUserMinimal(){
       // Prepare request
       String endPoint = "/Users";
       String requestBody = "{\n  \"UserName\": \"tester\" \n, \"Password\": \"$2a$12$AnJJhf5PICQ25vSzOo4AKe0mQcKbI3z8hDg24jSrvRTgfiiORfqRK\"}";

       // Perform add object test
       Boolean result = add(endPoint, requestBody, adminToken);
        
       // Check if true
       assertTrue(result);
    }

    @Test
    @Order(2)
    public void addUser(){
       // Prepare request
       String endPoint = "/Users";
       String requestBody = "{\n  \"UserName\": \"patient\" \n, \"Active\": true, \"PatientId\": \"1\",  \"Password\": \"$2a$12$AnJJhf5PICQ25vSzOo4AKe0mQcKbI3z8hDg24jSrvRTgfiiORfqRK\"}";

       // Perform add object test
       Boolean result = add(endPoint, requestBody, adminToken);
        
       // Check if true
       assertTrue(result);
    }

    @Test
    @Order(2)
    public void updateUser(){
       // Prepare request
       String endPoint = "/Users(4)";
       String requestBody = "{\n  \"UserName\": \"jon\" \n, \"PatientId\": \"2\",  \"Password\": \"$2a$12$h3BlDgol0ljs.MFh6AKNu.HzTDIKmgib0a33pVLeoQ8GoGwpnPXR6\"}";

       // Perform add object test
       Boolean result = update(endPoint, requestBody, adminToken);
        
       // Check if true
       assertTrue(result);
    }

    @Test
    @Order(2)
    public void invalidUsername(){
       // Prepare request
       String endPoint = "/Users(4)";
       String requestBody = "{\n  \"UserName\": \"\" \n, \"PatientId\": \"2\",  \"Password\": \"$2a$12$h3BlDgol0ljs.MFh6AKNu.HzTDIKmgib0a33pVLeoQ8GoGwpnPXR6\"}";

       // Perform add object test
       Boolean result = invalidUpdateObject(requestBody, endPoint, adminToken);
        
       // Check if true
       assertTrue(result);
    }

    @Test
    @Order(2)
    public void invalidPassword(){
       // Prepare request
       String endPoint = "/Users(4)";
       String requestBody = "{\n  \"UserName\": \"jon\" \n, \"PatientId\": \"2\",  \"Password\": \"\"}";

       // Perform add object test
       Boolean result = invalidUpdateObject(requestBody, endPoint, adminToken);
        
       // Check if true
       assertTrue(result);
    }

   @Test
   @Order(Ordered.LOWEST_PRECEDENCE)
	public void deleteUser() throws Exception {
        String endPoint = "/Users(5)";
        String url = this.baseUrl + endPoint;

        assertFalse(delete(url, adminToken));
	}

}