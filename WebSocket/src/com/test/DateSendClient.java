package com.test;

import java.net.URI;
import java.util.Date;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class DateSendClient {
	
	private Session session;
	
    public DateSendClient() {
    }
    
    @OnOpen
    public void onOpen(Session session) {
    	this.session = session;
    }
    
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
    	session = null;
    }
    
    @OnMessage
    public void onMessage(String message) {
    	System.out.println("@@ " + message);
    }
    
    public static void main(String[] args) throws Exception {
    	URI uri = new URI("ws://localhost:8080/ws/echo");
        WebSocketContainer container = ContainerProvider
                    .getWebSocketContainer();
        Session session = container.connectToServer(DateSendClient.class, uri);
        Long datetime = null; 
        
    	for (;;) {
    		datetime = Long.valueOf(new Date().getTime());
    		session.getBasicRemote().sendText(datetime.toString());
    		Thread.sleep(10000);
    	}
    }
}
