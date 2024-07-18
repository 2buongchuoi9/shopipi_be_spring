package shopipi.click.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Chat;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.ChatService;

@Controller
public class ChatController {
  @Autowired
  SimpMessagingTemplate simpleMessageTemplate;
  @Autowired
  ChatService chatService;

  @MessageMapping("/chat")
  @SendTo("/topic/public")
  public Chat sendMessage(@Payload Chat chat) {
    System.out.println("chat: " + chat.toString());

    // save chat to database
    return chatService.saveChat(chat, chat.getSenderId());
  }

  @MessageMapping("/message")
  @SendTo("/chatroom/public")
  public Message receiveMessage(@Payload Message message) {
    System.out.println("mess::::::::::::::" + message.toString());
    return message;
  }

  @MessageMapping("/private-message")
  public Message recMessage(@Payload Message message) {
    simpleMessageTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
    System.out.println(message.toString());
    return message;
  }

}
