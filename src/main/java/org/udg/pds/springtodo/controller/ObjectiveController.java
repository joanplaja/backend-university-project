package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Equipment;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.EquipmentService;
import org.udg.pds.springtodo.service.ObjectiveService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@RequestMapping(path="/objectives")
@RestController
public class ObjectiveController extends BaseController{

    @Autowired
    ObjectiveService objectiveService;

    @GetMapping(path = "/{id}")
    @JsonView(Views.Private.class)
    public Objective getObjective(HttpSession session,
                                  @PathVariable("id") Long objectiveId) {
        Long userId = getLoggedUser(session);

        return objectiveService.getObjective(userId, objectiveId);
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public Collection<Objective> listAllObjectives(HttpSession session,
                                                   @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);

        return objectiveService.getObjectives(userId);
    }

    @PostMapping(consumes = "application/json")
    public IdObject addObjective(HttpSession session, @Valid @RequestBody ObjectiveController.R_Objective objective) {

        Long userId = getLoggedUser(session);

        return objectiveService.addObjective(objective.type, objective.goal, userId);
    }

    @DeleteMapping(path = "/{id}")
    public String deleteObjective(HttpSession session,
                                  @PathVariable("id") Long objectiveId) {
        getLoggedUser(session);
        objectiveService.deleteObjective(objectiveId);
        return BaseController.OK_MESSAGE;
    }

    static class R_Objective {

        @NotNull
        public String type;

        @NotNull
        public double goal;
    }
}
