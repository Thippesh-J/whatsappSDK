package com.rory.receiver.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    public static ResponseEntity<String> verify(String mode, String verify_token, String challenge){
        System.out.println("Attempting verification...");
        // Check all fields are present
        if (mode != null && verify_token != null && challenge != null){
            // Check that mode and verification token are correct
            if (mode.equals("subscribe") && verify_token.equals(EnvironmentalVariableRetriever.verify_token)){
                // Since they are correct, we need to respond with the challenge as per documentation.
                return new ResponseEntity<>(challenge, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Verification failed. Check that hub.mode and hub.verify_token are correct.", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("Verification failed. Check all fields are present.", HttpStatus.FORBIDDEN);
        }
    }
}
