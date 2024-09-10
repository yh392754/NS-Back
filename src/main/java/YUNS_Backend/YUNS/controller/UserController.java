package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.UserRegisterDto;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/api/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserRegisterDto userRegisterDto, BindingResult bindingResult){

        //입력된 값에 이상이 있을 경우 400에러 반환
        if(bindingResult.hasErrors()){
            Map<String, String> error = new HashMap<>();
            error.put("message", "회원가입에 실패했습니다");

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        try{
            User user = User.createUser(userRegisterDto, passwordEncoder);
            userService.registerUser(user);
        }catch (IllegalStateException e){
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }catch (Exception e){ //서비스에서 에러 발생시 500에러 반환
            Map<String, String> error = new HashMap<>();
            error.put("message", "회원가입에 실패했습니다");

            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었습니다");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
