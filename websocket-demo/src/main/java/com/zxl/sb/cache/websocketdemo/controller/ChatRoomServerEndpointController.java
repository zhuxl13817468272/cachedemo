package com.zxl.sb.cache.websocketdemo.controller;

import com.zxl.sb.cache.websocketdemo.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Slf4j
@RestController
@ServerEndpoint("/chat-room/{username}")
public class ChatRoomServerEndpointController {

    @OnOpen
    public void openSession(@PathParam("username") String username, Session session){
        WebSocketUtils.LIVING_SESSIONS_CACHE.put(username,session);
        String message = "欢迎用户[" + username + "] 来到聊天室！";
        log.info(message);
        WebSocketUtils.sendMessageAll(message);
    }

    @OnMessage
    public void onMessage(@PathParam("username") String username,String message){
        log.info(message);
        WebSocketUtils.sendMessageAll(message);
    }

    @OnClose
    public void onClose(@PathParam("username") String username,Session session){
        WebSocketUtils.LIVING_SESSIONS_CACHE.remove(username);
        String message = "用户[" + username + "] 已经离开聊天室了！";
        WebSocketUtils.sendMessageAll(message);
        try{
            session.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session,Throwable throwable){
        try{
            session.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }

    @GetMapping("/chat-room/{sender}/to/{receiver}")
    public void onMessage(@PathVariable("sender") String sender, @PathVariable("receiver") String receiver, String message){
        WebSocketUtils.sendMessage(WebSocketUtils.LIVING_SESSIONS_CACHE.get(receiver),"[" + sender + "]" + "-> [" + receiver + "] : "+message);
        WebSocketUtils.sendMessage(WebSocketUtils.LIVING_SESSIONS_CACHE.get(sender),"[" + sender + "]" + "-> [" + receiver + "] : "+message);
    }

}
