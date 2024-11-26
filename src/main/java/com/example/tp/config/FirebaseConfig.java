package com.example.tp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.url}")
    private String firebaseUrl;
    @Value("${firebase.path}")
    private String firebasePath;

    @PostConstruct
    public void init(){
        try{
            FileInputStream serviceAccount =
//                    new FileInputStream("src/main/resources/serviceAccountKey.json");
                    new FileInputStream(firebasePath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setDatabaseUrl("https://tp-user-b09e5-default-rtdb.firebaseio.com/")
                    .setDatabaseUrl(firebaseUrl)
                    .build();
            FirebaseApp.initializeApp(options);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
