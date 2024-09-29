package com.example.onlineedusystem.controller;

import com.example.onlineedusystem.dto.ApiResponse;
import com.example.onlineedusystem.dto.JwtAuthenticationRequest;
import com.example.onlineedusystem.dto.Profile;
import com.example.onlineedusystem.dto.ProfileResponse;
import com.example.onlineedusystem.service.ProfileService;
import com.example.onlineedusystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v2/profile")
public class ProfileController {

    private ProfileService profileService;

    @PostMapping("/add/data")
    public ApiResponse addDataToUser(@RequestBody Profile profile, @RequestBody JwtAuthenticationRequest token) throws IOException {
      return profileService.addDataToUser(profile,token);
    }

    @GetMapping("/info")
    public ProfileResponse getProfileInfo(@RequestHeader("Authorization") JwtAuthenticationRequest token) throws IOException {
        return profileService.getUser(token);
    }

}
