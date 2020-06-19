package entities;

import java.time.LocalDateTime;

public abstract class TransactionSentence {

    private String id;
    private LocalDateTime transactionDate;
    private LocalDateTime postingDate; // dátum zaúčtovania
    private double amount;
    private String currency;
    private String type; // debet/kredit
    private String variableCode;
    private String specificCode;
    private String constantCode;
    public static final String dateFormat = "yyyy-MM-dd HH:mm";

    public TransactionSentence() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDateTime getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDateTime postingDate) {
        this.postingDate = postingDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVariableCode() {
        return variableCode;
    }

    public void setVariableCode(String variableCode) {
        this.variableCode = variableCode;
    }

    public String getSpecificCode() {
        return specificCode;
    }

    public void setSpecificCode(String specificCode) {
        this.specificCode = specificCode;
    }

    public String getConstantCode() {
        return constantCode;
    }

    public void setConstantCode(String constantCode) {
        this.constantCode = constantCode;
    }

    @Override
    public String toString() {
        return "TransactionSentence{" +
                "id='" + id + '\'' +
                ", transactionDate=" + transactionDate +
                ", postingDate=" + postingDate +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", type=" + type +
                ", variableCode='" + variableCode + '\'' +
                ", specificCode='" + specificCode + '\'' +
                ", constantCode='" + constantCode + '\'' +
                '}';
    }
}
