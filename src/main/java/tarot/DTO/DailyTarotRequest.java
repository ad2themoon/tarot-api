package tarot.DTO;

import lombok.Data;

@Data
public class DailyTarotRequest {

    private String cardName;
    private String keyword;
    private String element;

}