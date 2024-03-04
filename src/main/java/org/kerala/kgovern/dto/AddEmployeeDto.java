package org.kerala.kgovern.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeeDto {
    private String username;
    private String password;
    private String aadhaarId;
    private String district;
    private String phoneNumber;
}
