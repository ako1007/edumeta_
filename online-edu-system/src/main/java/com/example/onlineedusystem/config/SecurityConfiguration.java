package com.example.onlineedusystem.config;

import com.example.onlineedusystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                // Своего рода отключение CORS (разрешение запросов со всех доменов)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                // Настройка доступа к конечным точкам
                .authorizeHttpRequests(request -> request
                        // Можно указать конкретный путь, * - 1 уровень вложенности, ** - любое количество уровней вложенности
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api/v1/sign/in","/api/v1/sign/up", "/api/v1/sign/up/verify/{code}",
                        "/api/v1/send-code/{email}","/api/v1/verify/{code}","api/v1/forgot/password/verify","api/v1/delete/{email}","/api/v1/blog/add/{token}",
                                "/api/v1/blog/get/all/{token}","/api/v2/comment/get/{id}/{token}","/api/v2/comment/get/all/course/{id}/{token}",
                                "/api/v2/comment/get/all/teacher/{id}/{token}","/api/v2/profile/add/data",
                                "/api/v2/profile/info","/api/v2/teacher/get/{token}/{teacherId}","/api/v2/teacher/get/{token}","/api/v2/course/get/{courseId}",
                                "/api/v2/course/get/all/{token}").permitAll()
                        .requestMatchers( "/admin/**","/api/v1/blog/add/{token}","/delete/{blogId}/{token}","/api/v2/comment/add/course/{token}",
                                "/api/v2/comment/add/teacher/{token}", "/api/v2/comment/delete/{id}/{token}","/api/v1/teacher/add/{token}",
                                "/api/v2/teacher/edit/{token}","/api/v2/teacher/delete/{token}/{teacherId}","/api/v2/course/add/{token}"
                        ,"/api/v2/course/add/resource/{courseId}/{token}","/api/v2/course/edit/{courseId}/{token}","/api/v2/course/delete/{courseId}/{token}").hasRole("ADMIN")
                        .requestMatchers("/admin/add/{token","/admin/signup/verify/{code}", "api/v1/admin/delete","/delete/{blogId}/{token}","/api/v2/comment/add/course/{token}",
                        "/api/v2/comment/add/teacher/{token}", "/api/v2/comment/delete/{id}/{token}","/api/v1/teacher/add/{token}",
                                "/api/v1/admin/delete/{token}","/api/v2/teacher/edit/{token}","/api/v2/teacher/delete/{token}/{teacherId}").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/v2/teacher/edit/{token}").hasRole("TEACHER")
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
