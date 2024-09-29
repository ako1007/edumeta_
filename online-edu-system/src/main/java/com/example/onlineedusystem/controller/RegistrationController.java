package com.example.onlineedusystem.controller;

import com.example.onlineedusystem.dto.*;
import com.example.onlineedusystem.entity.Token;
import com.example.onlineedusystem.entity.enums.Role;
import com.example.onlineedusystem.repository.TokenRepository;
import com.example.onlineedusystem.service.AuthenticationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;


@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1")
public class RegistrationController {

    private final AuthenticationService authenticationService;

    private final TokenRepository tokenRepository;


    @PostMapping(value = "/sign/in", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @NonNull Login login) {
        JwtAuthenticationResponse response = authenticationService.signIn(login);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign/up")
    public ResponseEntity<ApiResponse> signUp(@RequestBody Registration registration) {
        return ResponseEntity.ok(authenticationService.signUp(registration));
    }


    @PutMapping("/sign/up/verify/{code}")
    public ResponseEntity<JwtAuthenticationResponse> confirmEmailForRegistration(@PathVariable String code) throws IOException {
        return ResponseEntity.ok(authenticationService.confirmationCodeAndSave(code, Role.ROLE_USER, null, null));
    }

    @PutMapping("/send-code/{email}")
    public ResponseEntity<ApiResponse> rePassword(@PathVariable String email) {
        return ResponseEntity.ok(authenticationService.forgotPasswordEmail(email));
    }

    @PutMapping("/verify/{code}")
    public ResponseEntity<ApiResponse> confirmEmailForPassword(@PathVariable String code) {
        return ResponseEntity.ok(authenticationService.verification(code));
    }

    @PostMapping("/forgot/password/verify")
    public ResponseEntity<JwtAuthenticationResponse> forgotPassword(@RequestBody PasswordChangeRequest password) {
        return ResponseEntity.ok(authenticationService.forgotPassword(password));
    }

    @DeleteMapping(value = "/delete/{token}")
    public ResponseEntity<ApiResponse> deleteAccount(@PathVariable String token,@RequestBody Login login) {
        return ResponseEntity.ok(authenticationService.userDeleteAccount(token, login));
    }

    @PostMapping("/admin/add/{token}")
    public ResponseEntity<ApiResponse> adminAdd(@RequestBody Registration admin, @PathVariable JwtAuthenticationRequest token) {
        Optional<Token> token1 = tokenRepository.findByToken(token.toString());
        if (token1.isEmpty()) throw new RuntimeException("Token not found");
        if (!token1.get().getUser().getRole().equals(Role.ROLE_SUPER_ADMIN))
            throw new RuntimeException("Role is not SUPER ADMIN");
        return ResponseEntity.ok(authenticationService.signUp(admin));
    }

    @PutMapping("/admin/signup/verify/{code}")
    public ResponseEntity<JwtAuthenticationResponse> confirmEmailForRegistrationAdmin(@PathVariable String code, @RequestBody Admin admin) throws IOException {
        return ResponseEntity.ok(authenticationService.confirmationCodeAndSave(code, Role.ROLE_ADMIN,admin, null));
    }

    @PostMapping("/teacher/add/{token}")
    public ResponseEntity<ApiResponse> addTeacher(@RequestBody Registration admin, @PathVariable JwtAuthenticationRequest token)  {
        Optional<Token> token1 = tokenRepository.findByToken(token.toString());
        if (token1.isEmpty()) throw new RuntimeException("Token not found");
        if (!token1.get().getUser().getRole().equals(Role.ROLE_SUPER_ADMIN))
            throw new RuntimeException("Role is not SUPER ADMIN");
        return ResponseEntity.ok(authenticationService.signUp(admin));
    }

    @PutMapping("/teacher/signup/verify/{code}")
    public ResponseEntity<JwtAuthenticationResponse> confirmEmailForRegistrationTeacher(@PathVariable String code, @RequestBody TeacherRequest teacher) throws IOException {
        return ResponseEntity.ok(authenticationService.confirmationCodeAndSave(code, Role.ROLE_TEACHER,null,teacher));
    }



    @DeleteMapping("/admin/delete/{email}/{token}")
    public ResponseEntity<ApiResponse> deleteAdmin(@PathVariable String email, @PathVariable JwtAuthenticationRequest token) {
        return ResponseEntity.ok(authenticationService.deleteAdmin(email, token));
    }

}
