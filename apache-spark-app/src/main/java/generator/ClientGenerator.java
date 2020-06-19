package generator;

import entities.Account;
import entities.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

// Generátor klientov
public class ClientGenerator extends Generator {

    private static List<String> GENDERS = Arrays.asList("M", "F"); // muž alebo žena
    private static double LIMIT_MINIMUM = 100;
    private static double LIMIT_MAXIMUM = 1500;
    private static int ACCOUNT_MINIMUM = 1;
    private static int ACCOUNT_MAXIMUM = 3;

    public static Client generateClient() {
        Client client = new Client();
        client.setId(getRandomUUIDString());
        client.setAge(getRandomInteger(18, 80));
        generateGenderAndNames(client);
        client.setApprovedOverdraft(getBiasedBoolean(20));
        client.setLimit(getRandomDouble(LIMIT_MINIMUM, LIMIT_MAXIMUM));
        client.setAccounts(new ArrayList<>());
        client.setCards(new ArrayList<>());
        IntStream.range(0, getRandomInteger(ACCOUNT_MINIMUM, ACCOUNT_MAXIMUM)).forEach( i -> {
            Account account = AccountGenerator.generateAccount();
            account.getCards().forEach(card -> {
                client.getCards().add(card);
            });
            client.getAccounts().add(account);
        });
        return client;
    }

    private static void generateGenderAndNames(Client client) {
        if (getRandomBoolean()) {
            generateMale(client);
        } else {
            generateFemale(client);
        }
    }

    private static void generateMale(Client client) {
        client.setGender(GENDERS.get(0));
        client.setName(NameGenerator.getRandomMaleName());
        client.setSurname(NameGenerator.getRandomMaleSurname());
    }

    private static void generateFemale(Client client) {
        client.setGender(GENDERS.get(1));
        client.setName(NameGenerator.getRandomFemaleName());
        client.setSurname(NameGenerator.getRandomFemaleSurname());
    }

}
