package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.PointService;
import org.udg.pds.springtodo.service.PostService;
import org.udg.pds.springtodo.service.RouteService;
import org.udg.pds.springtodo.service.WorkoutService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RequestMapping(path="/posts")
@RestController
public class PostController extends BaseController {

    @Autowired
    WorkoutService workoutService;

    @Autowired
    PostService postService;

    @GetMapping
    @JsonView(Views.Private.class)
    public List<Post> getPosts(HttpSession session) {
        Long userId = getLoggedUser(session);

        return postService.getPosts(userId);
    }

    @PostMapping(consumes = "application/json")
    public IdObject addPost(HttpSession session, @Valid @RequestBody R_Post post) {
        Long userId = getLoggedUser(session);

        IdObject postId = postService.addPost(userId, post.workoutId, post.description, post.imageUrl);
        return postId;
    }

    @DeleteMapping(path="/{id}")
    public String deletePost(HttpSession session,
                                @PathVariable("id") Long postId) {
        getLoggedUser(session);
        postService.deletePost(postId);
        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path="like/{id}")
    public String likePost(HttpSession session,
                           @PathVariable("id") Long postId) {
        Long userId = getLoggedUser(session);
        postService.likePost(userId,postId);
        return BaseController.OK_MESSAGE;
    }

    @GetMapping(path="/likes/{id}")
    @JsonView(Views.Public.class)
    public Collection<User> getLikes(HttpSession session,
                                     @PathVariable("id") Long postId){
        getLoggedUser(session);
        return postService.getLikes(postId);

    }

    @DeleteMapping(path="/removelike/{id}")
    public String removeLike(HttpSession session, @PathVariable("id") Long removeLikeId) {

        Long loggedUserId = getLoggedUser(session);
        postService.removeLike(loggedUserId, removeLikeId);
        return BaseController.OK_MESSAGE;

    }

    static class R_Post {
        @NotNull
        public Long workoutId;

        public String description;

        public String imageUrl;
    }
}
