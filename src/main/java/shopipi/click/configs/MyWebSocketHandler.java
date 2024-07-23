package shopipi.click.configs;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    // Handle message
    System.out.println("Received message: " + message.getPayload());

    // Example response
    session.sendMessage(new TextMessage("Response"));
  }
}
