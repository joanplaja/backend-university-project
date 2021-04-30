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

}
