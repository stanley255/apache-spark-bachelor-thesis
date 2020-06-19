package generator;

import entities.Account;
import entities.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

// Generátor klientských účtov
public class AccountGenerator extends Generator {

    private static List<String> TYPES = Arrays.asList("K", "D", "K", "T"); // klasický, debentný, kartový, terminovaný
    private static Double MIN_BALANCE = 50.0;
    private static Double MAX_BALANCE = 4000.0;
    private static int CARD_MINIMUM = 1;
    private static int CARD_MAXIMUM = 3;

    public static Account generateAccount() {
        Account account = new Account();
        account.setIban(getRandomIBAN());
        account.setType(TYPES.get(getRandomInteger(0,TYPES.size() - 1)));
        account.setBalance(getRandomDouble(MIN_BALANCE, MAX_BALANCE));
        account.setCards(new ArrayList<>());
        // Generovanie kariet pre účet
        IntStream.range(0, getRandomInteger(CARD_MINIMUM, CARD_MAXIMUM)).forEach(i -> {
            Card card = CardGenerator.generateCard();
            card.setIban(account.getIban());
            account.getCards().add(card);
        });
        return account;
    }

}
