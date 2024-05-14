package com.fitness.app.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @MessageMapping("/chat") // Endpoint para recibir mensajes de chat
    @SendTo("/topic/messages") // Envía los mensajes recibidos a todos los suscriptores del topic "/topic/messages"
    public String handleChatMessage(String message) {
        return "Usuario dice: " + message;
    }
}
