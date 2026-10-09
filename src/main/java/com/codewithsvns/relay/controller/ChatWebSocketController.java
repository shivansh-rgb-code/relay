package com.codewithsvns.relay.controller;

import com.codewithsvns.relay.dto.MessageResponse;
import com.codewithsvns.relay.dto.SendMessageRequest;
import com.codewithsvns.relay.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(
            MessageService messageService,
            SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(
            Principal principal,
            @Valid SendMessageRequest request) {

        String senderEmail = principal.getName();

        MessageResponse savedMessage =
                messageService.sendMessage(senderEmail, request);

        messagingTemplate.convertAndSendToUser(
                getRecipientEmail(request.getReceiverId()),
                "/queue/messages",
                savedMessage
        );
    }

    private String getRecipientEmail(Long receiverId) {
        // Resolve the receiver's email through the existing service.
        return messageService.getUserEmail(receiverId);
    }
}
