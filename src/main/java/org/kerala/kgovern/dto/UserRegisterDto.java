package org.kerala.kgovern.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDto {
    private String username;
    private String password;
    private String email;
    private Integer aadhaarId;
    private Integer phoneNumber;
}
