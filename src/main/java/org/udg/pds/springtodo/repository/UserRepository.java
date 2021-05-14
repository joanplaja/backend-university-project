package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.User;

import java.util.List;

@Component
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("SELECT u FROM users u WHERE u.username=:username")
    List<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM users u WHERE u.email=:email")
    List<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM users u WHERE u.facebookId=:facebookId")
    User findByFacebookId(@Param("facebookId") Long facebookId);

    @Query(value = "SELECT * FROM users u WHERE u.facebookId IN ( :facebookIds ) AND u.id NOT IN ( SELECT r.following_id FROM relation r LEFT JOIN users us ON r.user_id = us.id WHERE us.id = :id ) AND u.id != :id",nativeQuery = true)
    List<User> findFacebookFriends(@Param("id") Long id,@Param("facebookIds") List<String> facebookIds);

    @Query(value = "SELECT * FROM users u WHERE u.phoneNumber IN ( :phoneNumbers ) AND u.id NOT IN ( SELECT r.following_id FROM relation r LEFT JOIN users us ON r.user_id = us.id WHERE us.id = :id ) AND u.id != :id",nativeQuery = true)
    List<User> findPhoneFriends(@Param("id") Long id,@Param("phoneNumbers") List<String> phoneNumbers);

}
