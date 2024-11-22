package YUNS_Backend.YUNS.custom;

import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String studentNumber = authentication.getName();
        User user = userRepository.findByStudentNumber(studentNumber);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("studentNumber", studentNumber);
        responseData.put("userId", user.getUserId());
        responseData.put("role", user.getRole());
        responseData.put("name", user.getName());
        responseData.put("email", user.getEmail());
        responseData.put("phoneNumber", user.getPhoneNumber());
        responseData.put("userRentalStatus", user.isUserRentalStatus());

        String sessionId = request.getSession().getId();

//        Cookie jsessionCookie = new Cookie("JSESSIONID", sessionId);
//        jsessionCookie.setHttpOnly(true);
//        jsessionCookie.setSecure(false);
//        jsessionCookie.setPath("/");
//        jsessionCookie.setMaxAge(60 * 60);
//        response.addCookie(jsessionCookie);
        String cookieValue = "JSESSIONID=" + sessionId + "; Path=/; HttpOnly; Secure=false; SameSite=None";
        response.setHeader("Set-Cookie", cookieValue);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
    }
}
