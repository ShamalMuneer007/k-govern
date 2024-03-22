package org.kerala.kgovern.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.kerala.kgovern.Enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;


@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if(authorities.contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))){
            log.info("++++ HELLO ADMIN ++++");
            response.sendRedirect("/admin/departments");
        }
        else if(authorities.contains(new SimpleGrantedAuthority(Role.ROLE_EMPLOYEE.name()))){
            response.sendRedirect("/employee/");
        }
        else if(authorities.contains(new SimpleGrantedAuthority(Role.ROLE_HEAD.name()))){
            response.sendRedirect("/minister/");
        }
        else{
            response.sendRedirect("/home");
        }
    }
}
