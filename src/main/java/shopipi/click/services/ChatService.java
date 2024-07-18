package shopipi.click.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Chat;
import shopipi.click.entity.User;
import shopipi.click.repositories.ChatRepo;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepo chatRepo;

  public Chat saveChat(Chat chat, String userId) {
    chat.setSenderId(userId);
    return chatRepo.save(chat);
  }
}
