package generator;

import utils.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

// Generátor mužských a ženských mien a priezvisk
public class NameGenerator extends Generator {

    private static List<String> MALE_NAMES;
    private static List<String> MALE_SURNAMES;
    private static List<String> FEMALE_NAMES;
    private static List<String> FEMALE_SURNAMES;

    static {
        try {
            Configuration conf = Configuration.getInstance();
            MALE_NAMES = readFileInList(conf.getProperty("generator-file.male-names"));
            MALE_SURNAMES = readFileInList(conf.getProperty("generator-file.male-surnames"));
            FEMALE_NAMES = readFileInList(conf.getProperty("generator-file.female-names"));
            FEMALE_SURNAMES = readFileInList(conf.getProperty("generator-file.female-surnames"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFileInList(String fileName) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static String getRandomMaleName() {
        return MALE_NAMES.get(getRandomInteger(0, MALE_NAMES.size() - 1));
    }

    public static String getRandomFemaleName() {
        return FEMALE_NAMES.get(getRandomInteger(0, FEMALE_NAMES.size() - 1));
    }

    public static String getRandomMaleSurname() {
        return MALE_SURNAMES.get(getRandomInteger(0, MALE_SURNAMES.size() - 1));
    }

    public static String getRandomFemaleSurname() {
        return FEMALE_SURNAMES.get(getRandomInteger(0, FEMALE_SURNAMES.size() - 1));
    }

}
