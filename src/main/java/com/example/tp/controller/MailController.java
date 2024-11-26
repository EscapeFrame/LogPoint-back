package com.example.tp.controller;

import com.example.tp.model.User;
import com.example.tp.service.MailSendService;
import com.example.tp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MailController {
    @Autowired
    private final MailSendService mailService;
    @Autowired
    private UserService userService;

    public MailController(MailSendService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/auth")
    public ResponseEntity<?> authPage(HttpSession session, CsrfToken csrfToken) {
        Map<String, String> map = new HashMap<>();
        map.put("csrf", csrfToken.getToken());
        User user = (User) session.getAttribute("user");
        if(user.getUserName() == null) {
            map.put("message", "You don't have any user");
            return ResponseEntity.status(401).body(map);
        }

        mailService.joinEmail(user.getUserName());
        map.put("message", "Send mail successfully");
        return ResponseEntity.status(200).body(map);
    }

    @PostMapping("/check-auth")
    public ResponseEntity<Map<String, String>> AuthCheck(HttpSession session, @RequestParam String number, Model model, CsrfToken csrfToken){
        User user = (User) session.getAttribute("user");
        Boolean Checked=mailService.CheckAuthNum(user.getUserName(),number);
        Map<String, String> map = new HashMap<>();
        if(Checked){
            System.out.println("공덕현 success");
            userService.signUp(user);
            session.removeAttribute("user");
            map.put("message","success");
            return ResponseEntity.status(200).body(map);
        }
        else{
            System.out.println("공덕현 fail");
            map.put("message","fail");
            return ResponseEntity.status(401).body(map);
        }
    }

}
