package cat.santfeliu.api.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import cat.santfeliu.api.dto.ConnectorStatusDTO;

@Controller
public class SocketApiController {
	
	@MessageMapping("/status")
	@SendTo("/topic/status")
	public ConnectorStatusDTO status(ConnectorStatusDTO status) throws Exception {
	    return status;
	}
}
