package generator;

import entities.TransactionSentence;
import entities.TransactionSentenceForCard;
import entities.TransactionSentenceForTransfer;

import java.time.LocalDateTime;
import java.util.*;

// Generátor transakcií
public class TransactionGenerator extends Generator {

    private static int VARIABLE_CODE_LENGTH = 10;
    private static int SPECIFIC_CODE_LENGTH = 10;
    private static int CONSTANT_CODE_LENGTH = 4;
    private static double MIN_AMOUNT = 10;
    private static double MAX_AMOUNT = 2000;
    private static int MIN_POST_HOUR_ADDITION = 0;
    private static int MAX_POST_HOUR_ADDITION = 48;
    private static String EU_CURRENCY_CODE = "EUR";
    private static int ERROR_CHANCE = 40;
    private static int MERCHANT_CODE_LENGTH = 4;
    private static List<String> TYPES = Arrays.asList("D", "K"); // debetná alebo kreditná
    private static List<String> OPERATIONS = Arrays.asList("A", "Z", "V", "W", "P"); // autorizácia, zúčtovanie, vklad, výber, prevod
    private static Map<String, List<String>> OPERATION_ERROR_MAPPING = new HashMap<String, List<String>>() {{
        put("A", Arrays.asList("WRONG_PIN"));
        put("W", Arrays.asList("INSUFFICIENT_RESOURCES"));
        put("P", Arrays.asList("LIMIT_OVERDRAFT", "INSUFFICIENT_RESOURCES"));
    }};

    // Metóda na generovanie transakcií medzi bankovými účtami
    public static TransactionSentenceForTransfer generateTransactionSentenceForTransfer(String creditorIBAN, String debtorIBAN) {
        TransactionSentenceForTransfer tsft = new TransactionSentenceForTransfer();
        setBasicTransactionParameters(tsft);
        tsft.setCreditorIBAN(creditorIBAN);
        tsft.setDebtorIBAN(debtorIBAN);
        return tsft;
    }

    // Metóda na generovanie kartových transakcií
    public static TransactionSentenceForCard generateTransactionSentenceForCard(String cardNumber, String clientId) {
        TransactionSentenceForCard tsfc = new TransactionSentenceForCard();
        setBasicTransactionParameters(tsfc);
        tsfc.setCardNumber(cardNumber);
        tsfc.setOperationCode(OPERATIONS.get(getRandomInteger(0, OPERATIONS.size() - 1)));
        tsfc.setErrorCode("");
        if ((OPERATION_ERROR_MAPPING.containsKey(tsfc.getOperationCode())) && (getBiasedBoolean(ERROR_CHANCE))) {
                List<String> potentialErrorCodes = OPERATION_ERROR_MAPPING.get(tsfc.getOperationCode());
                String errorCode = potentialErrorCodes.get(getRandomInteger(0, potentialErrorCodes.size() - 1));
                if (isCorrectErrorCombination(tsfc.getType(), tsfc.getOperationCode()))
                    tsfc.setErrorCode(errorCode);
        }
        tsfc.setMerchantCode(getRandomStringOfNDigits(MERCHANT_CODE_LENGTH));
        tsfc.setClientId(clientId);
        return tsfc;
    }

    // Metóda na nastavenie základných parametrov pre transakciu
    private static void setBasicTransactionParameters(TransactionSentence ts) {
        ts.setId(getRandomUUIDString());
        ts.setTransactionDate(LocalDateTime.now());
        if (getRandomBoolean()) {
            ts.setPostingDate(LocalDateTime.now());
        } else {
            ts.setPostingDate(LocalDateTime.now().plusHours(getRandomInteger(MIN_POST_HOUR_ADDITION, MAX_POST_HOUR_ADDITION)));
        }
        ts.setAmount(getRandomDouble(MIN_AMOUNT, MAX_AMOUNT));
        ts.setCurrency(EU_CURRENCY_CODE);
        ts.setType(TYPES.get(getRandomInteger(0,TYPES.size() - 1)));
        ts.setVariableCode(getRandomStringOfNDigits(VARIABLE_CODE_LENGTH));
        ts.setSpecificCode(getRandomStringOfNDigits(SPECIFIC_CODE_LENGTH));
        ts.setConstantCode(getRandomStringOfNDigits(CONSTANT_CODE_LENGTH));
    }

    // Kontrolovanie korektných kombinácií chybových kódov
    private static boolean isCorrectErrorCombination(String type, String operation) {
        return !type.equals("K") || (!operation.equals("W") && !operation.equals("P"));
    }

}
