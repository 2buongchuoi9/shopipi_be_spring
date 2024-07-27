package shopipi.click.controllers;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Chat;
import shopipi.click.entity.OnlineStatusUser;
import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.repositories.UserRepo;
import shopipi.click.services.ChatService;
import shopipi.click.services.UserService;

@Controller
@RequiredArgsConstructor
public class SocketController {
  final SimpMessagingTemplate messagingTemplate;
  final ChatService chatService;
  final UserService userService;
  final MongoTemplate mongoTemplate;

  @MessageMapping("/chat.send")
  // @SendTo("/message")
  public Chat sendMessage(@Payload Chat chat) {

    // @DestinationVariable String recipientId

    chat = chatService.saveChat(chat);
    System.out.println("người gửi: " + chat.getSenderId() + " người nhận: " +
        chat.getReceiverId()
        + " nội dung: " + chat.getMessage());

    messagingTemplate.convertAndSendToUser(chat.getReceiverId(), "/private", chat);

    return chat;

  }

  @MessageMapping("/online")
  public void handleOnlineStatus(String userId) {

    mongoTemplate.upsert(new Query(Criteria.where("userId").is(userId)), new Update()
        .set("isOnline", true)
        .set("time", LocalDateTime.now()), OnlineStatusUser.class);

  }

  @MessageMapping("/offline")
  public void handleOfflineStatus(String userId) {

    mongoTemplate.upsert(new Query(Criteria.where("userId").is(userId)), new Update()
        .set("isOnline", false)
        .set("time", LocalDateTime.now()), OnlineStatusUser.class);
  }

}
