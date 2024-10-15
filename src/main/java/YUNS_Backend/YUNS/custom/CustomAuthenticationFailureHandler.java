package YUNS_Backend.YUNS.custom;

import YUNS_Backend.YUNS.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.LOGIN_INFO_INVALID;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{ \"code\": \"" + errorCode.getCode() + "\", " +
                        "\"message\": \"" + errorCode.getMessage() + "\" }"
        );
    }
}
