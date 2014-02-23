package com.test;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class BasicClient {
	
	private Session session;
	
    public BasicClient() {
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
    	URI uri = new URI("ws://localhost:8080/ws/echo?id=" + args[0]);
        WebSocketContainer container = ContainerProvider
                    .getWebSocketContainer();
        Session session = container.connectToServer(BasicClient.class, uri);
        Set<MessageHandler> handlers = session.getMessageHandlers();
        for (MessageHandler handler : handlers) {
        	session.removeMessageHandler(handler);
        }
        session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				System.out.println(message);
			}
		});
        System.in.read();
    }
}
