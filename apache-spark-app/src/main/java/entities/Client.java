package entities;

import java.util.List;

public class Client {

    private String id;
    private String name;
    private String surname;
    private Integer age;
    private String gender;
    private boolean approvedOverdraft;
    private double limit;
    private double score;
    private List<Account> accounts;
    private List<Card> cards;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isApprovedOverdraft() {
        return approvedOverdraft;
    }

    public void setApprovedOverdraft(boolean approvedOverdraft) {
        this.approvedOverdraft = approvedOverdraft;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String toString() {
        return id + ',' + name + ',' + surname + ',' + age + ',' + gender + ',' + (approvedOverdraft ? 'Y' : 'N') + ',' + limit;
    }
}
