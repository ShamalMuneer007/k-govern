package org.kerala.kgovern.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/home")
    @ResponseBody
    public String home(){
        return "home";
    }
    @GetMapping("/")
    public String index(){
        return "redirect:/login";
    }
}
