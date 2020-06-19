package utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Properties;

// Trieda na načítanie konfigurácie
public class Configuration {

    private static Configuration instance = null;
    Properties properties;

    private Configuration() throws IOException {
        InputStream input = Configuration.class.getClassLoader().getResourceAsStream("config.properties");
        if (input == null) {
            throw new IOException("File config.properties cannot be loaded");
        }
        this.properties = new Properties();
        this.properties.load(input);
    }

    public static Configuration getInstance() throws IOException {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public String getProperty(String key) {
        if (key == null) {
            throw new InvalidParameterException("Key cannot be null");
        }
        return properties.getProperty(key);
    }

}
