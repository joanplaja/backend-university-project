package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.PostRepository;
import org.udg.pds.springtodo.repository.RouteRepository;
import org.udg.pds.springtodo.repository.WorkoutRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    WorkoutService workoutService;

    public PostRepository crud(){ return postRepository; }

    @Transactional
    public IdObject addPost(Long userId, Long workoutId, String description, String imageUrl) {
        try {
            Workout workout = workoutService.getWorkout(userId, workoutId);
            Post post = new Post(description, imageUrl);
            workout.setPost(post);
            post.setWorkout(workout);
            postRepository.save(post);
            workoutRepository.save(workout);
            return new IdObject(post.getId());
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    /*
    public Route getRoute(Long workoutId, Long userId) {
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


    @Transactional
    public List<Route> getNearRoutes(Double latitude, Double longitude, Integer kmLimit){
        return routeRepository.findNearRoutes(latitude,longitude,kmLimit);
    }
    */


}
