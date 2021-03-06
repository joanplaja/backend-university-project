package org.udg.pds.springtodo.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Workout;
import org.udg.pds.springtodo.repository.PostRepository;
import org.udg.pds.springtodo.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationsService {

    @Autowired
    UserService userService;

    public void sendFirebaseMessage(String token, String username) {

        // Data of your message. Key/value pairs accessible from an Android client as follows:
        // instanceOfRemoteData.getData().get("firstName")
        Message message = Message.builder().putData("type","notification")
            .setNotification(Notification.builder()
                .setTitle("@" + username)
                .setBody(" started following you!")
                .build())
            .setToken(token)
            .build();

        // Send the message with registration token and body to device.
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
            System.out.println(response);
            System.out.println("S'ha enviat la notificacio");
        } catch (FirebaseMessagingException e) {
            System.out.println("Error sending Firebase message: " + e.getMessage());
        }
    }

    public void sendFirebaseMessageChat(String token, String username,String m,Long userId,String img,String messageId) {

        if(img == null)img = "";
        // Data of your message. Key/value pairs accessible from an Android client as follows:
        // instanceOfRemoteData.getData().get("firstName")
        Message message = Message.builder().putData("type","message").putData("userId",userId.toString()).putData("img",img).putData("messageId",messageId)
            .setNotification(Notification.builder()
                .setTitle(username)
                .setBody(m)
                .build())
            .setToken(token)
            .build();

        // Send the message with registration token and body to device.
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
            System.out.println(response);
            System.out.println("S'ha enviat la notificacio");
        } catch (FirebaseMessagingException e) {
            System.out.println("Error sending Firebase message: " + e.getMessage());
        }
    }

    @Transactional
    public void updateFirebaseToken(Long userId, String token) {
        User user = userService.getUser(userId);
        userService.updateDeviceId(user, token);
    }
}
