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
import org.udg.pds.springtodo.service.EquipmentService;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@RequestMapping(path="/equipment")
@RestController
public class EquipmentController extends BaseController {

    @Autowired
    EquipmentService equipmentService;

    @GetMapping(path = "/{id}")
    @JsonView(Views.Private.class)
    public Equipment getEquipment(HttpSession session,
                                  @PathVariable("id") Long equipmentId) {
        Long userId = getLoggedUser(session);

        return equipmentService.getEquipment(userId, equipmentId);
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public Collection<Equipment> listAllEquipments(HttpSession session,
                                                   @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);

        return equipmentService.getEquipments(userId);
    }

    @PostMapping(consumes = "application/json")
    public IdObject addEquipment(HttpSession session, @Valid @RequestBody R_Equipment equipment) {

        Long userId = getLoggedUser(session);

        return equipmentService.addEquipment(equipment.name, equipment.description, equipment.imageUrl, equipment.shopUrl, userId);
    }

    @DeleteMapping(path = "/{id}")
    public String deleteEquipment(HttpSession session,
                             @PathVariable("id") Long equipmentId) {
        getLoggedUser(session);
        equipmentService.deleteEquipment(equipmentId);
        return BaseController.OK_MESSAGE;
    }

    static class R_Equipment {

        @NotNull
        public String name;

        @NotNull
        public String description;

        public String imageUrl;

        @NotNull
        public String shopUrl;

    }
}
