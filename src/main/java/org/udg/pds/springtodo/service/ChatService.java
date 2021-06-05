package org.udg.pds.springtodo.service;

import io.minio.messages.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.ChatRepository;

import java.util.*;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    UserService userService;

    @Autowired
    ParticipantService participantService;

    @Autowired
    MessageService messageService;

    public ChatRepository crud(){ return chatRepository; }

    public List<Message> getChatMessages(Long chatId){
        try{
            Chat c = getChat(chatId);
            return (List<Message>) c.getMessages();
        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public Chat getChat(Long chatId){
        try {
            Optional<Chat> uo = chatRepository.findById(chatId);
            if (uo.isPresent())
                return uo.get();
            else
                throw new ServiceException(String.format("Chat with id = % dos not exists", chatId));
        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public Message addMessageToChat(Long userId,Long chatId,String message){
        try{

            Participant participant = participantService.findUserParticipantChat(userId,chatId);
            Chat c = this.getChat(chatId);

            Date d = new Date();
            Message m = new Message(message,d);
            m.setChat(c);
            m.setParticipant(participant);

            c.addMessage(m);
            messageService.crud().save(m);
            return m;
        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public IdObject createOneToOneChat(Long user1Id, Long user2Id){
        try {
            Chat chat = new Chat();
            chat.setType("normal");

            User user1 = userService.getUser(user1Id);
            User user2 = userService.getUser(user2Id);

            Participant p1 = new Participant();
            p1.setUser(user1);
            p1.setChat(chat);

            Participant p2 = new Participant();
            p2.setUser(user2);
            p2.setChat(chat);

            chat.addParticipant(p1);
            chat.addParticipant(p2);

            chatRepository.save(chat);
            return new IdObject(chat.getId());

        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public User findOtherUser(Long idUser, Long chatId){

        Chat chat = this.getChat(chatId);
        User u = null;

        List<Participant> chatParticipants = (List<Participant>) chat.getParticipants();
        Iterator<Participant> it = chatParticipants.iterator();
        while(it.hasNext()){
            Participant p2 = it.next();
            if(p2.getUserId() != idUser)u = p2.getUser();
        }
        return u;
    }

    public List<Chat> getChatsParticiping(Long userId){
        try {
            List<Chat> chats = new ArrayList<>();

            User user = userService.getUser(userId);
            List<Participant> participating = (List<Participant>) user.getParticipants();
            Iterator<Participant> iParticipant = participating.iterator();
            while(iParticipant.hasNext()){
                Participant p = iParticipant.next();
                Chat c = p.getChat();
                c.removeParticipant(p.getId());
                chats.add(c);
            }
            return  chats;

        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }
}
