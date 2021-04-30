package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;


@Entity
// This tells JAXB that it has to ignore getters and setters and only use fields for JSON marshaling/unmarshaling
public class Post implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Post() {
    }

    public Post(String descritption, String imageUrl) {
        this.descritption = descritption;
        this.imageUrl = imageUrl;
    }

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String descritption;

    private String imageUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_workout", referencedColumnName = "id")
    private Workout workout;

    @JsonView(Views.Private.class)
    public Workout getWorkout() { return workout; }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    @JsonView(Views.Private.class)
    public Long getId() {
        return id;
    }

    @JsonView(Views.Private.class)
    public String getDescription() {
        return descritption;
    }

    @JsonView(Views.Private.class)
    public String getImageUrl() {
        return imageUrl;
    }
}
