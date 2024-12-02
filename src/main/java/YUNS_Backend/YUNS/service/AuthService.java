package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.auth.TokenProvider;
import YUNS_Backend.YUNS.dto.LoginRequestDto;
import YUNS_Backend.YUNS.dto.LoginResponseDto;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.redis.BlackList;
import YUNS_Backend.YUNS.redis.BlackListRepository;
import YUNS_Backend.YUNS.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static YUNS_Backend.YUNS.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BlackListRepository blackListRepository;

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        User user = userRepository.findByStudentNumber(loginRequestDto.getStudentNumber());
        if(user == null){
            throw new CustomException(USER_NOT_FOUND);
        }

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new CustomException(LOGIN_INFO_INVALID);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getStudentNumber(),
                        loginRequestDto.getPassword()
                )
        );

        String token = tokenProvider.generateAccessToken(authentication);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .studentNumber(user.getStudentNumber())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole())
                .userRentalStatus(user.isUserRentalStatus())
                .accessToken(token)
                .build();

        return loginResponseDto;
    }

    public void logout(String accessToken, String studentNumber) {

        if (accessToken == null || !tokenProvider.validateToken(accessToken)) {
            throw new CustomException(INVALID_TOKEN);
        }

        Claims claims = tokenProvider.parseClaims(accessToken);
        long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();

        blackListRepository.save(new BlackList(studentNumber, accessToken, expiration));
    }

}
