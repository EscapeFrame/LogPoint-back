package com.example.tp.repository;

import com.example.tp.model.User;
import com.google.firebase.database.*;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
public class UserRepository {


    private final DatabaseReference databaseReference;
    public UserRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveUser(User user, String uid) {
        // Firebase Database에 사용자 정보 저장
        databaseReference.child(uid).setValueAsync(user);
    }

    public CompletableFuture<Boolean> userEameExist(String username) {
        CompletableFuture<Boolean> existsFuture = new CompletableFuture<>();

        databaseReference.orderByChild("userName").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                existsFuture.complete(exists); // 존재 여부를 CompletableFuture에 전달
                if (exists) {
                    System.out.println("Username exists: " + username);
                } else {
                    System.out.println("No matching username found: " + username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
                existsFuture.completeExceptionally(databaseError.toException());
            }
        });

        return existsFuture; // CompletableFuture 반환
    }
    public CompletableFuture<String> getPasswordByEmail(String userName) {
        CompletableFuture<String> passwordFuture = new CompletableFuture<>();

        databaseReference.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    String password = userSnapshot.child("userPassword").getValue(String.class);
                    passwordFuture.complete(password);
                } else {
                    passwordFuture.complete(null); // 사용자 없음
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                passwordFuture.completeExceptionally(databaseError.toException());
            }
        });

        return passwordFuture; // 비밀번호를 CompletableFuture로 반환
    }

}
