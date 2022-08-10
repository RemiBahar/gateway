package com.cmd.hms.gateway.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmd.hms.gateway.model.MyUserDetails;
import com.cmd.hms.gateway.model.User;

@RestController
public class PatientController {

    private static final String patientApiPath = "http://127.0.0.1:8090/odata";

    /* Patient API secure controllers */
    @RequestMapping(value={  
                "/api/patient/Addresss{\\d+}/**",
                "/api/patient/Patients({\\d+}/**",
                "/api/patient/Contacts({\\d+}/**",
                "/api/patient/Patients/**",
                "/api/patient/Contacts/**",
                "/api/patient/Addresss/**"
            })

    @Secured({"ROLE_ADMIN", "ROLE_ASSISTANCE"})
    public ResponseEntity<String> patientRequest(HttpServletRequest request, @RequestBody String requestBody){
        return ControllerMethods.forwardRequest(request, patientApiPath, requestBody);
    }

    @GetMapping(value={  
                "/api/patient/Patients/**",
                "/api/patient/Contacts/**",
                "/api/patient/Addresss/**"
    })
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_ASSISTANCE"})
    public ResponseEntity<String> patientsGetRequest(HttpServletRequest request, Authentication authentication){
        Boolean onlyUser = true;
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if(!grantedAuthority.toString().equals("ROLE_USER")){
                onlyUser = false;
                break;
            }
        }

        if(onlyUser){
            String requestPath = request.getRequestURI().toString();
            MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();
      
            requestPath = requestPath.replace("Patients", "Patients(" + principal.getPatientId() + ")");
            requestPath = requestPath.replace("Addresss", "Patients(" + principal.getPatientId() + ")/AddressDetails");
            requestPath = requestPath.replace("Contacts", "Patients(" + principal.getPatientId() + ")/ContactDetails");

            return ControllerMethods.customGetRequest(request, requestPath, patientApiPath);  
        } else {
            return ControllerMethods.forwardRequest(request, patientApiPath, null);
        }
    }
        

    @GetMapping(value={  
        "/api/patient/Addresss{\\d+}/**",
        "/api/patient/Patients({\\d+}/**",
        "/api/patient/Contacts({\\d+}/**",
    })
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_ASSISTANCE"})
    public ResponseEntity<String> patientGetRequest(HttpServletRequest request){
        return ControllerMethods.forwardRequest(request, patientApiPath, null);
    }


    @RequestMapping(value={"/api/patient/**"})
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> adminRequest(HttpServletRequest request, @RequestBody String requestBody){
        return ControllerMethods.forwardRequest(request, patientApiPath, requestBody);
    }

    @GetMapping(value={  
            "/api/patient/PatientAuds/**",
            "/api/patient/ContactAuds/**",
            "/api/patient/AddressAuds/**"
    })
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_ASSISTANCE"})
    public ResponseEntity<String> patientAuditLog(HttpServletRequest request, Authentication authentication) {
        Boolean onlyUser = true;

        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if(!grantedAuthority.toString().equals("ROLE_USER")){
                onlyUser = false;
                break;
            }
        }

        if(onlyUser){
            String requestPath = request.getRequestURI().toString();
            MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();
            

            requestPath = requestPath.replace("PatientAuds", "PatientAuds?$filter=PatientId eq " + principal.getPatientId());
            requestPath = requestPath.replace("AddressAuds", "AddressAuds?$filter=Patient eq " + principal.getPatientId());
            requestPath = requestPath.replace("ContactAuds", "ContactAuds?$filter=Patient eq " + principal.getPatientId());

            return ControllerMethods.customGetRequest(request, requestPath, patientApiPath);  
        } else {
            return ControllerMethods.forwardRequest(request, patientApiPath, null);
        }
     
    }
}