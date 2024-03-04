package org.kerala.kgovern.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.entities.DepartmentComplaint;
import org.kerala.kgovern.entities.Employee;
import org.kerala.kgovern.entities.User;
import org.kerala.kgovern.repositories.DepartmentComplaintRepository;
import org.kerala.kgovern.repositories.EmployeeRepository;
import org.kerala.kgovern.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employee")
@Slf4j
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentComplaintRepository complaintRepository;
    @GetMapping("/")
    public String departmentRedirect(){
        User user = getCurrentUser();
        log.info(user.getUsername());
        log.info(user.getRole().name());
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        return "redirect:/employee/complaints/"+employee.getDepartment()+"?district="+employee.getDistrict();
    }
    @GetMapping("/complaints")
    public String redirectToComplaints(){
        User user = getCurrentUser();
        log.info(user.getUsername());
        log.info(user.getRole().name());
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        return "redirect:/employee/complaints/"+employee.getDepartment()+"?district="+employee.getDistrict();
    }
    @GetMapping("/complaints/{department}")
    public String getComplaints(@PathVariable String department, Model model, @RequestParam(name = "district") String district){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        if(department == null || !department.equals(employee.getDepartment())){
            return "redirect:/employee/complaints/"+employee.getDepartment()+"?district="+employee.getDistrict();
        }
        if(district == null || !district.equals(employee.getDistrict())){
            return "redirect:/employee/complaints/"+employee.getDepartment()+"?district="+employee.getDistrict();
        }
        model.addAttribute("district",district);
        model.addAttribute("department",department);
        log.info("Inside complaints method!!");
        List<DepartmentComplaint> complaintList = complaintRepository.findByDepartmentAndDistrict(department,district);
        model.addAttribute("complaints",complaintList);
        return "employee/complaints";
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }
}
