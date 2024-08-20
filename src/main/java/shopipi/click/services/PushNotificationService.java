package shopipi.click.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import shopipi.click.entity.Notification;
import shopipi.click.repositories.NotifyRepo;

// import org.springframework.http.codec.ServerSentEvent;
// import org.springframework.stereotype.Service;
// import reactor.core.publisher.Flux;
// import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
  private final NotifyRepo notifyRepo;

  // private List<Notification> getNotifies(String userId) {
  // var notifies = notifyRepo.findByUserToAndDeliveredFalse(userId);
  // notifies.forEach(x -> x.setDelivered(true));
  // notifyRepo.saveAll(notifies);
  // return notifies;
  // }

  // public Flux<ServerSentEvent<List<Notification>>>
  // getNotificationsByUserToId(String userId) {

  // if (userId != null && !userId.isBlank()) {
  // return Flux.interval(Duration.ofSeconds(1))
  // .publishOn(Schedulers.boundedElastic())
  // .map(sequence ->
  // ServerSentEvent.<List<Notification>>builder().id(String.valueOf(sequence))
  // .event("user-list-event").data(getNotifies(userId))
  // .build());
  // }

  // return Flux.interval(Duration.ofSeconds(1)).map(sequence ->
  // ServerSentEvent.<List<Notification>>builder()
  // .id(String.valueOf(sequence)).event("user-list-event").data(new
  // ArrayList<>()).build());
  // }
}
