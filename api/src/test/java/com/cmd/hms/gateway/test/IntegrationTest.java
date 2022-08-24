package com.cmd.hms.gateway.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
  webEnvironment = WebEnvironment.DEFINED_PORT,
  properties = {
    "server.port=8080"
  })
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:application-test.properties") // Uses postgres database in memory deleted after testing
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {
    HttpHeaders headers = null;
    RestTemplate restTemplate = null;
    static String adminToken;
    static String assistanceToken;
    static String userToken;
    List<MediaType> list = null;
    String baseUrl = null;

    IntegrationTest(){
        List<MediaType> list = new ArrayList<MediaType>();
		list.add(MediaType.APPLICATION_JSON);
        this.list = list;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(list);
        //headers.add("Authorization", "Bearer " + this.adminToken);
        this.headers = headers;
        this.restTemplate = new RestTemplate();

        this.baseUrl = "http://localhost:8080/odata";

       
    }
    /* 
    @BeforeAll
    static void beforeAll() {
        RestTemplate restTemplate = new RestTemplate();
       
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(list);

        System.out.println("AppleCalculatorTest.beforeAll");
        String requestBody = "{\n \"username\": \"admin\", \"password\": \"pass\" \n}";
        String url = "http://localhost:8080/authenticate";
         // Headers

         headers.setContentType(MediaType.APPLICATION_JSON);
		 list.add(MediaType.APPLICATION_JSON);
         //headers.setAccept(list);
         headers.set("accept", "application/json");
         
        // Send request
        HttpEntity<String> request = new HttpEntity<String>(requestBody, headers); 

        String postResponse = restTemplate.postForObject(url, request, String.class);

        System.out.println("initial.........................");
        System.out.println("initial.........................");
        System.out.println("initial.........................");
        System.out.println("initial.........................");
        System.out.println("initial.........................");
        System.out.println("initial.........................");
        System.out.println(postResponse);

    }*/
    

    public JSONObject responseToJson(String response){
        String jsonString = "{" + StringUtils.substringAfter(response, "{");
        JSONObject json;
        try {
            json = new JSONObject(jsonString).getJSONObject("d");
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String postRequest(String requestBody, String url, String token){
         // Headers
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
         List<MediaType> list = new ArrayList<MediaType>();
		 list.add(MediaType.APPLICATION_JSON);
         //headers.setAccept(list);
         headers.set("accept", "application/json");


         if(token != null){
            headers.add("Authorization", "Bearer " + token);
         }
         
        // Send request
        HttpEntity<String> request = new HttpEntity<String>(requestBody, headers); 

        String postResponse = this.restTemplate.postForObject(url, request, String.class);
        return postResponse;
    }

    public String patchRequest(String url, String requestBody, String token){
        try {
            // Create requestTemplate
            RestTemplate restTemplate = new RestTemplate();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            restTemplate.setRequestFactory(requestFactory);

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<MediaType> list = new ArrayList<MediaType>();
            list.add(MediaType.APPLICATION_JSON);
            //headers.setAccept(list);
            headers.set("accept", "application/json");
    
            if(token != null){
                headers.add("Authorization", "Bearer " + token);
            } else {
                headers.add("Authorization", "Bearer " + adminToken);
            }
            
            // Send request
            HttpEntity<String> request = new HttpEntity<String>(requestBody, headers); 
            ResponseEntity<String> response =  restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);

            return response.toString();
        } catch (Exception e) {
            System.out.println("Error making PATCH request");
            System.out.println("Error making PATCH request");
            System.out.println("Error making PATCH request");

            System.out.println(requestBody);
            System.out.println(token);
            System.out.println(e.getLocalizedMessage());

            return null;
        }
   }

    public Boolean hasRole(String token, String role){
        String[] chunks = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
        String authorities = StringUtils.substringBetween(payload, "Authorities\":[", "]");
        
        return authorities.contains(role);
    }


    @Test
    @Order(1)
    public void loginAdmin(){

        String requestBody = "{\n \"username\": \"admin\", \"password\": \"pass\" \n}";
        String url = "http://localhost:8080/authenticate";
        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON);
        //headers.setAccept(list);
        headers.set("accept", "application/json");
         
        // Send request
        HttpEntity<String> request = new HttpEntity<String>(requestBody, headers); 

        String postResponse = this.restTemplate.postForObject(url, request, String.class);

        adminToken = StringUtils.substringBetween(postResponse, "\"token\":\"", "\"");

        assertTrue(hasRole(adminToken, "ROLE_ADMIN")); 
    }

    


    public String getRequest(String url, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json, application/xml");

        if(token != null){
            headers.add("Authorization", "Bearer " + token);
        }
       
        HttpEntity<String> request = new HttpEntity<String>(headers);

        String getResponse = this.restTemplate.exchange(url, HttpMethod.GET, request, String.class).toString();

        return getResponse;
    }

    public Boolean add(String endPoint, String requestBody, String token){
        
        

        try {
            // Add object
            String postUrl = this.baseUrl + endPoint;
            String postResponse = "{" + StringUtils.substringAfter(postRequest(requestBody, postUrl, token), "{");

            // Get added object
            JSONObject postJson = new JSONObject(postResponse).getJSONObject("d").getJSONObject("__metadata");
            String getUrl = this.baseUrl + StringUtils.substringAfter(postJson.get("id").toString(), "/odata");
            String getResponse = getRequest(getUrl, token);

            // Compare request with added object
            String jsonString = "{" + StringUtils.substringAfter(getResponse, "{");
            JSONObject afterJson = new JSONObject(jsonString);

			JSONObject dataJson = new JSONObject(requestBody);
			Iterator<String> keys = dataJson.keys();

			while(keys.hasNext()) {
				String key = keys.next();
				if (dataJson.get(key) instanceof JSONObject) {
					assertEquals(dataJson.get(key), afterJson.get(key));
                    if(dataJson.get(key) != afterJson.get(key)){
                        System.out.println("requestBodyAdd: " + requestBody);
                        System.out.println("postUrlAdd: " + postUrl);
                        return false;
                    }
				}
            }

            return true; // will reach this if tests passed
        } catch(Exception e){
            System.out.print(e.getLocalizedMessage());
            System.out.println("requestBodyAdd: " + requestBody);
            System.out.println("postUrlAdd: " + this.baseUrl + endPoint);
            return false;
        }

    
    }

    

    public Boolean update(String endPoint, String requestBody, String token){
        try {
            // Prepare request
            String url = this.baseUrl + endPoint;
    
            // Request JSON
            JSONObject requestJson = new JSONObject(requestBody);
            

            // Get initial object
            JSONObject initialJson = responseToJson(getRequest(url, token));

            //Send request
            String response = patchRequest(url, requestBody, token);

            // Get updated object
            JSONObject updatedJson = responseToJson(getRequest(url, token));
            Iterator<String> keys = updatedJson.keys();

            while(keys.hasNext()) {
                String key = keys.next();

				if(!key.endsWith("Details") && !key.startsWith("__")){
                    if(requestJson.has(key)){
                        if(updatedJson.get(key).toString().startsWith("/")){
                            Long initialDate = Instant.parse(requestJson.get(key) + ".000Z").toEpochMilli(); // Convert request date to unix timestamp
                            String s = updatedJson.get(key).toString();
                            Long updatedDate = Long.parseLong(s.substring(s.indexOf("(") + 1, s.indexOf(")"))); // Get unix timestamp of get date

                            // Dates not equal
                            if((initialDate - updatedDate) != 0){
                                return false;
                            }
                        } else if(!requestJson.get(key).equals(updatedJson.get(key))) {
                            return false;
                        }
                    } else if(!initialJson.get(key).equals(updatedJson.get(key))) { // For fields not in the request
                        return false;
                    }
                } 
                
                return true;
            }

            return true;
        
        } catch(Exception e){
            System.out.println(e.getLocalizedMessage());
            return false;
        }
       
        
    }

    public Boolean invalidUpdateObject(String requestBody, String endpoint, String token)  {
        try {
            String url = this.baseUrl + endpoint;

            // Get initial address
            JSONObject beforeJson = responseToJson(getRequest(url, token));

            // Try invalid update
            String response = patchRequest(url, requestBody, token);

            // Get entity
            JSONObject afterJson = responseToJson(getRequest(url, token));
            
            // Compare object before and after invalid object
            // Object should be unchanged
            JSONObject dataJson = new JSONObject(requestBody);
            Iterator<String> keys = dataJson.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                
                if(!beforeJson.get(key).equals(afterJson.get(key))){
                    return false;
                }
            }
            
            return true;

		} catch(Exception e){
			return true;
		}
		
	}

    public String deleteRequest(String url, String  token){
        // Create requestTemplate
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON);
        //headers.setAccept(list);
        headers.set("accept", "application/json");

        if(token != null){
            headers.add("Authorization", "Bearer " + token);
        } else {
            headers.add("Authorization", "Bearer " + adminToken);
        }
        
        // Send request
        HttpEntity<String> request = new HttpEntity<String>(headers); 
        ResponseEntity<String> response =  restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        System.out.println("response: " + response);
        System.out.println("response: " + response);
        System.out.println("response: " + response);


        return response.toString();
    }

    public Boolean deleteObject(String endpoint, String token) {
		// Delete object
        String url = baseUrl + endpoint;
		String deleteResponse = deleteRequest(url, token);

		//Check if object deleted
        JSONObject getJson = responseToJson(getRequest(url, token));

        return getJson.has("error");
	}

	public Boolean delete(String url, String token) throws Exception {
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            
            HttpEntity<String> request = new HttpEntity<String>(headers);

            this.restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

            String endPoint = StringUtils.substringAfterLast(url, "/");
            String entity = "/" + StringUtils.substringBefore(endPoint, "(");

            String getEntities = getRequest(this.baseUrl + entity, token);
   
            return getEntities.contains(endPoint);
        } catch(Exception e){
            String endPoint = StringUtils.substringAfterLast(url, "/");
            String entity = "/" + StringUtils.substringBefore(endPoint, "(");

            String getEntities = getRequest(this.baseUrl + entity, token);
   
            return getEntities.contains(endPoint);
        }

	}

    public Boolean invalidGet(String endPoint, String token){
        try {
                String url = this.baseUrl + endPoint;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                List<MediaType> list = new ArrayList<MediaType>();
                list.add(MediaType.APPLICATION_JSON);
                headers.set("accept", "application/json");
                headers.add("Authorization", "Bearer " + token);
            
                HttpEntity<String> request = new HttpEntity<String>(headers);
                
                String getResponse = this.restTemplate.exchange(url, HttpMethod.GET, request, String.class).toString();
                System.out.println("getInvalidResponse: " + getResponse);
                String[] chunks = token.split("\\.");
                String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
                
                String authorities = StringUtils.substringBetween(payload, "Authorities\":[", "]");
                System.out.println("authorities: " + authorities);

                return false;
        } catch(Exception e){
            return true;
        }
    }

    public Boolean get(String endPoint, String token){
        try {
                String url = this.baseUrl + endPoint;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                List<MediaType> list = new ArrayList<MediaType>();
                list.add(MediaType.APPLICATION_JSON);
                headers.set("accept", "application/json");
                headers.add("Authorization", "Bearer " + token);
            
                HttpEntity<String> request = new HttpEntity<String>(headers);
                
                String getResponse = this.restTemplate.exchange(url, HttpMethod.GET, request, String.class).toString();
                System.out.println("getInvalidResponse: " + getResponse);
                String[] chunks = token.split("\\.");
                String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
                
                String authorities = StringUtils.substringBetween(payload, "Authorities\":[", "]");
                System.out.println("authorities: " + authorities);

                System.out.println(getResponse);

                return true;
        } catch(Exception e){
            System.out.println(e.getLocalizedMessage());
            return false;
        }
    }


}
