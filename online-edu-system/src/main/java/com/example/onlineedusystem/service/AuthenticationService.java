package com.example.onlineedusystem.service;

import com.example.onlineedusystem.dto.*;
import com.example.onlineedusystem.entity.Teacher;
import com.example.onlineedusystem.entity.Token;
import com.example.onlineedusystem.entity.User;
import com.example.onlineedusystem.entity.enums.Role;
import com.example.onlineedusystem.entity.enums.TokenType;
import com.example.onlineedusystem.repository.TeacherRepository;
import com.example.onlineedusystem.repository.TokenRepository;
import com.example.onlineedusystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JavaMailSender javaMailSender;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final FileService fileService;
    private final TeacherRepository teacherRepository;
    private Boolean isTrue = true;

    private Registration registration;
    private String verificationCode;
    private String emailRequst;

    public ApiResponse signUp(Registration request) {
        if (request.getPassword().length() < 8 || request.getPassword().length() > 255 || !request.getPassword().equals(request.getRepassword()))
            throw new RuntimeException("Passwords do not match");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email address already in use");

        registration = request;
        sendEmail(request.getEmail());
        return new ApiResponse(true, "The email with the verification code has been sent");
    }

    public JwtAuthenticationResponse confirmationCodeAndSave(String code, Role role, Admin admin, TeacherRequest teacherRequest) throws IOException {
        if (!code.equals(verificationCode)) {
            throw new RuntimeException("Verification code is invalid");
        }

        User user = new User();
        user.setEnabled(true);
        user.setFirstName(registration.getName());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));

        if (role.equals(Role.ROLE_ADMIN)) {
            user.setRole(Role.ROLE_ADMIN);
        } else if (role.equals(Role.ROLE_TEACHER)) {
            Teacher teacher = new Teacher();
            teacher.setFirstName(teacherRequest.getFistName());
            teacher.setLastName(teacherRequest.getLastName());
            teacher.setEmail(teacherRequest.getEmail());
            teacher.setPhoneNumber(teacherRequest.getPhoneNumber());
            teacher.setExperience(teacherRequest.getExperience());
            teacher.setScience(teacherRequest.getScience());
            teacher.setBio(teacherRequest.getBio());
            teacher.setPicture(fileService.uploadFile(teacherRequest.getImage()));
            teacher.setPassword(passwordEncoder.encode(registration.getPassword()));
            teacherRepository.save(teacher);
        }

        user.setRole(role);
        User savedUser = userRepository.save(user);
        var jwt = jwtService.generateToken(savedUser);
        saveUserToken(savedUser, jwt);
        return new JwtAuthenticationResponse(jwt, savedUser.getEmail());
    }

    public JwtAuthenticationResponse signIn(Login request) {
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            throw new UsernameNotFoundException("User not found");}
        User user = userRepository.findByEmail(request.getEmail()).get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (user.isEnabled()) {
            var jwtToken = jwtService.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return JwtAuthenticationResponse.builder().token(jwtToken).email(user.getEmail()).build();
        }
        return JwtAuthenticationResponse.builder().token("First verify your account").build();
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = new Token();

        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public ApiResponse forgotPasswordEmail(String email) {
        if (!userRepository.existsByEmail(email)) throw new RuntimeException("Email not found!");
        emailRequst = email;
        sendEmail(email);
        return new ApiResponse(true, "The email with the verification code has been sent");
    }

    public ApiResponse verification(String verificationCode1) {
        if (!verificationCode.equals(verificationCode1)) throw new RuntimeException("Verification code does not match");
        isTrue = true;
        return new ApiResponse(true, "The verification is true");
    }

    public JwtAuthenticationResponse forgotPassword(PasswordChangeRequest password) {
        if (!isTrue || password.getPassword().length() < 8 || password.getPassword().length() > 255 || !password.getPassword().equals(password.getReEnterPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        var user = userRepository.findByEmail(emailRequst).get();
        userRepository.findByEmail(emailRequst).get().setPassword(passwordEncoder.encode(password.getPassword()));
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return JwtAuthenticationResponse.builder().token(jwtToken).email(user.getEmail()).build();
    }

    public ApiResponse userDeleteAccount(String token, Login login) {
        if (!userRepository.existsByEmail(login.getEmail())) throw new UsernameNotFoundException(login.getEmail());
        User user = userRepository.findByEmail(login.getEmail()).get();
        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        tokenRepository.deleteTokensByUserId(user.getId());
//        tokenRepository.detokenRepository.findTokenByUserId(user.getId());
        userRepository.delete(user);
        return new ApiResponse(true, "The account has been deleted.");
    }

    public ApiResponse deleteAdmin(String emailRequest, JwtAuthenticationRequest superAdminToken) {
        if (tokenRepository.findByToken(superAdminToken.toString()).isEmpty())
            throw new RuntimeException("Token not found");
        if (!userRepository.existsByEmail(emailRequest)) throw new RuntimeException("User not found");
        User user = userRepository.findByEmail(emailRequest).get();
        tokenRepository.deleteTokensByUserId(user.getId());
        userRepository.delete(user);
        return new ApiResponse(true, "The user with email has been removed");
    }

    public void sendEmail(String email) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        System.out.println(code);
        verificationCode = code.toString();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("akobir.botirov@icloud.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Verification");
        simpleMailMessage.setText("  \n" + "Hi,\n" + "\n" + "Someone tried to sign up for an EDUMATE account with " + email + ". If it was you, here is your verification code: " + code);
        javaMailSender.send(simpleMailMessage);
    }
}
