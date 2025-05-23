package event.nbc.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import jakarta.servlet.http.HttpSession;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(
		ServerHttpRequest request,
		WebSocketHandler wsHandler,
		Map<String, Object> attributes
	) {
		if (request instanceof ServletServerHttpRequest servletRequest) {
			HttpSession session = servletRequest.getServletRequest().getSession();
			String sessionId = session.getId();
			return () -> sessionId;
		}
		return () -> UUID.randomUUID().toString();
	}
}