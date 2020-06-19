package generator;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

// Abstraktný generátor obsahujúci základné metódy generovania náhodných údajov
public abstract class Generator {

    private static String IBAN_PREFIX = "SK";
    private static int IBAN_NUMBERS_LENGTH = 21;

    public static String getRandomIBAN() {
        StringBuilder sb = new StringBuilder();
        sb.append(IBAN_PREFIX);
        sb.append(getRandomStringOfNDigits(IBAN_NUMBERS_LENGTH));
        return sb.toString();
    }

    public static String getRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    public static String getRandomStringOfNDigits(int n) {
        StringBuilder sb = new StringBuilder();
        IntStream.rangeClosed(0,n - 1).forEach(x->sb.append(getRandomInteger(0,9)));
        return sb.toString();
    }

    public static int getRandomInteger(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        } else if (min == max) {
            return min;
        }
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static double getRandomDouble(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        } else if (min == max) {
            return min;
        }
        return min + (max - min) * new Random().nextDouble();
    }

    public static boolean getRandomBoolean() {
        return getRandomInteger(0, 1) == 1;
    }

    public static boolean getBiasedBoolean(int truePercentageChance) {
        if (truePercentageChance < 0 || truePercentageChance > 100) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return getRandomInteger(0, 100) <= truePercentageChance;
    }

}
