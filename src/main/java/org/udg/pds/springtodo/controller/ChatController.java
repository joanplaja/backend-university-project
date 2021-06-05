package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.ChatService;
import org.udg.pds.springtodo.service.NotificationsService;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RequestMapping(path="/chats")
@RestController
public class ChatController extends BaseController {

    @Autowired
    ChatService chatService;

    @Autowired
    UserService userService;

    @Autowired
    NotificationsService notificationService;

    @PostMapping
    @JsonView(Views.Private.class)
    public Long createChat(HttpSession session, @Valid @RequestBody  CreateChat createChat) {

        Long loggedUserId = getLoggedUser(session);
        Long otherUserId = createChat.userId;

        return chatService.createOneToOneChat(loggedUserId,otherUserId).getId();
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public List<Chat> getChats(HttpSession session) {
        Long userId = getLoggedUser(session);

        return chatService.getChatsParticiping(userId);
    }

    @GetMapping(path ="/{id}/messages")
    @JsonView(Views.Private.class)
    public List<Message> getChatMessages(HttpSession session, @PathVariable("id") Long chatId) {
        Long userId = getLoggedUser(session);

        return chatService.getChatMessages(chatId);
    }



    @PostMapping(path ="/{id}/messages")
    @JsonView(Views.Private.class)
    public Message sendMessage(HttpSession session,@PathVariable("id") Long chatId, @Valid @RequestBody  SendMessageChat sendMessageChat) {

        Long loggedUserId = getLoggedUser(session);
        User u = userService.getUser(loggedUserId);

        Message m = chatService.addMessageToChat(loggedUserId,chatId,sendMessageChat.message);
        User sender = chatService.findOtherUser(loggedUserId,chatId);

        if(sender.getDeviceId() != null) {
            notificationService.sendFirebaseMessageChat(sender.getDeviceId(), u.getUsername(),sendMessageChat.message,u.getId(),u.getImageUrl(),m.getId().toString());
        }

        return m;

    }

    static class CreateChat {
        @NotNull
        public long userId;
    }

    static class SendMessageChat{
        @NotNull
        public String message;
    }


}
