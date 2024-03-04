package org.kerala.kgovern.repositories;

import org.kerala.kgovern.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    User findByUsername(String username);

//    List<User> findByIsDeletedIsFalse();
}
