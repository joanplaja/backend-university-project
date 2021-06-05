package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.NotificationsService;
import org.udg.pds.springtodo.service.PostService;
import org.udg.pds.springtodo.service.WorkoutService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping(path="/notifications")
@RestController
public class NotificationsController extends BaseController {

    private NotificationsService notificationsService;

    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @PostMapping(path = "/update-token", consumes = "application/json")
    public String updateToken(HttpSession session, @Valid @RequestBody String token) {
        Long userId = getLoggedUser(session);
        notificationsService.updateFirebaseToken(userId, token);
        return BaseController.OK_MESSAGE;
    }
}
