package com.codewithsvns.relay.controller;

import com.codewithsvns.relay.dto.MessageResponse;
import com.codewithsvns.relay.dto.SendMessageRequest;
import com.codewithsvns.relay.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public MessageResponse sendMessage(
            Authentication authentication,
            @Valid @RequestBody SendMessageRequest request) {

        return messageService.sendMessage(
                authentication.getName(),
                request
        );
    }

    @GetMapping("/{userId}")
    public List<MessageResponse> getConversation(
            Authentication authentication,
            @PathVariable Long userId) {

        return messageService.getConversation(
                authentication.getName(),
                userId
        );
    }
}