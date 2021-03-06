package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class Point implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Point() {
    }

    public Point(Double latitude, Double longitude, Double timeDiff, Double distanceDiff, Double velocity) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeDiff = timeDiff;
        this.distanceDiff = distanceDiff;
        this.velocity = velocity;
    }

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;

    private Double longitude;

    private Double timeDiff;

    private Double distanceDiff;

    private Double velocity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_route")
    private Route route;

    @Column(name = "fk_route", insertable = false, updatable = false)
    private Long routeId;

    public void setRoute(Route route) {
        this.route = route;
    }

    @JsonView(Views.Private.class)
    public Long getId() {
        return id;
    }

    @JsonView(Views.Private.class)
    public Double getLatitude() { return latitude; }

    @JsonView(Views.Private.class)
    public Double getLongitude() {
        return longitude;
    }

    @JsonView(Views.Private.class)
    public Double getTimeDiff() {
        return timeDiff;
    }

    @JsonView(Views.Private.class)
    public Double getDistanceDiff() {
        return distanceDiff;
    }

    @JsonView(Views.Private.class)
    public Double getVelocity() {
        return velocity;
    }
}
