package com.dlim2012.user.service;

import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.clients.kafka.dto.user.DeleteUserRequest;
import com.dlim2012.user.dto.AuthenticationRequest;
import com.dlim2012.user.dto.UserContactInfoRequest;
import com.dlim2012.user.dto.UserContactInfoResponse;
import com.dlim2012.user.dto.UserRegisterRequest;
import com.dlim2012.user.dto.profile.NewPasswordRequest;
import com.dlim2012.user.dto.profile.NewPasswordResponse;
import com.dlim2012.user.dto.profile.UserProfileItem;
import com.dlim2012.user.entity.Gender;
import com.dlim2012.user.entity.User;
import com.dlim2012.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, DeleteUserRequest> deleteUserDeleteKafkaTemplate;

    public String register(UserRegisterRequest userRegisterRequest){

        if (userRepository.existsByEmail(userRegisterRequest.getEmail())){
            throw new IllegalArgumentException("Email already exists.");
        }

        User user = User.builder()
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .userRole(UserRole.APP_USER)
                .userCreatedAt(LocalDateTime.now())
                .locked(false)
                .build();

        //todo
        user.setDateOfBirth(LocalDate.now());

        userRepository.save(user);
        return tokenService.generateToken(user);
    }

    public String authenticate(AuthenticationRequest authenticationRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (Exception e){
            throw new IllegalArgumentException("Authentication failed.");
        }

        // user is manually set into SecurityContextHolder during authentication
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return tokenService.generateToken(user);
    }

    public void editProfile(UserProfileItem userProfileItem, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        log.info(userProfileItem.toString());
        if (userProfileItem.getFirstName() != null){
            user.setFirstName(userProfileItem.getFirstName());
        }
        if (userProfileItem.getLastName() != null){
            user.setLastName(userProfileItem.getLastName());
        }
        if (userProfileItem.getDisplayName() != null){
            user.setDisplayName(userProfileItem.getDisplayName());
        }
        if (userProfileItem.getEmail() != null){
            user.setEmail(userProfileItem.getEmail());
        }
        if (userProfileItem.getPhoneNumber() != null){
            user.setPhoneNumber(userProfileItem.getPhoneNumber());
        }
        if (userProfileItem.getDay() != null
            && userProfileItem.getMonth() != null
                && userProfileItem.getYear() != null
        ){
            log.info(String.valueOf(LocalDate.of(
                    Integer.valueOf(userProfileItem.getYear()),
                    Integer.valueOf(userProfileItem.getMonth()),
                    Integer.valueOf(userProfileItem.getDay()))));
            user.setDateOfBirth(LocalDate.of(
                    Integer.valueOf(userProfileItem.getYear()),
                    Integer.valueOf(userProfileItem.getMonth()),
                    Integer.valueOf(userProfileItem.getDay())));
        }
        if (userProfileItem.getGender() != null){
            user.setGender(Gender.valueOf(userProfileItem.getGender()));
        }
        userRepository.save(user);
        System.out.println(userRepository.findById(userId));
    }

    public UserProfileItem getProfile(Integer userId, String userEmail) {
        User user = userRepository.findByIdAndEmail(userId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return UserProfileItem.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .year(user.getDateOfBirth() == null ? null : String.valueOf(user.getDateOfBirth().getYear()))
                .month(user.getDateOfBirth() == null ? null : String.valueOf(user.getDateOfBirth().getMonthValue()))
                .day(user.getDateOfBirth() == null ? null : String.valueOf(user.getDateOfBirth().getDayOfMonth()))
                .gender(user.getGender() == null ? null : user.getGender().name())
                .build();
    }

    public List<UserContactInfoResponse> getContactInfo(List<UserContactInfoRequest> request) {
        return userRepository.findByIds(request.stream()
                .map(UserContactInfoRequest::getUserId)
                .toList()
        ).stream()
        .map(user -> UserContactInfoResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build()).toList();
    }

    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        deleteUserDeleteKafkaTemplate.send("delete-user", DeleteUserRequest.builder().userId(userId).build());
        userRepository.delete(user);
    }

    public NewPasswordResponse changePassword(Integer userId, NewPasswordRequest newPasswordRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        try{
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        newPasswordRequest.getPrevPassword()
                )
        );
        } catch (Exception e){
            return new NewPasswordResponse(false, false);
        }

        if (newPasswordRequest.getNewPassword().length() < 8 || newPasswordRequest.getNewPassword().length() > 20){
            return new NewPasswordResponse(true, false);
        }

        try {
            user.setPassword(passwordEncoder.encode(newPasswordRequest.getNewPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            return new NewPasswordResponse(true, false);
        }

        return new NewPasswordResponse(true, true);
    }

}
