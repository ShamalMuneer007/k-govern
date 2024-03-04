package org.kerala.kgovern.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.dto.ComplaintReport;
import org.kerala.kgovern.entities.DepartmentComplaint;
import org.kerala.kgovern.entities.User;
import org.kerala.kgovern.repositories.DepartmentComplaintRepository;
import org.kerala.kgovern.repositories.EmployeeRepository;
import org.kerala.kgovern.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final DepartmentComplaintRepository complaintRepository;
    @GetMapping("/complaints")
    public String complaints(Model model, HttpServletRequest request){
        User user = getCurrentUser();
        List<DepartmentComplaint> complaints = complaintRepository.findByUserUsername(user.getUsername());
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("complaints",complaints);
        return "user/complaints";
    }
    @GetMapping("/service/{department}/addComplaint")
    public String addComplaint(Model model,@PathVariable String department,HttpServletRequest request){
        model.addAttribute("department",department);
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("districts", List.of("Trivandrum", "Kollam", "Pathanamthitta", "Alappuzha", "Kottayam", "Idukki", "Ernakulam", "Thrissur", "Palakkad", "Malappuram", "Kozhikode", "Wayanad", "Kannur", "Kasaragod"));
        return "user/addComplaint";
    }
    @PostMapping("/service/{department}/addComplaint")
    public String postComplaint(RedirectAttributes ra,@PathVariable String department,@ModelAttribute ComplaintReport complaintReport){
        User user = getCurrentUser();
        DepartmentComplaint complaint = new DepartmentComplaint();
        complaint.setComplaint(complaintReport.getComplaint());
        complaint.setComplaintSubject(complaintReport.getComplaintSub());
        complaint.setDepartment(department);
        complaint.setDistrict(complaintReport.getDistrict());
        complaint.setUser(user);
        complaintRepository.save(complaint);
        ra.addFlashAttribute("message","Complaint reported successfully!!");
        return "redirect:/service/"+department;
    }
    @PostMapping("/deleteComplaint/{complaintId}")
    public String deleteComplaint(RedirectAttributes ra,@PathVariable Long complaintId){
        complaintRepository.deleteById(complaintId);
        ra.addFlashAttribute("message","Complaint deleted successfully!!");
        return "redirect:/user/complaints";
    }
    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }
}
