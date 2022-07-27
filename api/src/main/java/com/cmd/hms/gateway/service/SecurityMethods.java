package com.cmd.hms.gateway.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cmd.hms.gateway.model.MyUserDetails;

public class SecurityMethods {
    public static Boolean hasRole(String role){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String[] roles = authentication.getAuthorities().toString().split(",", -1);
        for (int i = 0; i < roles.length; i++) {
            if(roles[i].contains(role)){
                return true;
            }
        }
        return false;
    }

    public static int getPatientId(){
        MyUserDetails user = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getPatientId();
    }

    public static Boolean userViewPatient(Long patientId, Long patientStatusId){
        return (hasRole("ROLE_USER") && getPatientId() == patientId && (patientStatusId == 2 || patientStatusId == 3));
    }
}
