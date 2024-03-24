package org.kerala.kgovern.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.ComplaintStatus;
import org.kerala.kgovern.Enums.Role;
import org.kerala.kgovern.constants.District;
import org.kerala.kgovern.dto.AddEmployeeDto;
import org.kerala.kgovern.dto.ChangeComplaintStatusDto;
import org.kerala.kgovern.dto.NewsDto;
import org.kerala.kgovern.entities.*;
import org.kerala.kgovern.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/minister")
@Slf4j
@RequiredArgsConstructor
public class MinisterController {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentComplaintRepository complaintRepository;
    private final NewsRepository newsRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageRepository messageRepository;
    @GetMapping("/")
    public String redirect(){
        return "redirect:/minister/employees";
    }

    @GetMapping("/departments/add-employee/{department}")
    public String addEmployee(@PathVariable String department,Model model,HttpServletRequest request){
        model.addAttribute("department",department);
        model.addAttribute("districts", District.districts);
        model.addAttribute("requestURI",request.getRequestURI());
        return "minister/addEmployee";
    }
    @GetMapping("/employees")
    public String departmentInfo(Model model, HttpServletRequest request){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        String department = employee.getDepartment();
        List<Employee> employees = employeeRepository.findByDepartmentAndIsHeadFalse(department);
        model.addAttribute("employees",employees);
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("department",department);
        return "minister/department";
    }
    @PostMapping("/add-employee/{department}")
    @Transactional
    public String addEmployeeSubmit(@PathVariable String department,RedirectAttributes ra, @ModelAttribute AddEmployeeDto employeeDto){
        User user = new User();
        User currentUser = getCurrentUser();
        Employee temp = employeeRepository.findEmployeeByEmployeeUsername(currentUser.getUsername());
        if(userRepository.existsByUsername(employeeDto.getUsername())){
            ra.addFlashAttribute("errMessage","User with same username already exists");
            return "redirect:/minister/departments/add-employee/"+department;
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
            return "redirect:/minister/departments/add-employee/"+department;
        }
        ra.addFlashAttribute("message","Employee added successfully");
        return "redirect:/minister/employees";

    }
    @GetMapping("/messages")
    public String messages(Model model,HttpServletRequest request){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        String department = employee.getDepartment();
        List<DepartmentComplaint> complaints = complaintRepository.findByDepartment(department);
        complaints = complaints.stream().filter(complaint -> complaint.getStatus().equals(ComplaintStatus.UNDER_CONSIDERATION)).toList();
        model.addAttribute("complaints",complaints);
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("department",employee.getDepartment());
        return "minister/messages";
    }
    @GetMapping("/messages/chat/{complaintId}")
    public String chat(Model model, HttpServletRequest request, @PathVariable Long complaintId){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        DepartmentComplaint complaint = complaintRepository.findById(complaintId).get();
        List<DepartmentMessage> messages = messageRepository.findByComplaintAndDepartmentAndDistrict(complaint,complaint.getDepartment(),complaint.getDistrict());
        model.addAttribute("messages",messages);
        model.addAttribute("complaint",complaint);
        model.addAttribute("employee",employee);
        model.addAttribute("department",complaint.getDepartment());
        model.addAttribute("user",user);
        model.addAttribute("district",complaint.getDistrict());
        model.addAttribute("requestURI",request.getRequestURI());
        return "minister/chat";
    }
    @PostMapping("/chat/post-chat/{complaintId}")
    public String postChat(RedirectAttributes ra,@PathVariable Long complaintId,
                           @RequestParam("message") String message){
        try{
            User user =getCurrentUser();
            DepartmentComplaint complaint = complaintRepository.findById(complaintId).get();
            DepartmentMessage msg = new DepartmentMessage();
            msg.setMessage(message);
            msg.setUser(user);
            msg.setComplaint(complaint);
            msg.setDepartment(complaint.getDepartment());
            msg.setDistrict(complaint.getDistrict());
            messageRepository.save(msg);
        }
        catch (Exception e){
            ra.addFlashAttribute("errMessage","Something went wrong while sending the message");

        }
        return "redirect:/minister/messages/chat/"+complaintId;
    }


    @GetMapping("/complaints/complaint-details/{complaintId}")
    public String complaintDetails(@PathVariable(name = "complaintId")Long complaintId, Model model, HttpServletRequest request){
        DepartmentComplaint complaint = complaintRepository.findById(complaintId).get();
        model.addAttribute("complaint",complaint);
        model.addAttribute("department",complaint.getDepartment());
        model.addAttribute("requestURI",request.getRequestURI());
        return "minister/complaint";
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
        return "redirect:/minister/employees";
    }
    @GetMapping("/news")
    public String news(Model model,HttpServletRequest request){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        List<DepartmentNews> newsList = newsRepository.findByDepartmentAndDistrict(employee.getDepartment(),employee.getDistrict());
        model.addAttribute("district",employee.getDistrict());
        model.addAttribute("department",employee.getDepartment());
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("news",newsList);
        log.info("Inside News method!!");
        return "minister/news";
    }
    @GetMapping("/news/add-news")
    public String addNews(Model model,HttpServletRequest request){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        model.addAttribute("district",employee.getDistrict());
        model.addAttribute("department",employee.getDepartment());
        model.addAttribute("requestURI",request.getRequestURI());
        return "minister/addNews";
    }
    @PostMapping("/news/add-news")
    public String postNews(RedirectAttributes ra,@ModelAttribute NewsDto newsDto) throws IOException {
        try {
            User user = getCurrentUser();
            Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
            DepartmentNews news = new DepartmentNews();
            news.setNewsHeading(newsDto.getNewsHeading());
            news.setDepartment(employee.getDepartment());
            news.setDistrict(employee.getDistrict());
            news.setNewsContent(newsDto.getNewsContent());
            news.setExpiryDateTime(LocalDate.parse(newsDto.getExpiryDate()));
            news.setImage(fileUploadDir(newsDto.getFile()));
            newsRepository.save(news);
            ra.addFlashAttribute("message","news added successfully");
            return "redirect:/minister/news";
        }
        catch (Exception e){
            ra.addFlashAttribute("errMessage","something went wrong while adding news");
            return "redirect:/minister/news";
        }
    }
    @PostMapping("/complaint/changeStatus/{complaintId}")
    public String changeStatus(@ModelAttribute ChangeComplaintStatusDto statusInfo,
                               RedirectAttributes ra,
                               @PathVariable(name = "complaintId")Long complaintId){
        try {
            DepartmentComplaint complaint = complaintRepository.findById(complaintId).get();
            String status = statusInfo.getStatus();
            if (status.equals("REJECTED")) {
                complaint.setStatus(ComplaintStatus.REJECTED);
                complaint.setRejectedAt(LocalDate.now());
                complaint.setRejectResponse(statusInfo.getResponse());
            }
            if (status.equals("RESOLVED")) {
                complaint.setStatus(ComplaintStatus.RESOLVED);
                complaint.setResolvedAt(LocalDate.now());
                complaint.setResolvedResponse(statusInfo.getResponse());
            }
            if (status.equals("IN_PROGRESS")) {
                complaint.setStatus(ComplaintStatus.IN_PROGRESS);
                complaint.setInProgressAt(LocalDate.now());
                complaint.setInProgressResponse(statusInfo.getResponse());
            }
            complaintRepository.save(complaint);
            if(complaint.getStatus().equals(ComplaintStatus.REJECTED) || complaint.getStatus().equals(ComplaintStatus.RESOLVED))
                ra.addFlashAttribute("message","Changed complaint status to "+complaint.getStatus().name().toLowerCase());
            else
                ra.addFlashAttribute("message","complaint has been sent to department employees and is now in progress");
            return "redirect:/minister/complaints";
        }
        catch (Exception e){
            log.error(e.getMessage());
            ra.addFlashAttribute("errMessage","Something went wrong!!!!");
            return "redirect:/minister/complaints/complaint-details/"+complaintId;
        }
    }
    @GetMapping("/complaints")
    public String complaints(Model model,HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        String department = employee.getDepartment();
        model.addAttribute("district",employee.getDistrict());
        model.addAttribute("department",department);
        log.info("Inside minister complaints method!!");
        List<DepartmentComplaint> complaintList = complaintRepository.findByDepartment(department);
        complaintList = complaintList.stream().filter(complaint -> complaint.getStatus().equals(ComplaintStatus.UNDER_CONSIDERATION)).toList();
        model.addAttribute("complaints",complaintList);
        return "minister/complaints";
    }
    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }
    private String fileUploadDir(MultipartFile file) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Save the file to the upload directory
        String filePath = uploadDir + "/" + fileName;
        Path path = Paths.get(filePath);
        System.out.println(path);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Return the file path
        return fileName;
    }
}
