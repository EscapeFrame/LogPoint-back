package com.example.tp.controller;

import com.example.tp.model.User;
import com.example.tp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> home(HttpSession session, CsrfToken csrfToken) {
        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
        if (isLogin == null) {
            isLogin = false; // 기본값 설정
        }
        System.out.println("Home page accessed. isLogin: " + isLogin);
        Map<String, Object> map = new HashMap<>();
        map.put("isLogin", isLogin);
        map.put("userName", session.getAttribute("userName"));
        map.put("message", "Hello World");
        map.put("csrfToken", csrfToken.getToken());
        return ResponseEntity.ok(map);
    }

    @GetMapping("/signup")
    public ResponseEntity<Map<String, String>> signupPage(Model model, CsrfToken csrfToken) {
        Map<String, String> map = new HashMap<>();
        map.put("csrf", csrfToken.getToken());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestParam String userName,
                         @RequestParam String userPassword,
                         HttpSession session) {
        Map<String, String> map = new HashMap<>();
        User user = new User();
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        String result = userService.userExists(user);
        if (result.equals("success")) {
            session.setAttribute("user", user);
            map.put("message", "success");
        }
        else {
            map.put("message", result);
        }
        return ResponseEntity.ok(map);
    }
    @GetMapping("/signin")
    public ResponseEntity<Map<String, String>> loginPage(CsrfToken csrfToken) {
        Map<String, String> map = new HashMap<>();
        map.put("csrf", csrfToken.getToken());
        System.out.println("login Page");
        return ResponseEntity.ok(map);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestParam String userName,
                        @RequestParam String userPassword,
                        HttpSession session) {
        Map<String, String> map = new HashMap<>();
        try {
            String result = userService.signin(userName, userPassword).join(); // 결과를 기다리기
            if (result.startsWith("login success")) {
                System.out.println("login success");
                session.setAttribute("userName", userName);
                session.setAttribute("isLogin", true); // 로그인 상태 설정
                map.put("message", "success");
            }
            else {
                session.setAttribute("isLogin", false);
                System.out.println("login failed");
                map.put("message", result);
            }

        } catch (Exception e) {
            map.put("message", e.getMessage());
        }
        return ResponseEntity.ok(map);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화

        Map<String, String> map = new HashMap<>();
        map.put("message", "logout");
        return ResponseEntity.ok(map);
    }
}