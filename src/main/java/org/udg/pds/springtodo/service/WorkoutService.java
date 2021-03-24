package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Workout;
import org.udg.pds.springtodo.repository.WorkoutRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class WorkoutService {

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    UserService userService;

    public WorkoutRepository crud() {
        return workoutRepository;
    }

    public Collection<Workout> getWorkouts(Long id) {
        return userService.getUser(id).getWorkouts();
    }

    public Workout getWorkout(Long userId, Long id) {
        Optional<Workout> w = workoutRepository.findById(id);
        if (!w.isPresent()) throw new ServiceException("Workout does not exist");
        if (w.get().getUser().getId() != userId)
            throw new ServiceException("User does not have this workout");
        return w.get();
    }

    @Transactional
    public IdObject addWorkout(String type, Long userId, Date created) {
        try {
            User user = userService.getUser(userId);

            Workout workout = new Workout(type, created);

            workout.setUser(user);

            user.addWorkout(workout);

            workoutRepository.save(workout);
            return new IdObject(workout.getId());
        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }

}