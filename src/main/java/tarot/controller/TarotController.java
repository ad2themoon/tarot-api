package tarot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tarot.DTO.DailyTarotRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarot")
@CrossOrigin("*")
public class TarotController {
    private static final String OPEN_ROUTER_URL =
            "https://openrouter.ai/api/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${openrouter.api-key}")
    private String apiKey;
    @Value("${openrouter.model}")
    private String model;

    @PostConstruct
    public void init() {
        String key = safeKey();

        System.out.println("================================");
        System.out.println("TAROT AI START");
        System.out.println("KEY LOADED : " + key.startsWith("sk-or-v1-"));
        System.out.println("KEY PREFIX : " + key.substring(0, Math.min(10, key.length())));
        System.out.println("MODEL : " + model);
        System.out.println("================================");
    }

    @GetMapping("/test")
    public String test() {
        return "Tarot API OK 🔮";
    }

    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestBody TarotRequest request) {
        try {
            validateRequest(request);

            String prompt = buildPrompt(request);
            String responseBody = callOpenRouter(prompt);
            Map<String, String> aiResponse =
                    extractJsonContent(responseBody);

            return ResponseEntity.ok(aiResponse);


        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String callOpenRouter(String prompt) throws Exception {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0.8,
                "max_tokens", 1000
        );

        String jsonBody = objectMapper.writeValueAsString(body);

        HttpEntity<String> entity = new HttpEntity<>(
                jsonBody,
                createHeaders()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                OPEN_ROUTER_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(safeKey());

        headers.set("HTTP-Referer", "http://localhost:4200");
        headers.set("X-Title", "Tarot AI");
        headers.set("User-Agent", "Mozilla/5.0");

        return headers;
    }

    private String extractContent(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode contentNode = root
                .path("choices")
                .path(0)
                .path("message")
                .path("content");

        if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
            throw new RuntimeException("ไม่พบ content จาก AI: " + responseBody);
        }

        return contentNode.asText();
    }

    private String buildPrompt(TarotRequest request) {

        return """
        คุณคือหมอดูไพ่ยิปซีสไตล์วัยรุ่น
        
        วิเคราะห์ไพ่ตามความหมายจริงของไพ่
        ตอบตรงไปตรงมา
        ไม่โลกสวยเกินจริง
        
        ถ้าไพ่ไม่ดี สามารถตอบเชิงลบได้
        
        ========================
        
        หมวดคำถาม: %s
        
        คำถาม:
        %s
        
        ไพ่ที่เปิดได้:
        
        1. %s
        หมายถึงอดีต
        
        2. %s
        หมายถึงปัจจุบัน
        
        3. %s
        หมายถึงอนาคต
        
        ========================
        
        กรุณาตอบเป็น JSON เท่านั้น
        
        {
          "summary": "สรุปสั้น ๆ ฟีลวัยรุ่น แชร์ลง IG Story ได้ ไม่เกิน 50 ตัวอักษร",
          "result": "คำทำนายเต็ม"
        }
        
        summary ต้อง:
        
        - ภาษาวัยรุ่น
        - อ่านแล้วรู้สึก relatable
        - มี emoji ได้
        - ไม่เกิน 50 ตัวอักษร
        
        ตัวอย่าง:
        
        "เขาก็มีใจนะ แต่ยังงง ๆ อยู่ 🫠"
        "ช่วงนี้รักมันหน่วงอะดิ 🌙"
        "ระวังคิดมากเกินไปนะ 🥹"
        "งานกำลังจะดีขึ้นแล้ว ✨"
        
        result ให้ตอบแบบ:
        
        🔮 ภาพรวม
        ...
        
        🃏 ไพ่แต่ละใบ
        
        1. ...
        
        2. ...
        
        3. ...
        
        ✨ คำแนะนำ
        ...
        
        🌙 สรุป
        ...
        
        ความยาวประมาณ 250-400 คำ
        """
                .formatted(
                        request.getCategory(),
                        request.getQuestion(),
                        request.getCards().get(0),
                        request.getCards().get(1),
                        request.getCards().get(2)
                );
    }

    private void validateRequest(TarotRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request ห้ามว่าง");
        }

        if (isBlank(request.getCategory())) {
            throw new IllegalArgumentException("category ห้ามว่าง");
        }

        if (isBlank(request.getQuestion())) {
            throw new IllegalArgumentException("question ห้ามว่าง");
        }

        if (request.getCards() == null || request.getCards().size() != 3) {
            throw new IllegalArgumentException("ต้องเลือกไพ่ 3 ใบ");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safeKey() {
        return apiKey == null ? "" : apiKey.trim();
    }

    public static class TarotRequest {
        private String category;
        private String question;
        private List<String> cards;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getCards() {
            return cards;
        }

        public void setCards(List<String> cards) {
            this.cards = cards;
        }
    }
    @PostMapping("/daily")
    public ResponseEntity<?> daily(@RequestBody DailyTarotRequest request) {
        try {
            String prompt = buildDailyPrompt(request);
            String responseBody = callOpenRouter(prompt);
            String result = extractContent(responseBody);

            return ResponseEntity.ok(
                    Map.of("result", result)
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String buildDailyPrompt(DailyTarotRequest request) {
        return """
            คุณคือหมอดูไพ่ยิปซี
            
            ช่วยทำนายไพ่ประจำวันแบบเป็นกลาง
            ไม่โลกสวยเกินไป และไม่ฟันธงเกินจริง
            
            ไพ่ประจำวัน:
            %s
            
            Keyword:
            %s
            
            Element:
            %s
            
            กรุณาตอบภาษาไทยเท่านั้น
            
            รูปแบบคำตอบ:
            
            🔮 พลังงานวันนี้
            ...
            
            ⚠️ สิ่งที่ควรระวัง
            ...
            
            ✨ คำแนะนำ
            ...
            
            🌙 สรุปสั้น ๆ
            ...
            
            ความยาวประมาณ 50-70 คำ
            """
                .formatted(
                        request.getCardName(),
                        request.getKeyword(),
                        request.getElement()
                );
    }

    private Map<String, String> extractJsonContent(
            String responseBody
    ) throws Exception {

        String content = extractContent(responseBody);

        JsonNode jsonNode =
                objectMapper.readTree(content);

        return Map.of(
                "summary",
                jsonNode.path("summary").asText(),
                "result",
                jsonNode.path("result").asText()
        );
    }

}