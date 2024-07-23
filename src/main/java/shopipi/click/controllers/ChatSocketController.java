package shopipi.click.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {
  final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/chat.send/{recipientId}")
  @SendTo("/message")
  public void sendMessage(String message, @DestinationVariable String recipientId) {

    // Send message to specific user
    // messagingTemplate.convertAndSendToUser(recipientId, "/queue/messages",
    // message);
    messagingTemplate.convertAndSend("/message", message);
    System.out.println("Message sent to " + recipientId + ": " + message);
    // return message;
  }
}
