package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.tyrus.server.Server;

import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/reserve")
public class ReserveServerEndPoint {
	
	private static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session) {
        String queryStr = session.getQueryString();
        if (queryStr != null && queryStr.length() > 3) {
        	String id = getId(queryStr);
            sessionMap.put(getId(queryStr), session);
        }
		System.out.println("[open]" + session);
	}
	
	private String getId(String queryStr) {
		String[] params = queryStr.split("&");
		List<String> ids = new ArrayList<>();
		for (String param : params) {
			ids.add(param.split("=")[1]);
		}
		return StringUtils.join(ids, "_");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Reserve reserve = mapper.readValue(message, Reserve.class);
		Session s = sessionMap.get(reserve.accountId + "_" + reserve.storeId);
		String json = mapper.writeValueAsString(reserve);
		System.out.println(json);
		s.getBasicRemote().sendText(json);
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("[close]" + session);
		sessionMap.clear();
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server("localhost", 8080, "/ws", null, ReserveServerEndPoint.class);
		try {
			server.start();
			System.in.read();
		} finally {
			server.stop();
		}
	}
}
