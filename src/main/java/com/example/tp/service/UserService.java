package com.example.tp.service;

import com.example.tp.model.User;
import com.example.tp.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String userExists(User user) {
        try {
            boolean userExists = userRepository.userEameExist(user.getUserName()).get();  // `get()`으로 결과를 기다림

            // 이미 존재하는 사용자라면 가입 불가
            if (userExists)  return "이미 존재하는 사용자입니다.";
            else return "success";
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "사용자 존재 여부 확인 중 오류 발생";
        }
    }
    public String signUp(User user) {
        try {
            // 비밀번호 해시 처리
            String hashedPassword = hashPassword(user.getUserPassword());
            user.setUserPassword(hashedPassword);

            // Firebase에 사용자 등록
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(user.getUserName())
                    .setPassword(user.getUserPassword());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            String uid = userRecord.getUid();

            // Firebase에 사용자 정보 저장
            userRepository.saveUser(user, uid);

            return "success";

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return "회원가입 실패: " + e.getMessage();
        }
    }
    public CompletableFuture<String> signin(String userName, String userPassword) {
        // 사용자 이름 존재 여부 확인
        return userRepository.userEameExist(userName).thenCompose(exists -> {
            if (!exists) {
                return CompletableFuture.completedFuture("이메일을 찾을 수 없습니다.");
            }

            // 사용자 이름으로 비밀번호 가져오기
            return userRepository.getPasswordByEmail(userName).thenApply(storedHash -> {
                // 비밀번호 비교
                if (checkPassword(userPassword, storedHash)) {
                    return "login success"; // 이메일을 사용할 수 있음
                } else {
                    return "잘못된 비밀번호입니다.";
                }
            });
        });
    }
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private boolean checkPassword(String inputPassword, String storedHash) {
        return passwordEncoder.matches(inputPassword, storedHash);
    }
}
