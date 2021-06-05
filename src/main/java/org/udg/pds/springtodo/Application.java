package org.udg.pds.springtodo;

import org.springframework.boot.SpringApplication;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        initializeFirebaseAdmin();
    }
    private static void initializeFirebaseAdmin() {
        try {
            FileInputStream serviceAccount =
                new FileInputStream("firebase-privateKey.json"); // Path of private key saved in previous step

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            System.out.println("Firebase key not found! Firebase Admin functionality will not work.");
        } catch (IOException e) {
            System.out.println("Error initialising Firebase Admin! Firebase Admin functionality will not work.");
        }
    }
}
