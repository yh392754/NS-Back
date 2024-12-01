package YUNS_Backend.YUNS.auth;

import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = tokenProvider.resolveTokenInHeader(request);
        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        AntPathMatcher pathMatcher = new AntPathMatcher();
        String[] excludePath = {"/", "/api/register", "/api/login", "/api/createAdmin"};
        String path = request.getRequestURI();

        for (String pattern : excludePath) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}