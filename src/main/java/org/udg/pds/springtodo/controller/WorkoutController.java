package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.service.PointService;
import org.udg.pds.springtodo.service.RouteService;
import org.udg.pds.springtodo.service.WorkoutService;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@RequestMapping(path="/workouts")
@RestController
public class WorkoutController extends BaseController {

    @Autowired
    WorkoutService workoutService;

    @Autowired
    RouteService routeService;

    @Autowired
    PointService pointService;

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

    @GetMapping(path = "/{id}/routes")
    @JsonView(Views.Complete.class)
    public Route getWorkoutRoute(HttpSession session, @PathVariable("id") Long workoutId) {
        Long userId = getLoggedUser(session);
        Route r = routeService.getRoute(workoutId, userId);
        System.out.println(r.getId());
        return r;
    }

    @PostMapping(path = "/{id}/routes", consumes = "application/json")
    public IdObject addRoute(HttpSession session, @Valid @RequestBody R_Route route, @PathVariable("id") Long workoutId) {

        Long userId = getLoggedUser(session);

        return routeService.addRoute(userId, workoutId, route.initialLatitude, route.initialLongitude);
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

    static class R_Route {
        @NotNull
        public Double initialLatitude;

        @NotNull
        public Double initialLongitude;

    }

    static class R_Points {
        @NotNull
        public Long routeId;

        @NotNull
        public ArrayList<Map<Double, Double>> points;

    }

}
