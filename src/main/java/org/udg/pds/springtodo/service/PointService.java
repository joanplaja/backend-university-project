package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.RouteRepository;
import org.udg.pds.springtodo.repository.PointRepository;
import org.udg.pds.springtodo.repository.WorkoutRepository;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class PointService {

    @Autowired
    PointRepository pointRepository;

    @Autowired
    RouteService routeService;

    public PointRepository crud(){ return  pointRepository; }

    public boolean addPoints(Long idRoute, Collection<Point> points){
        try {
        Route route = routeService.getRoute(idRoute);

        for (Point p : points) {
            p.setRoute(route);
            route.addPoint(p);
            pointRepository.save(p);
        }
        return true;
        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public IdObject addPoint(Long idRoute, double latitude, double longitude){
        try {
            Route route = routeService.getRoute(idRoute);
            Point p = new Point(latitude,longitude);
            p.setRoute(route);
            route.addPoint(p);
            pointRepository.save(p);
            return new IdObject(p.getId());
        }
        catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }



}
