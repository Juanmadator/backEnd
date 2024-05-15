//package com.fitness.app.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.messaging.support.ExecutorSubscribableChannel;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer {
//
//    @Bean
//    public SimpMessagingTemplate messagingTemplate(MessageChannel clientOutboundChannel) {
//        return new SimpMessagingTemplate(clientOutboundChannel);
//    }
//
//    @Bean
//    public MessageChannel clientInboundChannel() {
//        return new ExecutorSubscribableChannel();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic"); // Habilitar el broker para mensajes destinados a "/topic"
//        config.setApplicationDestinationPrefixes("/app"); // Prefijo para mensajes dirigidos a métodos anotados con @MessageMapping
//    }
//
//
//}
