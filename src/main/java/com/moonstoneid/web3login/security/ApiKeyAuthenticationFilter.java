package com.moonstoneid.web3login.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonstoneid.web3login.api.model.ErrorResponseAM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import com.moonstoneid.web3login.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
public class ApiKeyAuthenticationFilter extends GenericFilterBean {

    public static class ApiKeyAuthenticationException extends AuthenticationException {

        public ApiKeyAuthenticationException(String msg) {
            super(msg);
        }

    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String apiKey;

    public ApiKeyAuthenticationFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    public void init() {
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = RandomStringUtils.random(32, true, true);
            log.info("Auto-generated API key: " + apiKey);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String providedApiKey = extractApiKey(request);

        if (providedApiKey == null) {
            SecurityContextHolder.clearContext();
            commence(response, new ApiKeyAuthenticationException("API key missing!"));
            return;
        }

        if (!providedApiKey.equals(apiKey)) {
            SecurityContextHolder.clearContext();
            commence(response, new ApiKeyAuthenticationException("Invalid API key!"));
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthenticationToken());

        chain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        return request.getHeader(AppConstants.API_KEY_HEADER);
    }

    public void commence(HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseAM errorResponse = new ErrorResponseAM(status.value(), authException.getMessage());

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

}
