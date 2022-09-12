package com.rory.receiver;

import com.rory.receiver.services.MessageReceiverService;
import com.rory.receiver.services.MessageSenderService;
import com.rory.receiver.services.VerificationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
public class GeneralController {
	
	@Autowired
	MessageReceiverService messageReceiverService;
	
    @GetMapping("/")
    public String mainPage(){
        return "Main page displayed successfully!";
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> verification(@RequestParam("hub.mode") String mode,
                                               @RequestParam("hub.verify_token") String verify_token,
                                               @RequestParam("hub.challenge") String challenge){
        return VerificationService.verify(mode, verify_token, challenge);
    }

    @PostMapping(value = "/webhook", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> receiveMessage(@RequestBody String jsonString){
        System.out.println("Message received.");
//        System.out.println(jsonString); //getting to phone number here
        messageReceiverService.sseEvent("MESSAGE", jsonString);
        return new ResponseEntity<>("Message Received", HttpStatus.OK);
    }

    @PostMapping(value = "/sendText", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> sendMessage(@RequestBody String messageJSONString){
        // Convert body to JSON and extract message
        String message;
        String toPhoneNumber;
        try {
            JSONObject messageJSON = new JSONObject(messageJSONString);
            message = messageJSON.getString("text");
            toPhoneNumber = messageJSON.getString("number");
        } catch (JSONException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        System.out.println("Message extracted properly...");

        try {
			return MessageSenderService.sendMessage(message,toPhoneNumber);
        } catch (MalformedURLException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/sendTemplate")
    public ResponseEntity<String> sendTemplate(@RequestParam("template_name") String template_name){
        try {
        	String toPhoneNumber="";
            return MessageSenderService.sendTemplate(template_name, toPhoneNumber);
        } catch (JSONException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping(path = "/sseEventInitiator", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter sseEventInitiator() {
		return messageReceiverService.sseEventInitiatorfunc();
	}	
	
	@GetMapping(path = "/sseEventEmit/{event}/{data}")
	public void sseEventEmit(@PathVariable(name="event")String event, @PathVariable(name="data")String data) {
		System.out.println(event);
		System.out.println(data);
		messageReceiverService.sseEventEmitfunc(event, data);
	}
	
	@PostMapping(path = "/sseEventEmitPost/{event}/{data}")
	public void sseEventEmitPost(@PathVariable(name="event")String event, @PathVariable(name="data")String data) {
		System.out.println(event);
		System.out.println(data);
		messageReceiverService.sseEventEmitfunc(event, data);
	}
}