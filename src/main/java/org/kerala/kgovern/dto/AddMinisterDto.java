package org.kerala.kgovern.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMinisterDto {
    private String username;
    private String password;
    private String department;
    private String aadhaarId;
    private String district;
    private String phoneNumber;
}