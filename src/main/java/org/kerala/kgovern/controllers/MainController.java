package org.kerala.kgovern.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/home")
    public String home(Model model,HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        return "home";
    }
    @GetMapping("/")
    public String index(){
        return "redirect:/home";
    }
    @GetMapping("/service/{department}")
    public String getServiceInfo(@PathVariable String department, Model model, HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("department",department);
        return "service";
    }
}
