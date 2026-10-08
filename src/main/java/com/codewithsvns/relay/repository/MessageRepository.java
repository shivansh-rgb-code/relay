package com.codewithsvns.relay.repository;

import com.codewithsvns.relay.entity.Message;
import com.codewithsvns.relay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrderByCreatedAtAsc(
            User sender,
            User receiver
    );

    List<Message> findBySenderAndReceiverOrSenderAndReceiverOrderByCreatedAtAsc(
            User sender1,
            User receiver1,
            User sender2,
            User receiver2
    );
}