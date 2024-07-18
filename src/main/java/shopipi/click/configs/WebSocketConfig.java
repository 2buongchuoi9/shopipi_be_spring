package shopipi.click.configs;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // registry.enableSimpleBroker("/topic", "/queue");
    // registry.setApplicationDestinationPrefixes("/app");
    // registry.setUserDestinationPrefix("/user");

    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/chatroom", "/user");
    registry.setUserDestinationPrefix("/user");

  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
    // .setInterceptors(new HandshakeInterceptor() {
    // @Override
    // public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse
    // response,
    // WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception
    // {

    // System.out.println("Attempting to connect...");

    // // security here

    // // attributes.put("user", user);

    // return true;
    // }

    // @Override
    // public void afterHandshake(ServerHttpRequest request, ServerHttpResponse
    // response, WebSocketHandler wsHandler,
    // @Nullable Exception exception) {

    // }
    // });
  }

  // @Override
  // public boolean configureMessageConverters(List<MessageConverter>
  // messageConverters) {
  // DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
  // resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
  // MappingJackson2MessageConverter converter = new
  // MappingJackson2MessageConverter();
  // converter.setObjectMapper(new ObjectMapper());
  // converter.setContentTypeResolver(resolver);
  // messageConverters.add(converter);
  // return false;
  // }

}
