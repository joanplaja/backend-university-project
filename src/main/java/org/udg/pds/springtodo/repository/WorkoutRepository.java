package org.udg.pds.springtodo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Workout;

@Component
public interface WorkoutRepository extends CrudRepository<Workout, Long> {}

