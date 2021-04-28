package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Tag;
import org.udg.pds.springtodo.entity.Task;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserRepository crud() {
        return userRepository;
    }

    public User matchPassword(String username, String password) {

        List<User> uc = userRepository.findByUsername(username);

        if(uc.size() == 0){
            uc = userRepository.findByEmail(username);
        }

        if (uc.size() == 0) throw new ServiceException("User does not exists");

        User u = uc.get(0);

        if (u.getPassword().equals(password))
            return u;
        else
            throw new ServiceException("Password does not match");
    }

    public User register(String username, String email, String password, Integer phoneNumber, String firstName, String lastName, Integer age) {

        List<User> uEmail = userRepository.findByEmail(email);
        if (uEmail.size() > 0)
            throw new ServiceException("Email already exist");


        List<User> uUsername = userRepository.findByUsername(username);
        if (uUsername.size() > 0)
            throw new ServiceException("Username already exists");

        User nu = new User(username, email, password, phoneNumber, firstName, lastName, age);
        userRepository.save(nu);
        return nu;
    }

    public User getUser(Long id) {
        Optional<User> uo = userRepository.findById(id);
        if (uo.isPresent())
            return uo.get();
        else
            throw new ServiceException(String.format("User with id = % dos not exists", id));
    }

    public User getUserProfile(long id) {
        User u = this.getUser(id);
        for (Task t : u.getTasks())
            t.getTags();
        return u;
    }

    public void updateUserMe(Long userId, String newUsername, String newDescription, Integer newPhoneNumber, String newImageUrl) {
        try {
            User updatingUser = this.getUser(userId);

            updatingUser.setUsername(newUsername);
            updatingUser.setDescription(newDescription);
            updatingUser.setPhoneNumber(newPhoneNumber);
            updatingUser.setImageUrl(newImageUrl);

            userRepository.save(updatingUser);
        }catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }

    public User getUserEquipment(Long loggedUserId) {
        //HEM DE BUSCAR ELS EQUIPMENT, SIMILAR A BUSCAR ELS TAG DE LES TASKS
        User u = this.getUser(loggedUserId);
        for (Task t : u.getTasks())
            t.getTags();
        return u;
    }

    @Transactional
    public void addFollowing(Long userId, Long followingId) {
        try {
            User user = this.getUser(userId);

            User following = this.getUser(followingId);

            if (!user.AlreadyFollowing(following)){
                user.Follow(following);

                following.addFollower(user);

                userRepository.save(user);
                userRepository.save(following);
            }
        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }

    @Transactional
    public void removeFollow(Long userId, Long unfollowId) {
        try {
            User user = this.getUser(userId);

            User unfollow = this.getUser(unfollowId);

            user.Unfollow(unfollow);

            unfollow.removeFollower(user);

            userRepository.save(user);
            userRepository.save(unfollow);
        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
            // We catch the normal exception and then transform it in a ServiceException
            throw new ServiceException(ex.getMessage());
        }
    }

    public Collection<User> getFollowing(Long id) {
        return this.getUser(id).getFollowing();
    }

    public Collection<User> getFollowers(Long id) {
        return this.getUser(id).getFollowers();
    }

    public List <User> findUser(Specification<User> spec){
        return userRepository.findAll(spec);
    }

    public Long getUserId(String username){
        List<User> uc = userRepository.findByUsername(username);
        User u = uc.get(0);
        return u.getId();
    }

}
