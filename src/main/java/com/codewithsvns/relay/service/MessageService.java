package com.codewithsvns.relay.service;

import com.codewithsvns.relay.dto.MessageResponse;
import com.codewithsvns.relay.dto.SendMessageRequest;
import com.codewithsvns.relay.entity.Message;
import com.codewithsvns.relay.entity.User;
import com.codewithsvns.relay.repository.MessageRepository;
import com.codewithsvns.relay.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public MessageResponse sendMessage(
            String senderEmail,
            SendMessageRequest request) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Receiver not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException(
                    "You cannot send a message to yourself"
            );
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());

        Message savedMessage = messageRepository.save(message);

        return toResponse(savedMessage);
    }

    public List<MessageResponse> getConversation(
            String currentUserEmail,
            Long otherUserId) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Current user not found"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        List<Message> messages =
                messageRepository
                        .findBySenderAndReceiverOrSenderAndReceiverOrderByCreatedAtAsc(
                                currentUser,
                                otherUser,
                                otherUser,
                                currentUser
                        );

        return messages.stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}