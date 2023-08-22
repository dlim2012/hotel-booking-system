package com.dlim2012.user.repository;

import com.dlim2012.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByEmail(String email);
    Boolean existsByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByIdAndEmail(Integer id, String email);

    @Query(
            value = "SELECT u FROM User u WHERE u.id in :ids"
    )
    List<User> findByIds(
            @Param("ids") List<Integer> ids);
}
