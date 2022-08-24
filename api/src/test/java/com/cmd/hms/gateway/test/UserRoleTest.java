package com.cmd.hms.gateway.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class UserRoleTest extends IntegrationTest{
    
    @Test
    @Order(2)
    public void addUserRole(){
       // Prepare request
       String endPoint = "/UserRoles";
       String requestBody = "{\n  \"RoleId\": \"1\", \"UserId\": \"1\" \n}";

       // Perform add object test
       Boolean result = add(endPoint, requestBody, adminToken);
        
       // Check if true
       assertTrue(result);
    }

    @Test
    @Order(2)
    public void updateUserRole(){
       // Prepare request
       String endPoint = "/UserRoles(2)";
       String requestBody = "{\n  \"RoleId\": \"3\", \"UserId\": \"3\" \n}";

       // Perform add object test
       Boolean result = update(endPoint, requestBody, adminToken);
        
       // Check if true
       assertTrue(result);
    }

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
	public void invalidRoleId() throws Exception {
        /*
         * Test that Title cannot be blank
         */
		String endPoint = "/UserRoles(2)";
        String requestBody = "{\n  \"RoleId\": \"100\", \"UserId\": \"3\" \n}";

		Boolean result = invalidUpdateObject(requestBody, endPoint, adminToken);
        assertTrue(result);
	}

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
	public void invalidUserId() throws Exception {
        /*
         * Test that Title cannot be blank
         */
		String endPoint = "/UserRoles(2)";
        String requestBody = "{\n  \"RoleId\": \"1\", \"UserId\": \"300\" \n}";

		Boolean result = invalidUpdateObject(requestBody, endPoint, adminToken);
        assertTrue(result);
	}

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
	public void deleteRole() throws Exception {
        String endPoint = "/UserRoles(3)";
        String url = this.baseUrl + endPoint;

        assertFalse(delete(url, adminToken));
	}

}
