package org.udg.pds.springtodo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Equipment;

@Component
public interface EquipmentRepository extends CrudRepository<Equipment, Long> {}
