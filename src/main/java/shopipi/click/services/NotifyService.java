package shopipi.click.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Notification;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.repositories.NotifyRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;

@Service
@RequiredArgsConstructor
public class NotifyService {
  private final NotifyRepo notifyRepo;
  private final MongoTemplate mongoTemplate;

  public Notification createNotification(Notification notification) {
    return notifyRepo.save(notification);
  }

  public Notification getNotificationsById(String id) {
    return notifyRepo.findById(id).orElseThrow(() -> new NotFoundError("notification not found!"));
  }

  public List<Notification> getNotificationsByUserIdNotRead(String userId) {
    return notifyRepo.findByUserToAndDeliveredFalse(userId);
  }

  public PageCustom<Notification> getNotificationsByUserId(String userId, Pageable pageable) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userTo").is(userId));

    long total = mongoTemplate.count(query, Notification.class);
    List<Notification> notifications = mongoTemplate.find(query.with(pageable), Notification.class);
    return new PageCustom<>(PageableExecutionUtils.getPage(notifications, pageable, () -> total));
  }

  public Notification changeNotifyStatusToRead(String notifyId) {
    var notify = notifyRepo.findById(notifyId)
        .orElseThrow(() -> new NotFoundError("not found!"));
    notify.setRead(true);
    return notifyRepo.save(notify);
  }

}
