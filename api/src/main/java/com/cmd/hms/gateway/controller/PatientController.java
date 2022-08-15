package com.cmd.hms.gateway.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cmd.hms.gateway.model.MyUserDetails;
import com.cmd.hms.gateway.model.User;

@RestController
public class PatientController {

    private static final String patientApiPath = "http://127.0.0.1:8090/odata";
    private static final String[] PATIENT_PATHS = {"10", "20"};
  


/* Admins can do anything */
@RequestMapping(value={  "/api/patient/**"}, method = {RequestMethod.DELETE, RequestMethod.GET})
@Secured({"ROLE_ADMIN"})
public ResponseEntity<String> adminWriteRequest(HttpServletRequest request, Authentication authentication){ // Get requests wont have a requestBody
    System.out.println("Admin write request");
    return ControllerMethods.forwardRequest(request, patientApiPath, null);
}

@RequestMapping("/api/patient/**")
@Secured({"ROLE_ADMIN"})
public ResponseEntity<String> adminReadRequest(HttpServletRequest request, Authentication authentication, @RequestBody String requestBody){
    System.out.println("Admin read request");
    if(requestBody == null || requestBody == ""){
        return ControllerMethods.forwardRequest(request, patientApiPath, null);
    } else {
        return ControllerMethods.forwardRequest(request, patientApiPath, requestBody);
    }
    
}

/* Assistance can read, update or create patient data but not delete it */

@PatchMapping(value={  
    "/api/patient/Addresss{\\d+}/**",
    "/api/patient/Patients({\\d+}/**",
    "/api/patient/Contacts({\\d+}/**",
    "/api/patient/Patients/**",
    "/api/patient/Contacts/**",
    "/api/patient/Addresss/**"
})
@PostMapping(value={  
    "/api/patient/Addresss{\\d+}/**",
    "/api/patient/Patients({\\d+}/**",
    "/api/patient/Contacts({\\d+}/**",
    "/api/patient/Patients/**",
    "/api/patient/Contacts/**",
    "/api/patient/Addresss/**"
})
@PutMapping(value={  
    "/api/patient/Addresss{\\d+}/**",
    "/api/patient/Patients({\\d+}/**",
    "/api/patient/Contacts({\\d+}/**",
    "/api/patient/Patients/**",
    "/api/patient/Contacts/**",
    "/api/patient/Addresss/**"
})
@Secured({"ROLE_ADMIN", "ROLE_ASSISTANCE"})
public ResponseEntity<String> assistanceWrite(HttpServletRequest request, @RequestBody String requestBody){
    System.out.println("Assistance write request");
    return ControllerMethods.forwardRequest(request, patientApiPath, requestBody);
}

/* Patient */
 
@GetMapping(value={  
    "/api/patient/Addresss{\\d+}/**",
    "/api/patient/Patients({\\d+}/**",
    "/api/patient/Contacts({\\d+}/**",
    "/api/patient/Patients/**",
    "/api/patient/Contacts/**",
    "/api/patient/Addresss/**",
    "/api/patient/PatientAuds/**",
    "/api/patient/ContactAuds/**",
    "/api/patient/AddressAuds/**"
})
//@GetMapping(value={  "/api/patient/**"})
@Secured({"ROLE_ADMIN", "ROLE_ASSISTANCE", "ROLE_USER"})
public ResponseEntity<String> patientsReadRequest(HttpServletRequest request, Authentication authentication){
    System.out.println("Patient read request");
    String requestPath = request.getRequestURI().toString();
    MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();

    Boolean onlyUser = true;
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if(!grantedAuthority.toString().equals("ROLE_USER")){
                onlyUser = false;
                break;
            }
        }

        if(onlyUser && !request.getRequestURI().contains("(")){

        // Reduce amount of data being sent back to patient
        requestPath = requestPath.replace("Patients", "Patients(" + principal.getPatientId() + ")");
        requestPath = requestPath.replace("Addresss", "Patients(" + principal.getPatientId() + ")/AddressDetails");
        requestPath = requestPath.replace("Contacts", "Patients(" + principal.getPatientId() + ")/ContactDetails");

        return ControllerMethods.customGetRequest(request, requestPath, patientApiPath);  

        } else {
            return ControllerMethods.forwardRequest(request, patientApiPath, null);
        }

}



}