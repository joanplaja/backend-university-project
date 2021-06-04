package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.PostRepository;
import org.udg.pds.springtodo.repository.RouteRepository;
import org.udg.pds.springtodo.repository.UserRepository;
import org.udg.pds.springtodo.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserService userService;

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    WorkoutService workoutService;

    @Autowired
    private UserRepository userRepository;

    public PostRepository crud(){ return postRepository; }

    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Transactional
    public IdObject addPost(Long userId, Long workoutId, String description, String imageUrl) {
        try {
            Workout workout = workoutService.getWorkout(userId, workoutId);
            Post post = new Post(description, imageUrl);
            workout.setPost(post);
            post.setWorkout(workout);
            postRepository.save(post);
            workoutRepository.save(workout);
            return new IdObject(post.getId());
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public List<Post> getPosts(Long userId) {
        List<Post> tots = (List<Post>) postRepository.findAll();
        User user = userService.getUser(userId);
        List<User> following = (List<User>) user.getFollowing();

        List<Post> feed = new ArrayList<>();
        for (Post p : tots) {
            Long postOwner = p.getWorkout().getUser().getId();
            if(postOwner == userId) {
                feed.add(p);
                continue;
            }
            for (User u : following) {
                Long friendId = u.getId();
                if (postOwner == friendId) {
                    feed.add(p);
                    break;
                }
            }
        }
        return feed;
    }

    public Post getPost(Long id) {
        Optional<Post> uo = postRepository.findById(id);
        if (uo.isPresent())
            return uo.get();
        else
            throw new ServiceException(String.format("Post with id = % dos not exists", id));
    }

    @Transactional
    public void likePost(Long userId, Long postId){
        Post likedPost = this.getPost(postId);
        User user = userService.getUser(userId);

        user.addLike(likedPost);
        likedPost.addLike(user);

        userRepository.save(user);
        postRepository.save(likedPost);
    }

    public Collection<User> getLikes(Long postId){
        return this.getPost(postId).getLikes();
    }



}
