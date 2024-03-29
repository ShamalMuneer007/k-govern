package org.kerala.kgovern.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/employee/**").hasAnyRole("ADMIN","EMPLOYEE")
                                .requestMatchers("/minister/**").hasAnyRole("HEAD","ADMIN")
                                .requestMatchers("/user/**").hasAnyRole("ADMIN","USER","EMPLOYEE","HEAD")
                                .requestMatchers("/login","/signup","/home/**","/**").permitAll()
                                .anyRequest().authenticated())
                .formLogin(login ->
                        login
                                .loginPage("/login")
                                .permitAll()
                                .successHandler(authenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));
        return httpSecurity.build();
    }
    @Bean
    public WebSecurityCustomizer securityCustomizer(){
        return (web) -> web
                .ignoring()
                .requestMatchers("/img/**","/images","/css/**","/js/**","/scss/**","/fonts/**","/vendor/**","/.sass-cache");
    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
