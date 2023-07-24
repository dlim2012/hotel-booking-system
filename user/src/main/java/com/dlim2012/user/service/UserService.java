package com.dlim2012.user.service;

import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.clients.exception.ResourceNotFoundException;
import com.dlim2012.user.dto.AuthenticationRequest;
import com.dlim2012.user.dto.UserContactInfoRequest;
import com.dlim2012.user.dto.UserContactInfoResponse;
import com.dlim2012.user.dto.UserRegisterRequest;
import com.dlim2012.user.dto.profile.UserProfileItem;
import com.dlim2012.user.entity.Gender;
import com.dlim2012.user.entity.User;
import com.dlim2012.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserService(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;

//        Converter<Gender, String> genderStringConverter = new Converter<Gender, String>() {
//            @Override
//            public String convert(MappingContext<Gender, String> mappingContext) {
//                return mappingContext.getSource() == null ? null : mappingContext.getSource().name();
//            }
//        };
//        modelMapper.addConverter(genderStringConverter);
    }

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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

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
        UserProfileItem userProfileItem = modelMapper.map(user, UserProfileItem.class);
        userProfileItem.setYear(user.getDateOfBirth() == null ? null : String.valueOf(user.getDateOfBirth().getYear()));;
        userProfileItem.setMonth(user.getDateOfBirth() == null ? null : String.valueOf(user.getDateOfBirth().getMonthValue()));;
        userProfileItem.setDay(user.getDateOfBirth() == null ? null : String.valueOf(user.getDateOfBirth().getDayOfMonth()));
        userProfileItem.setGender(user.getGender() == null ? null : user.getGender().name());
        log.info(userProfileItem.toString());
        return userProfileItem;
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

//    public UserContactInfo getContactInfo(Integer userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
//        UserContactInfo userContactInfo = UserContactInfo.builder()
//                .id(user.getId())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .email(user.getEmail())
//                .phoneNumber(user.getPhoneNumber())
//                .build();
//        return userContactInfo;
//    }
}
