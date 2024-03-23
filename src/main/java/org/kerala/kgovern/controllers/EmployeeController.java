package org.kerala.kgovern.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.ComplaintStatus;
import org.kerala.kgovern.dto.ChangeComplaintStatusDto;
import org.kerala.kgovern.dto.NewsDto;
import org.kerala.kgovern.entities.DepartmentComplaint;
import org.kerala.kgovern.entities.DepartmentNews;
import org.kerala.kgovern.entities.Employee;
import org.kerala.kgovern.entities.User;
import org.kerala.kgovern.repositories.DepartmentComplaintRepository;
import org.kerala.kgovern.repositories.EmployeeRepository;
import org.kerala.kgovern.repositories.NewsRepository;
import org.kerala.kgovern.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/employee")
@Slf4j
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentComplaintRepository complaintRepository;
    private final NewsRepository newsRepository;
    @GetMapping("/")
    public String departmentRedirect(){
        return "redirect:/employee/complaints";
    }
    @GetMapping("/complaints")
    public String redirectToComplaints(Model model,HttpServletRequest request){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        List<DepartmentComplaint> complaintList = complaintRepository.findByDepartmentAndDistrict(employee.getDepartment(),employee.getDistrict());
        model.addAttribute("district",employee.getDistrict());
        model.addAttribute("department",employee.getDepartment());
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("complaints",complaintList);
        log.info("Inside complaints method!!");
        return "employee/complaints";
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
        return "employee/news";
    }
    @GetMapping("/news/add-news")
    public String addNews(Model model,HttpServletRequest request){
        User user = getCurrentUser();
        Employee employee = employeeRepository.findEmployeeByEmployeeUsername(user.getUsername());
        model.addAttribute("district",employee.getDistrict());
        model.addAttribute("department",employee.getDepartment());
        model.addAttribute("requestURI",request.getRequestURI());
        return "employee/addNews";
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
            return "redirect:/employee/news";
        }
        catch (Exception e){
            ra.addFlashAttribute("errMessage","something went wrong while adding news");
            return "redirect:/employee/news";
        }
    }
    @GetMapping("/complaints/complaint-details/{complaintId}")
    public String complaintDetails(@PathVariable(name = "complaintId")Long complaintId, Model model, HttpServletRequest request){
        DepartmentComplaint complaint = complaintRepository.findById(complaintId).get();
        model.addAttribute("complaint",complaint);
        model.addAttribute("department",complaint.getDepartment());
        model.addAttribute("requestURI",request.getRequestURI());
        return "employee/complaint";
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
            ra.addFlashAttribute("message","Changed complaint status successfully");
            return "redirect:/employee/complaints";
        }
        catch (Exception e){
            log.error(e.getMessage());
            ra.addFlashAttribute("errMessage","Something went wrong!!!!");
            return "redirect:/employee/complaints/complaint-details/"+complaintId;
        }
    }
    @PostMapping("/deleteComplaint/{complaintId}")
    public String deleteComplaint(RedirectAttributes ra, @PathVariable Long complaintId){
        try {
            complaintRepository.deleteById(complaintId);
            ra.addFlashAttribute("message", "Complaint deleted successfully!!");
            return "redirect:/employee/complaints";
        }
        catch (Exception e){
            ra.addFlashAttribute("errMessage", "Something went wrong please try again "+e.getMessage());
            return "redirect:/employee/complaints";
        }
    }
    // Image upload directory method
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

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }
}
