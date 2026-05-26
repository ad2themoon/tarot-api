//package tarot.controller;
//
//import com.google.cloud.firestore.Firestore;
//import com.google.firebase.cloud.FirestoreClient;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/stats")
//@CrossOrigin("*")
//public class StatsController {
//
//    @PostMapping("/visit")
//    public Map<String, Object> visit() throws Exception {
//        Firestore db = FirestoreClient.getFirestore();
//
//        db.collection("visits")
//                .add(Map.of(
//                        "createdAt", new Date()
//                ))
//                .get();
//
//        return Map.of("status", "ok");
//    }
//
//    @PostMapping("/reading")
//    public Map<String, Object> reading(@RequestBody ReadingRequest request) throws Exception {
//        Firestore db = FirestoreClient.getFirestore();
//
//        db.collection("readings")
//                .add(Map.of(
//                        "category", request.getCategory(),
//                        "question", request.getQuestion(),
//                        "cards", request.getCards(),
//                        "createdAt", new Date()
//                ))
//                .get();
//
//        return Map.of("status", "ok");
//    }
//
//    public static class ReadingRequest {
//        private String category;
//        private String question;
//        private List<String> cards;
//
//        public String getCategory() {
//            return category;
//        }
//
//        public void setCategory(String category) {
//            this.category = category;
//        }
//
//        public String getQuestion() {
//            return question;
//        }
//
//        public void setQuestion(String question) {
//            this.question = question;
//        }
//
//        public List<String> getCards() {
//            return cards;
//        }
//
//        public void setCards(List<String> cards) {
//            this.cards = cards;
//        }
//    }
//}