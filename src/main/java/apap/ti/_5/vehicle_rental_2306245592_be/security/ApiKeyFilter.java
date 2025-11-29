package apap.ti._5.vehicle_rental_2306245592_be.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String apiKey;

    // Protected endpoints yang memerlukan API Key
    private static final List<String> PROTECTED_PATHS = Arrays.asList(
            "/api/loyalty/coupons/use",
            "/api/loyalty/points/add"
    );

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, 
                                    @Nonnull HttpServletResponse response, 
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();
        
        log.debug("Processing request: {} {}", requestMethod, requestPath);
        
        // Cek apakah request path cocok dengan protected endpoints
        boolean isProtectedEndpoint = PROTECTED_PATHS.stream()
                .anyMatch(path -> requestPath.equals(path) && "POST".equalsIgnoreCase(requestMethod));
        
        if (isProtectedEndpoint) {
            log.debug("Protected endpoint detected: {}", requestPath);
            
            // Ambil API Key dari header
            String clientApiKey = request.getHeader("API-KEY");
            
            // Validasi API Key
            if (clientApiKey == null || clientApiKey.trim().isEmpty()) {
                log.warn("API Key missing for request to: {}", requestPath);
                sendUnauthorizedResponse(response, "API Key is required");
                return;
            }
            
            if (!apiKey.equals(clientApiKey)) {
                log.warn("Invalid API Key provided for request to: {}", requestPath);
                sendUnauthorizedResponse(response, "Invalid API Key");
                return;
            }
            
            log.debug("API Key validated successfully for: {}", requestPath);
        }
        
        // Lanjutkan ke filter berikutnya jika validasi berhasil atau endpoint tidak dilindungi
        filterChain.doFilter(request, response);
    }

    /**
     * Mengirim response 401 Unauthorized dengan pesan error
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
                "{\"status\": 401, \"message\": \"%s\", \"timestamp\": \"%s\", \"data\": null}",
                message,
                new java.util.Date().toString()
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
