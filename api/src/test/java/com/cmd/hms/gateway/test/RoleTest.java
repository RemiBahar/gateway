package com.cmd.hms.gateway.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class RoleTest extends IntegrationTest{
    
    @Test
    @Order(2)
    public void addRole(){
       // Prepare request
       String endPoint = "/Roles";
       String requestBody = "{\n  \"Title\": \"ROLE_FIELD\" \n}";

       // Perform add object test
       Boolean result = add(endPoint, requestBody, adminToken);
        
       // Check if true
       assertTrue(result);
    }

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void updateRole(){
        String endPoint = "/Roles(4)";
        String requestBody = "{\n  \"Title\": \"ROLE_DOCTOR\" \n}";

        Boolean result = update(endPoint, requestBody, adminToken);
        assertTrue(result);
    }

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
	public void invalidUpdateRole() throws Exception {
        /*
         * Test that Title cannot be blank
         */
		String requestBody = "{\n \"Title\": \"\" \n}";
		String endpoint = "/Roles(4)";

		Boolean result = invalidUpdateObject(requestBody, endpoint, adminToken);
        assertTrue(result);
	}

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
	public void deleteRole() throws Exception {
        String endPoint = "/Roles(5)";
        String url = this.baseUrl + endPoint;

        assertFalse(delete(url, adminToken));
	}

}
