package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.RouteRepository;
import org.udg.pds.springtodo.repository.PointRepository;
import org.udg.pds.springtodo.repository.WorkoutRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class RouteService {

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    PointService pointService;

    @Autowired
    WorkoutService workoutService;

    public RouteRepository crud(){ return routeRepository; }

    public Route getRoute(Long workoutId, Long userId){
        Optional<Workout> w = workoutRepository.findById(workoutId);
        if (!w.isPresent()) throw new ServiceException("Workout does not exist");
        if (w.get().getUser().getId() != userId)
            throw new ServiceException("User does not have this workout");
        Workout workout = w.get();
        Route route = workout.getRoute();
        if(route == null) {
            throw new ServiceException("This workout does not have a route");
        }
        return route;
    }

    public IdObject addRoute(Long userId, Long workoutId, double initialLatitude, double initialLongitude ) {
        try {
            Workout workout = workoutService.getWorkout(userId, workoutId);
            Route route = new Route(new Point(initialLatitude, initialLongitude));
            workout.setRoute(route);
            route.setWorkout(workout);
            routeRepository.save(route);
            workoutRepository.save(workout);
            return new IdObject(route.getId());
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

}
