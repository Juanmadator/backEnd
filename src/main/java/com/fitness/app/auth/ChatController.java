package com.fitness.app.auth;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @MessageMapping("/chat") // Endpoint para recibir mensajes de chat
    @SendTo("/topic/messages") // Envía los mensajes recibidos a todos los suscriptores del topic "/topic/messages"
    public String handleChatMessage(String message) {
        return "Usuario dice: " + message;
    }
}
