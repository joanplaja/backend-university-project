package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Workout;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.service.WorkoutService;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@RequestMapping(path="/workouts")
@RestController
public class WorkoutController extends BaseController {

    @Autowired
    WorkoutService workoutService;

    @GetMapping(path="/{id}")
    public Workout getWorkout(HttpSession session,
                        @PathVariable("id") Long id) {
        Long userId = getLoggedUser(session);

        return workoutService.getWorkout(userId, id);
    }

    @GetMapping
    @JsonView(Views.Private.class)
    public Collection<Workout> listAllWorkouts(HttpSession session,
                                            @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);

        return workoutService.getWorkouts(userId);
    }

    @PostMapping(consumes = "application/json")
    public IdObject addWorkout(HttpSession session, @Valid @RequestBody R_Workout workout) {

        Long userId = getLoggedUser(session);

        return workoutService.addWorkout(workout.type, userId, workout.dateCreated);
    }

    @DeleteMapping(path="/{id}")
    public String deleteWorkout(HttpSession session,
                             @PathVariable("id") Long workoutId) {
        getLoggedUser(session);
        workoutService.crud().deleteById(workoutId);
        return BaseController.OK_MESSAGE;
    }

    static class R_Workout {
        @NotNull
        public String type;

        @NotNull
        @JsonDeserialize(using=JsonDateDeserializer.class)
        public Date dateCreated;
    }

}
