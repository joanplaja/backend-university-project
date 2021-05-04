package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name = "users")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "username"}))
public class User implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    public User() {
    }

    public User(String username, String email, String password, Integer phoneNumber, String firstName, String lastName, Integer age) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.tasks = new ArrayList<>();
        this.workouts = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.facebookToken = null;
        this.facebookId = null;
    }
    public User(String username, String email,String password, Integer phoneNumber, String firstName, String lastName, Integer age,String facebookToken, Long facebookId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.tasks = new ArrayList<>();
        this.workouts = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.facebookToken = facebookToken;
        this.facebookId = facebookId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Integer phoneNumber;

  private String imageUrl;

  @NotNull
  private String description = "Hey! I'm using Avarst";

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private Integer age;

    private  Long facebookId;

    private String facebookToken;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Task> tasks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Workout> workouts;

    @ManyToMany(/*fetch = FetchType.EAGER*/)
    @JoinTable(name = "relation",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Collection<User> following;

    @ManyToMany(mappedBy = "following")
    private Collection<User> followers;

    @JsonView(Views.Public.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    @JsonView(Views.Private.class)
    public String getEmail() {
        return email;
    }

    @JsonView(Views.Public.class)
    public String getUsername() {
        return username;
    }

  @JsonView(Views.Public.class)
  public void setUsername(String newUsername) { this.username = newUsername;  }

  @JsonView(Views.Public.class)
  public Integer getPhoneNumber() {
        return phoneNumber;
    }

  @JsonView(Views.Public.class)
  public void setPhoneNumber(Integer newPhoneNumber) { this.phoneNumber = newPhoneNumber;  }

  @JsonView(Views.Public.class)
  public String getDescription() {
        return description;
    }

  @JsonView(Views.Public.class)
  public void setDescription(String newDescription) { this.description = newDescription;  }

  @JsonIgnore
  public String getPassword() {
    return password;
  }
    @JsonView(Views.Public.class)
    public String getFirstName() {
        return firstName;
    }

    @JsonView(Views.Public.class)
    public String getLastName() {
        return lastName;
    }

    @JsonView(Views.Public.class)
    public Integer getAge() {
        return age;
    }



   /* @JsonIgnore
    public String getPassword() {
        return password;
    }*/

  @JsonView(Views.Public.class)
  public String getImageUrl() {
        return imageUrl;
    }

  @JsonView(Views.Public.class)
  public void setImageUrl(String newImageUrl) { this.imageUrl = newImageUrl; }


    @JsonView(Views.Complete.class)
  public Collection<Task> getTasks() {
    // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
    // that you have to query the object (calling size(), for example) to get the list initialized
    // More: http://www.javabeat.net/jpa-lazy-eager-loading/
    tasks.size();
    return tasks;
  }

    @JsonView(Views.Complete.class)
    public Collection<Workout> getWorkouts() {
        // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
        // that you have to query the object (calling size(), for example) to get the list initialized
        // More: http://www.javabeat.net/jpa-lazy-eager-loading/
        workouts.size();
        return workouts;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addWorkout(Workout workout) {
        workouts.add(workout);
    }

    public void addFollower(User u) {
        followers.add(u);
    }

    public void removeFollower(User u) {
        followers.remove(u);
    }

    public void Follow(User u) {
        following.add(u);
    }

    public Boolean AlreadyFollowing(User u){
        if (following.contains(u))
            return true;
        else return false;
    }

    public void Unfollow(User u) {
        following.remove(u);
    }

    @JsonView(Views.Complete.class)
    public Collection<User> getFollowers() {
        followers.size();
        return followers;
    }

    @JsonView(Views.Complete.class)
    public Collection<User> getFollowing() {
        following.size();
        return following;
    }
}
