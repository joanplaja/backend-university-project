package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.UserSpecificationsBuilder;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    @PostMapping(path="/login")
    @JsonView(Views.Private.class)
    public User login(HttpSession session, @Valid @RequestBody LoginUser user) {

        checkNotLoggedIn(session);

        User u = userService.matchPassword(user.username, user.password);
        session.setAttribute("simpleapp_auth_id", u.getId());
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


    @PostMapping(path="/register", consumes = "application/json")
    public String register(HttpSession session, @Valid  @RequestBody RegisterUser ru) {

        checkNotLoggedIn(session);

        userService.register(ru.username, ru.email, ru.password, ru.phoneNumber, ru.firstName, ru.lastName, ru.age);

        LoginUser loginUser = new LoginUser(ru.username, ru.password);
        login(session, loginUser);
        return BaseController.OK_MESSAGE;
    }

    @GetMapping(path="/me")
    @JsonView(Views.Complete.class)
    public User getUserProfile(HttpSession session) {

        Long loggedUserId = getLoggedUser(session);

        return userService.getUserProfile(loggedUserId);
    }

    @GetMapping(path="/me/equipment")
    @JsonView(Views.Complete.class)
    public User getUserEquipment(HttpSession session) {
        //PER FER, SIMILAR ALS TASKS AMB ELS TAGS
        Long loggedUserId = getLoggedUser(session);

        return userService.getUserEquipment(loggedUserId);
    }

    @GetMapping(path="/check")
    public String checkLoggedIn(HttpSession session) {

        getLoggedUser(session);

        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path="/follow/{id}")
    public String Follow(HttpSession session, @PathVariable("id") Long followId) {

        Long loggedUserId = getLoggedUser(session);
        userService.addFollowing(loggedUserId, followId);
        return BaseController.OK_MESSAGE;

    }

    @PostMapping(path="/unfollow/{id}")
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

    @GetMapping(path="/id/{username}")
    @JsonView(Views.Public.class)
    public Long getUserId(HttpSession session, @PathVariable("username") String username) {
        getLoggedUser(session);

        return userService.getUserId(username);
    }

    static class LoginUser {
        @NotNull
        public String username;
        @NotNull
        public String password;

        public LoginUser(String Username, String Password){
            username = Username;
            password = Password;
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
    }

    static class ID {
        public Long id;

        public ID(Long id) {
            this.id = id;
        }
    }

}
