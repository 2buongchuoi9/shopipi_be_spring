package shopipi.click.configs;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.KeyToken;
import shopipi.click.entity.UserRoot;
import shopipi.click.exceptions.NoAuthorizeError;
import shopipi.click.repositories.KeyTokenRepo;
import shopipi.click.security.JwtAuthenticationFilter;
import shopipi.click.services.JwtService;
import shopipi.click.services.UserRootService;
import shopipi.click.utils.Constants.HEADER;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  // private final KeyTokenRepo keyRepo;
  // private final JwtService jwtService;
  // private final UserRootService userRootService;

  // @Bean
  // public ThreadPoolTaskScheduler taskScheduler() {
  // ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
  // taskScheduler.setPoolSize(1);
  // taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
  // taskScheduler.initialize();
  // return taskScheduler;
  // }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {

    // ThreadPoolTaskScheduler taskScheduler = taskScheduler();

    registry.setApplicationDestinationPrefixes("/app");

    registry.enableSimpleBroker("/user", "/queue", "/message");
    // .setHeartbeatValue(new long[] { 10000, 10000 })
    // .setTaskScheduler(taskScheduler);

    registry.setUserDestinationPrefix("/user");

  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws");
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();

    // .setInterceptors(new HandshakeInterceptor() {
    // @Override
    // public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse
    // response,
    // WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception
    // {

    // final var holder = new Object() {
    // String clientId = null;
    // String token = null;
    // };

    // request.getHeaders().forEach((k, v) -> {
    // System.out.println(k + " : " + v);
    // if (k.equals(HEADER.X_CLIENT_ID))
    // holder.clientId = v.get(0);

    // if (k.equals(HEADER.AUTHORIZATION))
    // holder.token = v.get(0);
    // });

    // if (holder.clientId == null)
    // throw new NoAuthorizeError("x-client-id has must in header");

    // if (holder.token == null)
    // throw new NoAuthorizeError("authorization has must in header");

    // KeyToken keyStore = keyRepo.findByUserId(holder.clientId)
    // .orElseThrow(() -> new NoAuthorizeError("invalid x-client-id in header"));

    // String userEmail = jwtService.verifyToken(holder.token,
    // jwtService.getPublicKeyFromString(keyStore.getPublicKey()));
    // if (userEmail == null)
    // throw new NoAuthorizeError("decode token is fail");

    // UserDetails userDetails = userRootService.loadUserByUsername(userEmail);

    // if (userDetails != null) {
    // // Đặt đối tượng UserRoot vào attributes
    // attributes.put("user", ((UserRoot) userDetails).getUser().getId());
    // return true;
    // }

    // return false;
    // }

    // @Override
    // public void afterHandshake(ServerHttpRequest request, ServerHttpResponse
    // response, WebSocketHandler wsHandler,
    // @Nullable Exception exception) {

    // }
    // });
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration.setSendTimeLimit(15_000);
    registration.setSendBufferSizeLimit(512 * 1024);
    registration.setMessageSizeLimit(128 * 1024);
    // registration.setSubProtocolHandler(new DefaultSubProtocolHandler());
  }

}
