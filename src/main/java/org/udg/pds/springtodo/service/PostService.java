package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.PostRepository;
import org.udg.pds.springtodo.repository.RouteRepository;
import org.udg.pds.springtodo.repository.WorkoutRepository;

import java.util.Collection;
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

    //Aquest metode ha de retornar els posts de l'usuari i tambe els posts dels amics de l'usuari
    public List<Post> getPosts(Long userId) {
        List<Post> tots = (List<Post>) postRepository.findAll();

        //Aqui s'hauria de fer ara el control de que nomes es retornin aquells que son de l'usuari o d'algun amic seu
        return tots;
    }


}
