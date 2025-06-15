package com.ruoyi.web.websocket;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务器端点
 * 
 * @author ruoyi
 * @date 2025-01-XX
 */
@ServerEndpoint(value = "/websocket")
@Component
public class WebSocketServer {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    
    /** 存储所有连接的会话，key为用户ID */
    private static final ConcurrentHashMap<Long, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    
    /** 当前会话 */
    private Session session;
    
    /** 当前用户ID */
    private Long userId;
    
    /**
     * 连接建立成功调用的方法
     * 
     * @param session 会话
     */
    @OnOpen
    public void onOpen(Session session) {
        Long userId= SecurityUtils.getUserId();
        this.session = session;
        this.userId = userId;
        webSocketMap.put(userId, this);
        log.info("用户{}连接WebSocket成功，当前在线人数：{}", userId, webSocketMap.size());
        
        // 发送连接成功消息
        sendMessage("连接成功");
    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (userId != null) {
            webSocketMap.remove(userId);
            log.info("用户{}断开WebSocket连接，当前在线人数：{}", userId, webSocketMap.size());
        }
    }
    
    /**
     * 收到客户端消息后调用的方法
     * 
     * @param message 客户端发送过来的消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户{}的消息：{}", userId, message);
        
        // 可以在这里处理客户端发送的消息
        // 比如心跳检测、消息确认等
    }
    
    /**
     * 发生错误时调用
     * 
     * @param session 会话
     * @param error 错误信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户{}WebSocket发生错误：{}", userId, error.getMessage(), error);
    }
    
    /**
     * 发送消息给当前用户
     * 
     * @param message 消息内容
     */
    public void sendMessage(String message) {
        try {
            if (this.session != null && this.session.isOpen()) {
                this.session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            log.error("WebSocket发送消息失败，用户ID：{}，错误：{}", userId, e.getMessage(), e);
        }
    }
    
    /**
     * 发送消息给指定用户（静态方法）
     * 
     * @param userId 用户ID
     * @param message 消息内容
     * @return 是否发送成功
     */
    public static boolean sendMessageToUser(Long userId, String message) {
        WebSocketServer webSocketServer = webSocketMap.get(userId);
        if (webSocketServer != null) {
            webSocketServer.sendMessage(message);
            return true;
        } else {
            log.warn("用户{}未连接WebSocket，无法发送消息", userId);
            return false;
        }
    }
    
    /**
     * 批量发送消息给多个用户
     * 
     * @param userIds 用户ID列表
     * @param message 消息内容
     * @return 成功发送的用户数量
     */
    public static int sendMessageToUsers(List<Long> userIds, String message) {
        int successCount = 0;
        for (Long userId : userIds) {
            if (sendMessageToUser(userId, message)) {
                successCount++;
            }
        }
        log.info("批量发送消息完成，目标用户数：{}，成功发送：{}", userIds.size(), successCount);
        return successCount;
    }
    
    /**
     * 发送对象消息给指定用户
     * 
     * @param userId 用户ID
     * @param object 消息对象
     * @return 是否发送成功
     */
    public static boolean sendObjectToUser(Long userId, Object object) {
        try {
            String message = JSON.toJSONString(object);
            return sendMessageToUser(userId, message);
        } catch (Exception e) {
            log.error("发送对象消息失败，用户ID：{}，错误：{}", userId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 批量发送对象消息给多个用户
     * 
     * @param userIds 用户ID列表
     * @param object 消息对象
     * @return 成功发送的用户数量
     */
    public static int sendObjectToUsers(List<Long> userIds, Object object) {
        try {
            String message = JSON.toJSONString(object);
            return sendMessageToUsers(userIds, message);
        } catch (Exception e) {
            log.error("批量发送对象消息失败，错误：{}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * 获取当前在线用户数量
     * 
     * @return 在线用户数量
     */
    public static int getOnlineCount() {
        return webSocketMap.size();
    }
    
    /**
     * 获取所有在线用户ID
     * 
     * @return 在线用户ID列表
     */
    public static List<Long> getOnlineUserIds() {
        return new ArrayList<>(webSocketMap.keySet());
    }
    
    /**
     * 检查用户是否在线
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isUserOnline(Long userId) {
        return webSocketMap.containsKey(userId);
    }
} 