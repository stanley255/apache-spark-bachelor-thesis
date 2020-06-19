package generator;

import entities.Card;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Generátor kariet
public class CardGenerator extends Generator {

    private static int CARD_NUMBER_LENGTH = 16;
    private static List<String> ISSUERS = Arrays.asList("V", "M"); // Visa alebo MasterCard
    private static List<String> TYPES = Arrays.asList("D", "K"); // debetná alebo kreditná
    private static Map<String, Integer> ISSUER_START_DIGIT = new HashMap<String, Integer>() {{
        put("V", 4);
        put("M", 5);
    }};
    private static int CCV_LENGTH = 3;

    protected static LocalDateTime getRandomExpirationDateTime() {
        return LocalDateTime.of(getRandomInteger(2021,2024),
                Month.of(getRandomInteger(1,12)),
                getRandomInteger(1,27), getRandomInteger(1,23),
                getRandomInteger(1,59));
    }

    public static Card generateCard() {
        Card card = new Card();
        card.setCvv(getRandomStringOfNDigits(CCV_LENGTH));
        card.setExpiration(getRandomExpirationDateTime());
        card.setIssuer(ISSUERS.get(getRandomInteger(0,ISSUERS.size() - 1)));
        card.setType(TYPES.get(getRandomInteger(0,TYPES.size() - 1)));
        String randomCardNumberAsString = ISSUER_START_DIGIT.get(card.getIssuer()) + getRandomStringOfNDigits(CARD_NUMBER_LENGTH - 1);
        card.setNumber(randomCardNumberAsString);
        return card;
    }

}
