package org.kerala.kgovern.services;

import org.kerala.kgovern.dto.UserRegisterDto;
import org.kerala.kgovern.entities.User;

import java.util.List;

public interface UserService {
    boolean userExists(String username);

    boolean emailExists(String email);

    void registerUser(UserRegisterDto userRegisterDto);

    List<User> getAllUsers();
}
