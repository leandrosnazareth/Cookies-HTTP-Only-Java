package br.com.leandrosnazareth.Cookies_HTTP_Only_Java.util;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter implements Filter {

    @Override
    public void doFilter(jakarta.servlet.ServletRequest servletRequest,
                         jakarta.servlet.ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Procure o cookie authToken no request
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie authTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> "authToken".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);

            if (authTokenCookie != null) {
                // Valide o token JWT
                String username = JwtUtil.validateToken(authTokenCookie.getValue());

                if (username != null) {
                    // Se válido, configure a autenticação no contexto de segurança
                    PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                            username, null, null);  // Aqui você pode adicionar authorities se necessário
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Se o token for inválido ou expirado, você pode adicionar uma resposta
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return; // Evita a continuidade do filtro
                }
            }
        }

        // Prosegue com a cadeia de filtros
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
