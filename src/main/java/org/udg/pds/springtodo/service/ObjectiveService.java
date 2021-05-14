package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Objective;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.ObjectiveRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class ObjectiveService {

    @Autowired
    ObjectiveRepository objectiveRepository;

    @Autowired
    UserService userService;

    public ObjectiveRepository crud() {
        return objectiveRepository;
    }

    public Collection<Objective> getObjectives(Long userId) {
        return userService.getUser(userId).getObjectives();
    }

    @Transactional
    public void deleteObjective(Long objectiveId) {
        objectiveRepository.deleteById(objectiveId);
    }

    public Objective getObjective(Long userId, Long id) {
        Optional<Objective> o = objectiveRepository.findById(id);
        if (!o.isPresent()) throw new ServiceException("Objective does not exist");
        if (o.get().getUser().getId() != userId)
            throw new ServiceException("User does not have this objective");
        return o.get();
    }

    @Transactional
    public IdObject addObjective(int type, double goal, Long userId) {
        try {
            User user = userService.getUser(userId);

            Objective objective = new Objective(type, goal);

            objective.setUser(user);

            user.addObjective(objective);

            objectiveRepository.save(objective);
            return new IdObject(objective.getId());
        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }

}
