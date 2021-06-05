package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.UserSpecificationsBuilder;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.NotificationsService;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class is used to process all the authentication related URLs
@RequestMapping(path="/users")
@RestController
public class UserController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    NotificationsService notificationsService;

    @PostMapping(path="/login")
    @JsonView(Views.Private.class)
    public User login(HttpSession session, @Valid @RequestBody LoginUser user) {

        checkNotLoggedIn(session);

        User u = userService.matchPassword(user.username, user.password);
        session.setAttribute("simpleapp_auth_id", u.getId());
        userService.updateDeviceId(u, user.deviceId);
        return u;
    }

    @PostMapping(path="/logout")
    @JsonView(Views.Private.class)
    public String logout(HttpSession session) {

        getLoggedUser(session);

        session.removeAttribute("simpleapp_auth_id");
        return BaseController.OK_MESSAGE;
    }

    @GetMapping(path="/{id}")
    @JsonView(Views.Public.class)
    public User getPublicUser(HttpSession session, @PathVariable("id") Long userId) {

        getLoggedUser(session);

        return userService.getUser(userId);
    }

    @DeleteMapping(path="/{id}")
    public String deleteUser(HttpSession session, @PathVariable("id") Long userId) {

        Long loggedUserId = getLoggedUser(session);

        if (!loggedUserId.equals(userId))
            throw new ControllerException("You cannot delete other users!");

        userService.crud().deleteById(userId);
        session.removeAttribute("simpleapp_auth_id");

        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path="/signInFacebook", consumes = "application/json")
    public User sigInFacebook(HttpSession session,  @Valid  @RequestBody SignInUserFacebook ruFacebook) {

        checkNotLoggedIn(session);

        if(userService.getUserFacebook(ruFacebook.facebookId) == null)
            throw new ServiceException("Facebook id does not exists");

        User u = userService.signInFacebook(ruFacebook.facebookId, ruFacebook.facebookToken);
        session.setAttribute("simpleapp_auth_id", u.getId());
        userService.updateDeviceId(u, ruFacebook.deviceId);
        return u;
    }

    @PostMapping(path="/registerFacebook", consumes = "application/json")
    public String registerWithFacebook(HttpSession session,  @Valid  @RequestBody RegisterUserFacebook ruFacebook) {

        checkNotLoggedIn(session);
        //check if user email exists if it does update the facebook token
        //Otherwhise create the new user with facebook token
        User u = userService.registerFacebook(ruFacebook.username, ruFacebook.email, ruFacebook.password, ruFacebook.phoneNumber, ruFacebook.firstName, ruFacebook.lastName, ruFacebook.age, ruFacebook.facebookToken, ruFacebook.facebookId);
        userService.updateDeviceId(u, ruFacebook.deviceId);
        return BaseController.OK_MESSAGE;
    }


    @PostMapping(path="/register", consumes = "application/json")
    public String register(HttpSession session, @Valid  @RequestBody RegisterUser ru) {

        checkNotLoggedIn(session);

        userService.register(ru.username, ru.email, ru.password, ru.phoneNumber, ru.firstName, ru.lastName, ru.age);

        LoginUser loginUser = new LoginUser(ru.username, ru.password, ru.deviceId);
        login(session, loginUser);
        return BaseController.OK_MESSAGE;
    }

    @GetMapping(path="/me")
    @JsonView(Views.Private.class)
    public User getUserProfile(HttpSession session) {

        Long loggedUserId = getLoggedUser(session);

        return userService.getUserProfile(loggedUserId);
    }

    @PutMapping(path="/me", consumes = "application/json")
    public String updateUserMe(HttpSession session, @Valid  @RequestBody UpdateUser u) {

        //checkNotLoggedIn(session);
        Long loggedUserId = getLoggedUser(session);
        userService.updateUserMe(loggedUserId, u.username, u.description, u.phoneNumber, u.imageUrl);
        return BaseController.OK_MESSAGE;
    }

    @GetMapping(path="/me/equipment")
    @JsonView(Views.Complete.class)
    public User getUserEquipment(HttpSession session) {
        //PER FER, SIMILAR ALS TASKS AMB ELS TAGS
        Long loggedUserId = getLoggedUser(session);

        return userService.getUserEquipment(loggedUserId);
    }

    @GetMapping(path="/check/{token}")
    public String checkLoggedIn(HttpSession session, @PathVariable("token") String token) {

        Long userId = getLoggedUser(session);
        notificationsService.updateFirebaseToken(userId, token);

        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path="/follow/{id}")
    public String Follow(HttpSession session, @PathVariable("id") Long followId) {

        Long loggedUserId = getLoggedUser(session);
        User u = userService.getUser(loggedUserId);
        userService.addFollowing(loggedUserId, followId);
        User followed = userService.getUser(followId);
        if(followed.getDeviceId() != null) {
            notificationsService.sendFirebaseMessage(followed.getDeviceId(), u.getUsername());
        }

        return BaseController.OK_MESSAGE;

    }

    @DeleteMapping(path="/unfollow/{id}")
    public String unFollow(HttpSession session, @PathVariable("id") Long unfollowId) {

        Long loggedUserId = getLoggedUser(session);
        userService.removeFollow(loggedUserId, unfollowId);
        return BaseController.OK_MESSAGE;

    }

    @GetMapping(path="/following")
    @JsonView(Views.Public.class)
    public Collection<User> getFollowing(HttpSession session) {

        Long loggedUserId = getLoggedUser(session);

        return userService.getFollowing(loggedUserId);
    }

    @GetMapping(path="/followers")
    @JsonView(Views.Public.class)
    public Collection<User> getFollowers(HttpSession session) {

        Long loggedUserId = getLoggedUser(session);

        return userService.getFollowers(loggedUserId);
    }



    @GetMapping(path="/searchUsersWithoutChat")
    @JsonView(Views.Public.class)
    public List<User> searchUsersWithoutChat(HttpSession session,@RequestParam(value = "search") String search) {

        Long loggedUserId = getLoggedUser(session);

        search = "username:" + search;

        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        Specification<User> spec = builder.build();
        List<User> users = userService.findUser(spec);
        System.out.println(users);
        return userService.filterUsersWithoutChat(users,loggedUserId);
    }

    @RequestMapping(method = RequestMethod.GET)
    @JsonView(Views.Public.class)
    public List<User> search(@RequestParam(value = "search") String search) {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        Specification<User> spec = builder.build();
        return userService.findUser(spec);
    }

    @PostMapping(path="/findFacebookFriends")
    @JsonView(Views.Public.class)
    public List<User> findFacebookFriends(HttpSession session, @Valid  @RequestBody FindFacebookFriends fFacebookFreinds) {

        Long loggedUserId = getLoggedUser(session);

        return userService.findFacebookFriends(loggedUserId, fFacebookFreinds.facebookIds);

    }

    @PostMapping(path="/findPhoneFriends")
    @JsonView(Views.Public.class)
    public List<User> findPhoneFriends(HttpSession session, @Valid  @RequestBody FindPhoneFriends fPhoneFriends) {

        Long loggedUserId = getLoggedUser(session);
        return userService.findPhoneFriends(loggedUserId, fPhoneFriends.phoneNumbers);

    }

    @GetMapping(path="/id/{username}")
    @JsonView(Views.Public.class)
    public Long getUserId(HttpSession session, @PathVariable("username") String username) {
        getLoggedUser(session);

        return userService.getUserId(username);
    }

    @GetMapping(path="/following/{id}")
    @JsonView(Views.Public.class)
    public Collection<User> getFollowingOfUser(HttpSession session,@PathVariable("id") Long followingId) {

        getLoggedUser(session);

        return userService.getFollowing(followingId);
    }

    @GetMapping(path="/followers/{id}")
    @JsonView(Views.Public.class)
    public Collection<User> getFollowersOfUser(HttpSession session,@PathVariable("id") Long followersId) {

        getLoggedUser(session);

        return userService.getFollowers(followersId);
    }

    @GetMapping(path="/private")
    @JsonView(Views.Public.class)
    public String changePrivacy(HttpSession session) {

        Long loggedUserId = getLoggedUser(session);
        userService.changePrivacy(loggedUserId);
        return BaseController.OK_MESSAGE;
    }

    static class FindFacebookFriends {
        @NotNull
        public List<String> facebookIds;

    }

    static class FindPhoneFriends {
        @NotNull
        public List<String> phoneNumbers;

    }

    static class LoginUser {
        @NotNull
        public String username;
        @NotNull
        public String password;

        public String deviceId;

        public LoginUser(String Username, String Password, String deviceId){
            username = Username;
            password = Password;
            this.deviceId = deviceId;
        }
    }

    static class RegisterUser {
        @NotNull
        public String username;
        @NotNull
        public String email;
        @NotNull
        public String password;
        @NotNull
        public Integer phoneNumber;
        @NotNull
        public String firstName;
        @NotNull
        public String lastName;
        @NotNull
        public Integer age;

        public String deviceId;
    }

    static class RegisterUserFacebook {
        @NotNull
        public String username;
        @NotNull
        public String email;
        @NotNull
        public String password;
        @NotNull
        public Integer phoneNumber;
        @NotNull
        public String firstName;
        @NotNull
        public String lastName;
        @NotNull
        public Integer age;
        @NotNull
        public Long facebookId;
        @NotNull
        public String facebookToken;

        public String deviceId;
    }

    static class SignInUserFacebook {
        @NotNull
        public Long facebookId;
        @NotNull
        public String facebookToken;

        public String deviceId;
    }

    //classe per a que spring sapiga com mapejar el user que li arriba al body
    static class UpdateUser{
        @NotNull
        public String username;
        @NotNull
        public String description;
        @NotNull
        public Integer phoneNumber;
        @NotNull
        public String imageUrl;
    }

    static class ID {
        public Long id;

        public ID(Long id) {
            this.id = id;
        }
    }

}
