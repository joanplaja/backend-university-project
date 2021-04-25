package org.udg.pds.springtodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.PointService;
import org.udg.pds.springtodo.service.RouteService;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@RequestMapping(path="/routes")
@RestController
public class RouteController extends BaseController {

    @Autowired
    RouteService routeService;

    @Autowired
    PointService pointService;

    //This function search near routes based on the initial points of the routes and the given position
    @PostMapping(path = "/near", consumes = "application/json")
    public List<Route> nearRoutes(HttpSession session, @Valid @RequestBody R_Position_Limit positionLimit) {

        Long userId = getLoggedUser(session);
        List<Route> routes = routeService.getNearRoutes(positionLimit.latitude,positionLimit.longitude,positionLimit.limit);

        return routes;
    }

    static class R_Position_Limit {
        @NotNull
        public Double latitude;

        @NotNull
        public Double longitude;

        @NotNull
        public Integer limit;



    }
}
