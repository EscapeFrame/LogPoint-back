package com.example.tp.controller;

import com.example.tp.service.DpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DpiController {

    @Autowired
    private DpiService dpiService;

    @GetMapping("/dpi")
    public ResponseEntity<Map<String, String>> dpi(CsrfToken csrfToken) {
        Map<String, String> map = new HashMap<>();
        map.put("csrf", csrfToken.getToken());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/get-dpi")
    public ResponseEntity<Map<String, String>> chat(@RequestParam int dpi,
                               @RequestParam List<Integer> errors) {
        String errs="";
        int cnt=0;
        for(Integer error : errors) {
            if(cnt!=0)errs+=", "+error;
            else {
                errs+=error;
                cnt++;
            }
        }
        String prompt = "나 마우스 dpi 보정해줘, 오차 5개가 들어올거야, 나는 현재 " + dpi + " dpi 사용중이야. 오차는" + errs;
        System.out.println(prompt);
        String res =  dpiService.sendDong(prompt);
        System.out.println("result : " + res);
        Map<String, String> map = new HashMap<>();
        map.put("result", res);
        return ResponseEntity.ok(map);
    }
}
