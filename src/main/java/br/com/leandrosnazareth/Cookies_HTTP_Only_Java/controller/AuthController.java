package br.com.leandrosnazareth.Cookies_HTTP_Only_Java.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.leandrosnazareth.Cookies_HTTP_Only_Java.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        // Lógica de autenticação (aqui com valores fixos para exemplo)
        if ("user".equals(username) && "password".equals(password)) {
            // Gere o token JWT
            String token = JwtUtil.generateToken(username);

            // Crie o cookie HTTP-Only e Secure
            ResponseCookie cookie = ResponseCookie.from("authToken", token)
                    .httpOnly(true)
                    .secure(true) // Alterar para true para garantir que o cookie só será transmitido via HTTPS
                    .path("/")
                    .maxAge(24 * 60 * 60) // Expiração de 1 dia
                    .build();

            // Retorna a resposta com o cookie JWT
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body("Login bem-sucedido!");
        }
        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String token = authorizationHeader.substring(7);
        String username = JwtUtil.validateToken(token);

        if (username != null) {
            return ResponseEntity.ok("Token válido");
        }
        return ResponseEntity.status(401).body("Token inválido");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Limpa o cookie removendo o token (expirando imediatamente)
        ResponseCookie cookie = ResponseCookie.from("authToken", "")
                .httpOnly(true)
                .secure(true) // Alterar para true em produção
                .path("/")
                .maxAge(0) // Expira o cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logout bem-sucedido!");
    }

}
