package com.example.tp.service;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DpiService {
    @Autowired
    private WebClient webClient;

    public String sendDong(String prompt) {
        String system = "너는 마우스 dpi 조절의 전문가야, 출력형식은 항상 “보정한 dpi는 {너가 생각하는 값}입니다. 라고 출력해줘”, 이상치는 계산에서 제외시켜줘";
        String reqest = """
                {
                    "model": "ft:gpt-3.5-turbo-0125:personal::AQzFvvIn",
                    "messages": [
                        {"role":"system", "content":"%s"},
                        {"role": "user", "content": "%s"}
                    ]
                }
                """.formatted(system,prompt);

        String res =  webClient.post()
                .uri("/chat/completions")
                .bodyValue(reqest)
                .retrieve()
                .bodyToMono(String.class) // JSON 응답을 문자열로 받음
                .block();
        String content = JsonPath.read(res, "$.choices[0].message.content");
        return content;
    }
}
