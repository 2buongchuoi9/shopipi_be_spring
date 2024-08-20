package shopipi.click.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Chat;
import shopipi.click.entity.User;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.ChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
  final ChatService chatService;

  @GetMapping("/list")
  public ResponseEntity<MainResponse<PageCustom<Chat>>> getChatsBetweenUsers(@RequestParam String userId_1,
      @RequestParam String userId_2,
      @PageableDefault(size = 10) Pageable pageable) {

    return ResponseEntity.ok(MainResponse.oke(chatService.getChatsBetweenUsers(userId_1, userId_2, pageable)));
  }

  @GetMapping("/user-chatted/{senderId}")
  public ResponseEntity<MainResponse<List<User>>> getUserChattedWithUserId(@PathVariable String senderId) {

    return ResponseEntity.ok(MainResponse.oke(chatService.getUserChattedWithUserId(senderId)));
  }

}
