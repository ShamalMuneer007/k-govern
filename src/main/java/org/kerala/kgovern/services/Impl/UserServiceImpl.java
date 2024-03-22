package org.kerala.kgovern.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.Role;
import org.kerala.kgovern.dto.UserRegisterDto;
import org.kerala.kgovern.entities.User;
import org.kerala.kgovern.exceptions.InternalServerError;
import org.kerala.kgovern.repositories.UserRepository;
import org.kerala.kgovern.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void registerUser(UserRegisterDto userRegisterDto) {
        try {
            log.warn("user registering....");

            User user = User.builder()
                    .username(userRegisterDto.getUsername())
                    .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                    .aadhaarNumber(userRegisterDto.getAadhaarId())
                    .phoneNumber(userRegisterDto.getAadhaarId())
                    .email(userRegisterDto.getEmail())
                    .role(userRepository.count() < 1 ? Role.ROLE_ADMIN : Role.ROLE_USER)
                    .build();
            userRepository.save(user);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new InternalServerError("Something went wrong while registering user");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
