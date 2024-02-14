package org.kerala.kgovern.services;

import org.kerala.kgovern.dto.UserRegisterDto;

public interface UserService {
    boolean userExists(String username);

    boolean emailExists(String email);

    void registerUser(UserRegisterDto userRegisterDto);
}
