package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.repository.MessageRepository;
import org.udg.pds.springtodo.repository.ParticipantRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public MessageRepository crud(){ return messageRepository; }
}
