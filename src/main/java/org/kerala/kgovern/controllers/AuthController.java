package org.kerala.kgovern.controllers;

import ch.qos.logback.core.model.Model;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.dto.UserRegisterDto;
import org.kerala.kgovern.exceptions.InternalServerError;
import org.kerala.kgovern.services.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@Slf4j
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!(authentication instanceof AnonymousAuthenticationToken)){
//            if(roles.contains("ADMIN")){
//                return "redirect:/home";//Redirects admin to his dashboard if he tries to go back to log-in page
//            }
//            if(roles.contains("USER")){
//                return "redirect:/home";//Redirects authenticated user to home page if he tries to go back to log-in page
//            }
            return "redirect:/home";
        }
        log.info("In login page");
        return "login";
    }
    @GetMapping("/signup")
    public String signup(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!(authentication instanceof AnonymousAuthenticationToken)){
//            if(roles.contains("ADMIN")){
//                return "redirect:/home";//Redirects admin to his dashboard if he tries to go back to log-in page
//            }
//            if(roles.contains("USER")){
//                return "redirect:/home";//Redirects authenticated user to home page if he tries to go back to log-in page
//            }
            return "redirect:/home";
        }
        log.info("In signup page");
        return "signup";
    }
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute UserRegisterDto userRegisterDto,
                               Model model, RedirectAttributes redirectAttributes){
        log.info("Registering user...");
        try {
            if (userService.userExists(userRegisterDto.getUsername())) {
                log.warn("User with username already exists!");
                redirectAttributes.addFlashAttribute("errMessage", "Username already exists!");
                return "redirect:/signup";
            }
            if (userService.emailExists(userRegisterDto.getEmail())) {
                log.warn("User with email already exists!");
                redirectAttributes.addFlashAttribute("errMessage", "Email already exists!");
                return "redirect:/signup";
            }
            userService.registerUser(userRegisterDto);
        }
        catch (Exception e){
            log.error("Something went wrong while registering the user");
            throw new InternalServerError("Something went wrong while registering the user");
        }
        log.info("User registered successfully!");
        redirectAttributes.addFlashAttribute("message","User has registered successfully !");
        return "redirect:/login";
    }
}
