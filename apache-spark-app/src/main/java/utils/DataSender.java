package utils;

import entities.*;
import generator.ClientGenerator;
import generator.Generator;
import generator.TransactionGenerator;
import kafka.KafkaProducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

// Trieda na zasielanie vygenerovaných údajov do Apache Kafka
public class DataSender {

    private static int TOTAL_CLIENTS;
    private static int BLACKLISTED_CLIENTS;

    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration config = Configuration.getInstance();
        TOTAL_CLIENTS = Integer.parseInt(config.getProperty("data-sender.total-clients"));
        BLACKLISTED_CLIENTS = Integer.parseInt(config.getProperty("data-sender.blacklisted-clients"));

        KafkaProducer kafkaProducer = new KafkaProducer(config.getProperty("kafka.endpoint"), config.getProperty("kafka.data-sender-id"));
        // Vygenerovanie klientov
        List<Client> allClients = generateClients();
        Map<Account, String> accountsForBlacklist = sortAccountsForBlacklist(allClients);
        // Zaslanie klientských údajov do Apache Kafka
        sendAllClientInfoToKafka(kafkaProducer, allClients);
        // Zaslanie údajov do blacklist-u
        sendBlacklistedIbansToKafka(kafkaProducer, accountsForBlacklist, config.getProperty("kafka.blacklist-topic"));
        // Generovanie transakcií
        generateTransactions(kafkaProducer, allClients, config.getProperty("kafka.tx-transfer-topic"), config.getProperty("kafka.tx-card-topic"));
    }

    // Metóda na generovanie klientov
    private static List<Client> generateClients() {
        List<Client> allClients = new ArrayList<>();
        IntStream.range(0, TOTAL_CLIENTS).forEach(i -> {
            allClients.add(ClientGenerator.generateClient());
        });
        return allClients;
    }

    // Voľba náhodných klientov, ktorých účty budú blacklistované
    private static Map<Account, String> sortAccountsForBlacklist(List<Client> allClients) {
        Map<Account, String> accountsForBlacklist = new HashMap<>();
        IntStream.range(0, BLACKLISTED_CLIENTS).forEach(i -> {
            allClients.get(i).getAccounts().forEach(acc -> {
                accountsForBlacklist.put(acc, "Y");
            });
        });
        IntStream.range(BLACKLISTED_CLIENTS, allClients.size()).forEach(i -> {
            allClients.get(i).getAccounts().forEach(acc -> {
                accountsForBlacklist.put(acc, "N");
            });
        });
        return accountsForBlacklist;
    }

    // Metóda, na zaslanie klientských údajov do Apache Kafka
    private static void sendAllClientInfoToKafka(KafkaProducer kafkaProducer, List<Client> allClients) throws IOException {
        Configuration config = Configuration.getInstance();
        allClients.forEach(client -> {
            // Karty
            sendCardInfoToKafka(kafkaProducer, client, config.getProperty("kafka.card-topic"));
            // Účty
            sendAccountInfoToKafka(kafkaProducer, client, config.getProperty("kafka.account-topic"));
            // Klient
            kafkaProducer.produce(client.getId(), client.toString(), config.getProperty("kafka.client-topic"));
            System.out.println(String.format("Client %s send", client.getId()));
        });
    }

    // Zaslanie kariet do Apache Kafka
    private static void sendCardInfoToKafka(KafkaProducer kafkaProducer, Client client, String cardTopic) {
        client.getCards().forEach(card -> {
            System.out.println("CARD BELONGS TO: " + card.getIban());
            kafkaProducer.produce(card.getNumber(), String.format("%s,%s", card.toString(), card.getIban()), cardTopic);
            System.out.println(String.format("Card %s send", card.getNumber()));
        });
    }

    // Zaslanie účtov do Apache Kafka
    private static void sendAccountInfoToKafka(KafkaProducer kafkaProducer, Client client, String accountTopic) {
        client.getAccounts().forEach(account -> {
            kafkaProducer.produce(account.getIban(), String.format("%s,%s", account.toString(), client.getId()), accountTopic);
            System.out.println(String.format("Account %s send", account.getIban()));
        });
    }

    // Zaslanie čiernej listiny do Apache Kafka
    private static void sendBlacklistedIbansToKafka(KafkaProducer kafkaProducer, Map<Account, String> accountsForBlacklist, String blacklistTopic) {
        accountsForBlacklist.forEach((account, flag) -> {
            kafkaProducer.produce(
                    account.getIban(),
                    String.format("%s,%s", account.getIban(), flag),
                    blacklistTopic
            );
            System.out.println(String.format("IBAN %s send to blacklist with flag %s", account.getIban(), flag));
        });
    }

    // Periodické generovanie transakcií
    private static void generateTransactions(KafkaProducer kafkaProducer, List<Client> allClients, String transferTransactionTopic, String cardTransactionTopic) throws InterruptedException {
        while (true) {
            Account debtorAcc, creditorAcc;
            Client debtor, creditor;
            do {
                debtor = allClients.get(Generator.getRandomInteger(0, allClients.size() - 1));
                creditor = allClients.get(Generator.getRandomInteger(0, allClients.size() - 1));
                debtorAcc = debtor.getAccounts().get(Generator.getRandomInteger(0, debtor.getAccounts().size() - 1));
                creditorAcc = creditor.getAccounts().get(Generator.getRandomInteger(0, creditor.getAccounts().size() - 1));
            } while (debtorAcc.equals(creditorAcc));
            TransactionSentenceForTransfer tr = TransactionGenerator.generateTransactionSentenceForTransfer(creditorAcc.getIban(), debtorAcc.getIban());
            kafkaProducer.produce(tr.getId(), tr.toString(), transferTransactionTopic);

            String randomClientCardNumber = debtor.getCards().get(Generator.getRandomInteger(0, debtor.getCards().size() - 1)).getNumber();
            TransactionSentenceForCard tsfc = TransactionGenerator.generateTransactionSentenceForCard(randomClientCardNumber, debtor.getId());
            kafkaProducer.produce(tsfc.toString(), cardTransactionTopic);

            System.out.println(String.format("Transaction send %s", tr.getId()));
            System.out.println(String.format("Transaction send %s", tsfc.getId()));
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
