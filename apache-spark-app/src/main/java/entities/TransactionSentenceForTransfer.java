package entities;

import java.time.format.DateTimeFormatter;

public class TransactionSentenceForTransfer extends TransactionSentence {

    private String creditorIBAN;
    private String debtorIBAN;

    public TransactionSentenceForTransfer() {}

    public String getCreditorIBAN() {
        return creditorIBAN;
    }

    public void setCreditorIBAN(String creditorIBAN) {
        this.creditorIBAN = creditorIBAN;
    }

    public String getDebtorIBAN() {
        return debtorIBAN;
    }

    public void setDebtorIBAN(String debtorIBAN) {
        this.debtorIBAN = debtorIBAN;
    }

    @Override
    public String toString() {
        return getId() +
                "," + getTransactionDate().format(DateTimeFormatter.ofPattern(dateFormat)) +
                "," + getPostingDate().format(DateTimeFormatter.ofPattern(dateFormat)) +
                "," + getAmount() +
                "," + getCurrency() +
                "," + getType() +
                "," + getVariableCode() +
                "," + getSpecificCode() +
                "," + getConstantCode() +
                "," + creditorIBAN +
                "," + debtorIBAN;
    }

}
