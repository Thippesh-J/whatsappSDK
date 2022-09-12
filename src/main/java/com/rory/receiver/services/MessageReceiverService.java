package com.rory.receiver.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageReceiverService {

    public static ResponseEntity<String> wiseManEcho(JSONObject jsonObject) {
        // When message is received, responds with the same message content prepended with 'a wise man once said'
//    	[{"from":"","id":"id","text":{"body":"Hi"},"type":"text","timestamp":"1659957174"}]
        System.out.println("Echoing as wise man...");
        try {
            JSONArray messages = jsonObject.getJSONArray("entry").getJSONObject(0).getJSONArray("changes").getJSONObject(0).getJSONObject("value").getJSONArray("messages");
            for (int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                String fromNumber = message.getString("from");
                JSONObject messageTextObject = message.getJSONObject("text");
                String content = messageTextObject.getString("body");
                System.out.println(content);
                if(content.startsWith("LEAP INFYAR")) {
                	MessageSenderService.sendMessage("Dear Requestor,\n"
                			+ "\n"
                			+ "Thanks for contacting PTC team. \n"
                			+ "\n"
                			+ "We will address your query at the earliest for CaseId:AR:00000070\n"
                			+ "\n"
                			+ "Your patience and co-operation is appreciated.\n"
                			+ "\n"
                			+ "Regards,\n"
                			+ "PTC Team  \n"
                			+ " ",fromNumber);
                	System.out.println("CASE CREATED WITH :"+content);
                }
                else if(content.startsWith("LEAP INFYVR")) {
                	MessageSenderService.sendMessage("Dear Requestor,\n"
                			+ "\n"
                			+ "Thanks for contacting PTC team. \n"
                			+ "\n"
                			+ "We will address your query at the earliest for CaseId:VR:00000070\n"
                			+ "\n"
                			+ "Your patience and co-operation is appreciated.\n"
                			+ "\n"
                			+ "Regards,\n"
                			+ "PTC Team  \n"
                			+ " ",fromNumber);
                	System.out.println("CASE CREATED WITH :"+content);
                }
                else {
                	MessageSenderService.sendMessage("THANK YOU FOR CONTACTING, WE CAN'T PROCESS YOUR REQUEST AT THIS TIME",fromNumber);
                	System.out.println("NO PROCESS FOUND"+content);
                }
            }
            return new ResponseEntity<>("Message Received", HttpStatus.OK);
        } catch (JSONException | IOException e) {
            return new ResponseEntity<>("Request body JSON improperly formed", HttpStatus.BAD_REQUEST);
        }
    }
    
    
    List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	
	
	public void sseEventEmitfunc(String event,String data) {
		System.out.println(event);
		System.out.println(data);
		for (SseEmitter emitter : emitters) {
			try {

				emitter.send(SseEmitter.event().name(event).data(data));
			}

			catch (IOException e) {
				emitters.remove(emitter);
			}
		}
	}
	
	public SseEmitter sseEventInitiatorfunc() {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		try {
			emitter.send(SseEmitter.event().name("INIT"));

		} catch (IOException e) {
			System.out.println(e);
		}
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitters.add(emitter);
		return emitter;
	}
    
	public void sseEvent(String event, String data) {
		
		for (SseEmitter emitter : emitters) {
			try {

				emitter.send(SseEmitter.event().name(event).data(data));
			}

			catch (IOException e) {
				emitters.remove(emitter);
			}
		}

	}
    
	public void sseEventComplete() {
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("close").data("COMPLETED"));
				emitter.complete();
			} catch (Exception e) {
				emitters.remove(emitter);
				System.out.println(e);
			}
		}
	}

}

