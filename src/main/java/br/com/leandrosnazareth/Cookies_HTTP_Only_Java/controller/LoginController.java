package br.com.leandrosnazareth.Cookies_HTTP_Only_Java.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping(value = { "/", "/login", "/login.html" })
    public String showLoginPage() {
        return "login"; // Retorna o template `login.html` em Thymeleaf
    }

    @GetMapping("/protected")
    public String showProtectedPage() {
        return "protected"; // Esse é o nome do arquivo protected.html
    }
}
