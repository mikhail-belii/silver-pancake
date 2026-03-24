package silverpancake.presentation.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import silverpancake.application.model.auth.AuthorizationModel;
import silverpancake.application.model.common.Response;
import silverpancake.application.service.LoggedOutTokenService;
import silverpancake.application.util.JwtUtil;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtFilter implements Filter {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final LoggedOutTokenService loggedOutTokenService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return;
        }

        var token = authHeader.substring(7);
        try {
            if (loggedOutTokenService.isTokenLoggedOut(token)) {
                writeUnauthorized(response);
                return;
            }

            var claims = jwtUtil.parseAccessClaims(token);
            var authModel = new AuthorizationModel(claims.get("user_id", String.class), token);

            request.setAttribute("authModel", authModel);
        } catch (Exception e) {
            writeUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Boolean isPublicEndpoint(String uri) {
        return uri.equals("/api/auth/login") || uri.equals("/api/auth/register")
                || uri.equals("/api/auth/refresh-tokens");
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Response.error(401, "Unauthorized"));
    }
}

