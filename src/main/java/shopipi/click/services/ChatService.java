package shopipi.click.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.cloudinary.api.exceptions.NotFound;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Chat;
import shopipi.click.entity.User;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.repositories.ChatRepo;
import shopipi.click.repositories.UserRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepo chatRepo;
  private final UserRepo userRepo;
  private final MongoTemplate mongoTemplate;

  public Chat saveChat(Chat chat) {
    // người gửi
    if (!userRepo.existsById(chat.getSenderId()))
      throw new NotFoundError("Người gửi không tồn tại");
    // người nhận
    if (!userRepo.existsById(chat.getReceiverId()))
      throw new NotFoundError("Người nhận không tồn tại");

    return chatRepo.save(Chat.builder()
        .message(chat.getMessage())
        .receiverId(chat.getReceiverId())
        .senderId(chat.getSenderId())
        .type(chat.getType())
        .isRead(false)
        .build());
  }

  public PageCustom<Chat> getChatsBetweenUsers(String userId_1, String userId_2, Pageable pageable) {
    Query query = new Query();

    query.addCriteria(new Criteria().orOperator(
        Criteria.where("senderId").is(userId_1).and("receiverId").is(userId_2),
        Criteria.where("senderId").is(userId_2).and("receiverId").is(userId_1)));

    long total = mongoTemplate.count(query, Chat.class);

    query.with(pageable);
    List<Chat> list = mongoTemplate.find(query, Chat.class);

    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  public List<User> getUserChattedWithUserId(String userId) {
    Query query = new Query();
    query.addCriteria(new Criteria().orOperator(
        Criteria.where("senderId").is(userId),
        Criteria.where("receiverId").is(userId)));

    query.with(Sort.by(Sort.Order.desc("createdAt"))); // Sắp xếp theo thời gian tin nhắn gần nhất
    List<Chat> list = mongoTemplate.find(query, Chat.class);

    Set<String> set = list.stream()
        .flatMap(chat -> List.of(chat.getSenderId(), chat.getReceiverId()).stream())
        .filter(id -> !id.equals(userId))
        .collect(Collectors.toSet());

    return userRepo.findAllById(set);

  }

}
