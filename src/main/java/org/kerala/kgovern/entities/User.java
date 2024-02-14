package org.kerala.kgovern.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.kerala.kgovern.Enums.Role;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private Long userId;
    private String username;
    private String password;
    private String email;
    private Integer aadhaarNumber;
    private Integer phoneNumber;
    @Enumerated(EnumType.STRING)
    private Role role;
}
