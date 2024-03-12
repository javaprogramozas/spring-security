package hu.bearmaster.springtutorial.boot.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.util.Map;

public class MyFormLoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler, LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    public MyFormLoginHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        writeResponse(response, HttpServletResponse.SC_OK, "success");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "failed");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        writeResponse(response, HttpServletResponse.SC_OK, "success");
    }

    private void writeResponse(HttpServletResponse response, int statusCode, String resultString) throws IOException {
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode);
        response.getWriter().print(objectMapper.writeValueAsString(Map.of("result", resultString)));
    }
}
