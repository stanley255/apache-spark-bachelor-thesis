package entities;

import java.time.format.DateTimeFormatter;

public class TransactionSentenceForCard extends TransactionSentence {

    private String cardNumber;
    private String operationCode; // autorizácia, zúčtovanie, vklad, výber, platba
    private String errorCode; // zlý pin, nedostatok prostriedkov
    private String merchantCode;
    private String clientId;
    private String topic;
    private String businessRuleMessage;

    public TransactionSentenceForCard() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBusinessRuleMessage() {
        return businessRuleMessage;
    }

    public void setBusinessRuleMessage(String specialCaseMessage) {
        this.businessRuleMessage = specialCaseMessage;
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
                "," + getCardNumber() +
                "," + getOperationCode() +
                "," + getErrorCode() +
                "," + getMerchantCode() +
                "," + getClientId();
    }
}
