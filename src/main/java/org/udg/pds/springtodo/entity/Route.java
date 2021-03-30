package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.jdbc.Work;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.serializer.JsonDateSerializer;
import org.udg.pds.springtodo.serializer.JsonTagSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


@Entity
// This tells JAXB that it has to ignore getters and setters and only use fields for JSON marshaling/unmarshaling
public class Route implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Route() {
    }

    public Route(Point initialPoint) {
        this.initialLatitude = initialPoint.latitude();
        this.initialLongitude = initialPoint.longitude();
        this.points = new ArrayList<>();
    }

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private double initialLatitude;

    private double initialLongitude;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "route",fetch = FetchType.EAGER)
    private Collection<Point> points;

    @OneToOne(mappedBy = "route")
    private Workout workout;

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public void addPoint(Point p){ points.add(p);}

    public Long getId() {
        return id;
    }

    @JsonView(Views.Complete.class)
    public Collection<Point> getPoints() {
        // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
        // that you have to query the object (calling size(), for example) to get the list initialized
        // More: http://www.javabeat.net/jpa-lazy-eager-loading/
        points.size();
        return points;
    }
}
