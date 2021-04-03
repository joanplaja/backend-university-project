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

@RequestMapping(path="/routes")
@RestController
public class RouteController extends BaseController {

    @Autowired
    WorkoutService workoutService;

    @Autowired
    RouteService routeService;

    @Autowired
    PointService pointService;


    @PostMapping(path = "/{id}/points", consumes = "application/json")
    public IdObject addPointsToRoute(HttpSession session, @Valid @RequestBody Double[][] points, @PathVariable("id") Long workoutId) {

        Long userId = getLoggedUser(session);
        ArrayList<Point> newPoints = new ArrayList<>();
        for(Double[] coordinate : points) {
            Point p = new Point(coordinate[0], coordinate[1]);
            newPoints.add(p);
        }
        return pointService.addPoints(userId, workoutId, newPoints);
    }

}
