package org.example.internship_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/enums/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/students/me").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/companies").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.POST, "/api/internships").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.GET, "/api/internships/my").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/internships/{id}").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/internships/{id}").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.GET, "/api/internships/{id}/applications").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.GET, "/api/applications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/applications").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/applications/my").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PATCH, "/api/applications/{id}/status").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/companies/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/companies/{id}/registration").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // Exposes the AuthenticationManager as a bean so it can be injected
    // (e.g. in AuthService.verify() for login). Spring builds it from the
    // AuthenticationProvider bean above.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
