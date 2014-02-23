package com.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.server.Server;

@ServerEndpoint("/echo")
public class EchoServerEndPoint {
	
	private static Map<Integer, Session> sessionMap = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session) {
        String queryStr = session.getQueryString();
        if (queryStr != null && queryStr.length() > 3) {
            Integer id = Integer.valueOf(queryStr.split("=")[1]);
            sessionMap.put(id, session);
        }
		System.out.println("[open]" + session);
	}
	
	@OnMessage
	public void onMessage(String message, Session session) throws Exception {
		System.out.println("[" + message + "]" + session);
        Long datetime = Long.valueOf(message);
        Long _id = datetime % 2;
        Integer id = _id.intValue();
        Session s = sessionMap.get(id);
        if (s == null) {
        	return;
        }
		s.getBasicRemote().sendText(message);
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("[close]" + session);
		sessionMap.clear();
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server("localhost", 8080, "/ws", null, EchoServerEndPoint.class);
		try {
			server.start();
			System.in.read();
		} finally {
			server.stop();
		}
	}
}
