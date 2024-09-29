package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.UserRegisterDto;
import YUNS_Backend.YUNS.entity.Role;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.exception.ErrorCode;
import YUNS_Backend.YUNS.repository.UserRepository;
import YUNS_Backend.YUNS.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping(value = "/api/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRegisterDto userRegisterDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                String field = fieldError.getField();

                switch (field) {
                    case "email":
                        throw new CustomException(ErrorCode.USER_EMAIL_INVALID);
                    case "password":
                        throw new CustomException(ErrorCode.USER_PASSWORD_INVALID);
                    default:
                        throw new CustomException(ErrorCode.USER_INVALID_INPUT);
                }
            }
        }

        try{
            User user = User.createUser(userRegisterDto, passwordEncoder);
            userService.registerUser(user);
        } catch (CustomException ce){
            throw ce;
        } catch (Exception e){
            throw new CustomException(ErrorCode.USER_REGIST_FAILED);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었습니다");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/users/{userId}")
    public @ResponseBody ResponseEntity unRegister(@PathVariable("userId") Long userId, Principal principal){
        String studentNumber = principal.getName();
        User user = userService.findUserByStudentNumber(studentNumber);

        if(userId != user.getUserId()){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        userService.deleteUser(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원탈퇴가 완료되었습니다");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //테스트를 위한 admin 생성용 api
    @PostMapping("/api/createAdmin")
    public @ResponseBody ResponseEntity createAdmin(@Valid @RequestBody UserRegisterDto userRegisterDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new CustomException(ErrorCode.USER_INVALID_INPUT);
        }

        User user = User.builder()
                .studentNumber(userRegisterDto.getStudentNumber())
                .name(userRegisterDto.getName())
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .phoneNumber(userRegisterDto.getPhoneNumber())
                .email(userRegisterDto.getEmail())
                .role(Role.ADMIN)
                .userRentalStatus(false)
                .build();

        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
