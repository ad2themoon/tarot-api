//package tarot.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.ByteArrayInputStream;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class FirebaseConfig {
//
//    @Value("${firebase.service-account-json}")
//    private String serviceAccountJson;
//
//    @PostConstruct
//    public void init() {
//        try {
//            if (!FirebaseApp.getApps().isEmpty()) {
//                return;
//            }
//
//            GoogleCredentials credentials = GoogleCredentials.fromStream(
//                    new ByteArrayInputStream(
//                            serviceAccountJson.getBytes(StandardCharsets.UTF_8)
//                    )
//            );
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(credentials)
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//
//            System.out.println("Firebase connected");
//
//        } catch (Exception e) {
//            throw new RuntimeException("Firebase init failed", e);
//        }
//    }
//}