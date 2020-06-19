package entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Card {

    private String number;
    private LocalDateTime expiration;
    private String cvv;
    private String issuer;
    private String type; // debet/kredit
    private String iban;
    public static final String expirationFormat = "MM/yy";

    public String getExpirationString() {
        return expiration.format(DateTimeFormatter.ofPattern(expirationFormat));
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    @Override
    public String toString() {
        return number + ',' + getExpirationString() + ',' + cvv + ',' + issuer + ',' + type;
    }
}
