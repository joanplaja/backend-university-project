package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Objective implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Objective(){

    }

    public Objective(int type, double goal){
        this.type = type;
        this.goal = goal;
    }

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int type;

    private double goal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_user")
    private User user;

    @Column(name = "fk_user", insertable = false, updatable = false)
    private Long userId;

    @JsonView(Views.Public.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(Views.Private.class)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonView(Views.Public.class)
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JsonView(Views.Public.class)
    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    @JsonView(Views.Complete.class)
    public long getUserId() {
        return userId;
    }

}
