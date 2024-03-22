package org.kerala.kgovern.services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.Role;
import org.kerala.kgovern.dto.AddMinisterDto;
import org.kerala.kgovern.entities.Employee;
import org.kerala.kgovern.entities.User;
import org.kerala.kgovern.repositories.EmployeeRepository;
import org.kerala.kgovern.repositories.UserRepository;
import org.kerala.kgovern.services.EmployeeService;
import org.kerala.kgovern.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void addMinister(AddMinisterDto addMinisterDto) {
        Employee employee = new Employee();
        User user = new User();
        user.setUsername(addMinisterDto.getUsername());
        user.setRole(Role.ROLE_HEAD);
        user.setPassword(encoder.encode(addMinisterDto.getPassword()));
        user.setEmail(addMinisterDto.getDepartment());
        user.setPhoneNumber(addMinisterDto.getPhoneNumber());
        user.setAadhaarNumber(addMinisterDto.getAadhaarId());
        user = userRepository.save(user);
        employeeRepository.findByDepartment(addMinisterDto.getDepartment())
                .forEach(emp -> {
                    if(emp.isHead()){
                        emp.setHead(false);
                        User usertemp = employee.getEmployee();
                        usertemp.setRole(Role.ROLE_EMPLOYEE);
                        userRepository.save(usertemp);
                        employeeRepository.save(emp);
                    }
                });
        employee.setHead(true);
        employee.setDepartment(addMinisterDto.getDepartment());
        employee.setDistrict(addMinisterDto.getDistrict());
        employee.setEmployee(user);
        employeeRepository.save(employee);
    }
}
