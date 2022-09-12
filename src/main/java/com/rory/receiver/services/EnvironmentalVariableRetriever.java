package com.rory.receiver.services;

public class EnvironmentalVariableRetriever {

    public static final String access_token = System.getenv("ACCESS_TOKEN"); //ACCESS TOKEN FOR META ACCOUNT
    public static final String WAID = System.getenv("WAID"); //PHONE NUMBER ID
    public static final String verify_token = System.getenv("VERIFICATION_TOKEN"); // ANY RANDOM STRING
}
