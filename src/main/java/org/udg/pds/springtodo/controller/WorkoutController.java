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
    @JsonView(Views.Private.class)
    public Workout getWorkout(HttpSession session,
                        @PathVariable("id") Long id) {
        Long userId = getLoggedUser(session);

        return workoutService.getWorkout(userId, id);
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public Collection<Workout> listAllWorkouts(HttpSession session,
                                            @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);

        return workoutService.getWorkouts(userId);
    }

    @PostMapping(consumes = "application/json")
    public IdObject addWorkout(HttpSession session, @Valid @RequestBody R_Workout workout) {
        //Ara el workout conte una ruta a dins, i quan es crea un workout, s'ha d'especificar la latitude i longitude (documentat a la wiki)
        Long userId = getLoggedUser(session);
        IdObject workoutId = workoutService.addWorkoutWithRoute(workout.type, userId, workout.route.initialLatitude, workout.route.initialLongitude);
        return workoutId;
    }

    @PostMapping(path = "/{id}/points", consumes = "application/json")
    public String addPointsToRoute(HttpSession session, @Valid @RequestBody Double[][] points, @PathVariable("id") Long workoutId) {

        Long userId = getLoggedUser(session);
        ArrayList<Point> newPoints = new ArrayList<>();
        for(Double[] coordinate : points) {
            Point p = new Point(coordinate[0], coordinate[1]);
            newPoints.add(p);
        }
        boolean res = pointService.addPoints(userId, workoutId, newPoints);
        String resultStatment;
        if(res) {
            resultStatment = BaseController.OK_MESSAGE;
        } else {
            resultStatment = BaseController.BAD_MESSAGE;
        }
        return resultStatment;
    }

    @DeleteMapping(path="/{id}")
    public String deleteWorkout(HttpSession session,
                             @PathVariable("id") Long workoutId) {
        getLoggedUser(session);
        workoutService.deleteWorkout(workoutId);
        return BaseController.OK_MESSAGE;
    }

    static class R_Workout {
        @NotNull
        public String type;

        @NotNull
         public R_Route route;

    }

    static class R_Route {
        @NotNull
        public Double initialLatitude;

        @NotNull
        public Double initialLongitude;

    }
}

