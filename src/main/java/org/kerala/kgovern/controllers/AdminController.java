package org.kerala.kgovern.controllers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.Role;
import org.kerala.kgovern.dto.AddEmployeeDto;
import org.kerala.kgovern.entities.Employee;
import org.kerala.kgovern.entities.User;
import org.kerala.kgovern.repositories.EmployeeRepository;
import org.kerala.kgovern.repositories.UserRepository;
import org.kerala.kgovern.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/departments")
    public String department(Model model, HttpServletRequest request){
       model.addAttribute("requestURI",request.getRequestURI());
       return "admin/departments";
    }
    @GetMapping("/departments/{department}")
    public String getDepartment(@PathVariable String department,Model model,HttpServletRequest request,@RequestParam(name = "district") String district){
        if(district == null){
            return "redirect:/admin/departments/"+department+"?district=Trivandrum";
        }
        List<Employee> employees = employeeRepository.findByDepartmentAndDistrict(department,district);
        model.addAttribute("employees",employees);
        model.addAttribute("currentDistrict",district);
        model.addAttribute("districts", List.of("Trivandrum", "Kollam", "Pathanamthitta", "Alappuzha", "Kottayam", "Idukki", "Ernakulam", "Thrissur", "Palakkad", "Malappuram", "Kozhikode", "Wayanad", "Kannur", "Kasaragod"));
        model.addAttribute("department",department);
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/department";
    }
    @PostMapping("/departments/{department}/delete-employee/{employeeId}")
    public String deleteEmployee(RedirectAttributes ra,@PathVariable String department, Model model, HttpServletRequest request, @PathVariable Long employeeId){
        try {
            employeeRepository.deleteById(employeeId);
        }
        catch (Exception e){
            log.error("ERROR !!!! {}",e.getMessage());
            ra.addFlashAttribute("errMessage","Something went wrong while deleting employee!!");
        }
        ra.addFlashAttribute("message","User detailed successfully!!");
        return "redirect:/admin/departments/"+department+"?district=Trivandrum";
    }
    @GetMapping("/departments/add-employee/{department}")
    public String addEmployee(@PathVariable String department,Model model,HttpServletRequest request){
        model.addAttribute("department",department);
        model.addAttribute("districts", List.of("Trivandrum", "Kollam", "Pathanamthitta", "Alappuzha", "Kottayam", "Idukki", "Ernakulam", "Thrissur", "Palakkad", "Malappuram", "Kozhikode", "Wayanad", "Kannur", "Kasaragod"));
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/addEmployee";
    }
    @PostMapping("/add-employee/{department}")
    @Transactional
    public String addEmployeeSubmit(@PathVariable String department,RedirectAttributes ra, @ModelAttribute AddEmployeeDto employeeDto){
        User user = new User();
        if(userRepository.existsByUsername(employeeDto.getUsername())){
            ra.addFlashAttribute("errMessage","User with same username already exists");
            return "redirect:/admin/departments/add-employee/"+department;
        }
        try {
            user.setUsername(employeeDto.getUsername());
            user.setAadhaarNumber(employeeDto.getAadhaarId());
            user.setRole(Role.ROLE_EMPLOYEE);
            user.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
            user.setPhoneNumber(employeeDto.getPhoneNumber());
            user = userRepository.save(user);
            Employee employee = new Employee();
            employee.setHead(false);
            employee.setDepartment(department);
            employee.setEmployee(user);
            employee.setDistrict(employeeDto.getDistrict());
            employeeRepository.save(employee);
        }
        catch (Exception e){
            log.error(e.getMessage());
            ra.addFlashAttribute("errMessage","Something went wrong!!!!");
            return "redirect:/admin/departments/add-employee/"+department;
        }
        ra.addFlashAttribute("message","Employee added successfully");
        return "redirect:/admin/departments";

    }
//    @GetMapping("/users")
//    public String getUsers(Model model, HttpServletRequest request){
//        List<User> users = userService.getAllUsers();
//        model.addAttribute("users",users);
//        return "admin/users";
//    }
}
