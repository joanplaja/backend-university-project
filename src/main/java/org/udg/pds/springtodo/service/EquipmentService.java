package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Equipment;
import org.udg.pds.springtodo.repository.EquipmentRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class EquipmentService {

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    UserService userService;

    public EquipmentRepository crud() {
        return equipmentRepository;
    }

    public Collection<Equipment> getEquipments(Long userId) {
        return userService.getUser(userId).getEquipments();
    }

    @Transactional
    public void deleteEquipment(Long equipmentId) {
        equipmentRepository.deleteById(equipmentId);
    }

    public Equipment getEquipment(Long userId, Long id) {
        Optional<Equipment> e = equipmentRepository.findById(id);
        if (!e.isPresent()) throw new ServiceException("Equipment does not exist");
        if (e.get().getUser().getId() != userId)
            throw new ServiceException("User does not have this equipment");
        return e.get();
    }

    @Transactional
    public IdObject addEquipment(String name, String description, String imageUrl, String shopUrl, Long userId) {
        try {
            User user = userService.getUser(userId);

            Equipment equipment = new Equipment(name, description, imageUrl, shopUrl);

            equipment.setUser(user);

            user.addEquipment(equipment);

            equipmentRepository.save(equipment);
            return new IdObject(equipment.getId());
        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }

}
