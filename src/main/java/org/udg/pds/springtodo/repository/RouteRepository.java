package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Route;

import java.util.List;

@Component
public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query(value = "SELECT * FROM route r WHERE ( 6371 * acos(cos(RADIANS(:latitude)) * cos(radians(r.initialLatitude)) * cos(radians(r.initialLongitude) - RADIANS( :latitude )) + sin(radians( :longitude )) * sin(radians(r.initialLatitude )))) < :kmLimit ",
    nativeQuery = true)
    List<Route> findNearRoutes(@Param("latitude") Double latitude,@Param("longitude") Double longitude,@Param("kmLimit") Integer kmLimit);
}

