package org.kerala.kgovern.controllers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.Role;
import org.kerala.kgovern.constants.Department;
import org.kerala.kgovern.constants.District;
import org.kerala.kgovern.dto.AddEmployeeDto;
import org.kerala.kgovern.dto.AddMinisterDto;
import org.kerala.kgovern.entities.Employee;
import org.kerala.kgovern.entities.User;
import org.kerala.kgovern.repositories.EmployeeRepository;
import org.kerala.kgovern.repositories.UserRepository;
import org.kerala.kgovern.services.EmployeeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/departments")
    public String department(Model model, HttpServletRequest request){
       model.addAttribute("requestURI",request.getRequestURI());
       return "admin/departments";
    }
    @GetMapping("/minister")
    public String ministers(Model model, HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/ministers";
    }
    @GetMapping("/minister/department-ministers")
    public String departmentMinister(Model model, HttpServletRequest request){
        List<Employee> ministers = employeeRepository.findEmployeeByIsHeadIsTrue();
        model.addAttribute("ministers",ministers);
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/departmentMinisters";
    }
    @GetMapping("/minister/department-minister/add-minister")
    public String addMinister(Model model, HttpServletRequest request){
        model.addAttribute("districts", District.districts);
        model.addAttribute("departments", Department.departments);
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/addMinister";
    }
    @PostMapping("/minister/add-minister")
    public String addDepartmentMinister(@ModelAttribute AddMinisterDto addMinisterDto,RedirectAttributes ra){
        if(userRepository.existsByUsername(addMinisterDto.getUsername())){
            ra.addFlashAttribute("errMessage","Username already exists");
            return "redirect:/admin/minister/department-minister/add-minister";
        }
        employeeService.addMinister(addMinisterDto);
        ra.addFlashAttribute("message","minister added successfully");
        return "redirect:/admin/minister/department-ministers";
    }

    @GetMapping("/departments/{department}")
    public String getDepartment(@PathVariable String department,Model model,HttpServletRequest request,@RequestParam(name = "district") String district){
        AtomicBoolean ministerPresent = new AtomicBoolean(false);
        AtomicReference<Employee> minister = new AtomicReference<>(new Employee());
        employeeRepository.findByDepartment(department).forEach(employee -> {
            if(employee.isHead()){
                ministerPresent.set(true);
                minister.set(employee);
            }
        });
        model.addAttribute("ministerPresent",ministerPresent.get());
        model.addAttribute("minister",minister.get());
        if(district == null){
            return "redirect:/admin/departments/"+department+"?district=Trivandrum";
        }
        model.addAttribute("district",district);
        List<Employee> employees = employeeRepository.findByDepartmentAndDistrictAndIsHeadFalse(department,district);
        model.addAttribute("employees",employees);
        model.addAttribute("currentDistrict",district);
        model.addAttribute("districts", District.districts);
        model.addAttribute("department",department);
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/department";
    }
    @PostMapping("/departments/{department}/delete-employee/{employeeId}/{district}")
    public String deleteEmployee(RedirectAttributes ra, @PathVariable String department, Model model, HttpServletRequest request, @PathVariable Long employeeId, @PathVariable String district){
        try {
            employeeRepository.deleteById(employeeId);
        }
        catch (Exception e){
            log.error("ERROR !!!! {}",e.getMessage());
            ra.addFlashAttribute("errMessage","Something went wrong while deleting employee!!");
        }
        ra.addFlashAttribute("message","Employee deleted successfully!!");
        return "redirect:/admin/departments/"+department+"?district="+district;
    }
    @PostMapping("/departments/{department}/upgrade-employee/{employeeId}/{district}")
    public String upgradeEmployee(RedirectAttributes ra, @PathVariable String department, Model model, HttpServletRequest request, @PathVariable Long employeeId, @PathVariable String district){
        try {
            List<Employee> employees = employeeRepository.findByDepartment(department);
            employees.stream()
                    .filter(Employee::isHead)
                    .forEach(employee -> {
                        employee.setHead(false);
                        employee.getEmployee().setRole(Role.ROLE_EMPLOYEE);
                    });
            employeeRepository.saveAll(employees);
            Employee employee = employeeRepository.findById(employeeId).get();
            User user  = employee.getEmployee();
            user.setRole(Role.ROLE_HEAD);
            employee.setHead(true);
            employeeRepository.save(employee);
            userRepository.save(user);
            ra.addFlashAttribute("message","Employee upgraded successfully!!");
            return "redirect:/admin/departments/"+department+"?district="+district;
        }
        catch (Exception e){
            log.error("ERROR !!!! {}",e.getMessage());
            ra.addFlashAttribute("errMessage","Something went wrong while upgrading employee!!");
            return "redirect:/admin/departments/"+department+"?district="+district;
        }
    }
    @GetMapping("/departments/add-employee/{department}")
    public String addEmployee(@PathVariable String department,Model model,HttpServletRequest request){
        model.addAttribute("department",department);
        model.addAttribute("districts", District.districts);
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
}
