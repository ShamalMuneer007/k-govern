package org.kerala.kgovern.entities;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String aadhaarNumber;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Role role;
}
