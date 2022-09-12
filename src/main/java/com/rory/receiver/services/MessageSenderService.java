package com.rory.receiver.services;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

@Service
public class MessageSenderService {

    // URL of the Meta endpoint we use to send messages. Insert the WhatsApp ID (WAID) relevant to the Meta App you want
    // to send messages from (Not the phone number itself).
    private static final String urlString = "https://graph.facebook.com/v13.0/" + EnvironmentalVariableRetriever.WAID + "/messages";

    // The number to send messages to. Hardcoded in for this example.
//    private static final String toNumber = "91";

    public static ResponseEntity<String> sendMessage(String message,String toPhoneNumber) throws IOException {
        // Establish connection
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Add headers
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + EnvironmentalVariableRetriever.access_token);

        // Build body
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("messaging_product", "whatsapp");
        messageJSON.put("to",toPhoneNumber);

        JSONObject textJSON = new JSONObject();
        textJSON.put("body", message);

        messageJSON.put("text", textJSON);

        // Add body
        OutputStream outStream = connection.getOutputStream();
        outStream.write(messageJSON.toString().getBytes());

        // Get result and pass back
        return new ResponseEntity<>(connection.getResponseMessage(), HttpStatus.valueOf(connection.getResponseCode()));
    }

    public static ResponseEntity<String> sendTemplate(String template_name,String toPhoneNumber) throws IOException {

        // Establish connection
        URL url = new URL(MessageSenderService.urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Add headers
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + EnvironmentalVariableRetriever.access_token);

        // Build body
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("messaging_product", "whatsapp");
        messageJSON.put("to", toPhoneNumber);
        messageJSON.put("type", "template");

        JSONObject templateJSON = new JSONObject();
        templateJSON.put("name", template_name);

        JSONObject languageJSON = new JSONObject();
        languageJSON.put("code", "en_US");

        templateJSON.put("language", languageJSON);

        messageJSON.put("template", templateJSON);

        // Add body to connection
        OutputStream outStream = connection.getOutputStream();
        outStream.write(messageJSON.toString().getBytes());

        // Get result and pass back
        return new ResponseEntity<>(connection.getResponseMessage(), HttpStatus.valueOf(connection.getResponseCode()));
    }
}
