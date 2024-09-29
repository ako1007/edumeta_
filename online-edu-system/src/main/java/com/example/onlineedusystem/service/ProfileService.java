package com.example.onlineedusystem.service;

import com.example.onlineedusystem.dto.ApiResponse;
import com.example.onlineedusystem.dto.JwtAuthenticationRequest;
import com.example.onlineedusystem.dto.Profile;
import com.example.onlineedusystem.dto.ProfileResponse;
import com.example.onlineedusystem.entity.User;
import com.example.onlineedusystem.repository.TokenRepository;
import com.example.onlineedusystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private String verificationCode;
    private Profile profile;
    private String email;
    private FileService fileService;
    private AuthenticationService authenticationService;


    public ApiResponse addDataToUser( Profile profile, JwtAuthenticationRequest token) throws IOException {
        if (tokenRepository.findByToken(token.toString()).isEmpty()) throw new RuntimeException("Token not found");
        User user = tokenRepository.findByToken(token.toString()).get().getUser();
        if (userRepository.existsByPhoneNumber(profile.getPhoneNumber()))
            throw new RuntimeException("Phone number already in use");
        if (!user.getEmail().equals(profile.getEmail())){
            if (userRepository.existsByEmail(profile.getEmail())) throw new RuntimeException("Email already in use");
            this.profile = profile;
            authenticationService.sendEmail(profile.getEmail());
            return new ApiResponse(true, "The email with the verification code has been sent");
        }
        user.setFirstName(profile.getFistName());
        user.setLastName(profile.getLastName());
        user.setPhoneNumber(profile.getPhoneNumber());
        user.setEmail(profile.getEmail());
        user.setPostcode(profile.getPostcode());
        if (profile.getProfilePicture() != null) {
           user.setMediaId(fileService.uploadFile(profile.getProfilePicture()));
        }
        userRepository.save(user);
        return new ApiResponse(true,"Profile has been saved");

    }

    public ApiResponse saveProfile(String code, JwtAuthenticationRequest token) throws IOException {
        if (tokenRepository.findByToken(token.toString()).isEmpty()) throw new RuntimeException("Token not found");
        if (!verificationCode.equals(code)) throw new RuntimeException("Invalid verification code");
        User user = userRepository.findByEmail(email).get();
        user.setFirstName(profile.getFistName());
        user.setLastName(profile.getLastName());
        user.setPhoneNumber(profile.getPhoneNumber());
        user.setEmail(profile.getEmail());
        user.setPostcode(profile.getPostcode());
        if (profile.getProfilePicture() != null) {
            user.setMediaId(fileService.uploadFile(profile.getProfilePicture()));
        }
        userRepository.save(user);
        return new ApiResponse(true,"Profile has been saved");
    }

    public ProfileResponse getUser(JwtAuthenticationRequest token) throws IOException {
        if (tokenRepository.findByToken(token.toString()).isEmpty()) throw new RuntimeException("Token not found");
        User user = tokenRepository.findByToken(token.toString()).get().getUser();
        ProfileResponse profile = new ProfileResponse();
        profile.setFistName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setEmail(user.getEmail());
        profile.setPostcode(user.getPostcode());
        if (user.getMediaId() != null) {
            profile.setProfilePicture(fileService.getFile(user.getMediaId()));
        }
        return profile;
    }
}
