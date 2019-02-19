package br.com.leonardoloures.account;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.mongodb.AbstractMongodbEntity;
import org.mongodb.morphia.annotations.Entity;
import org.restexpress.plugin.hyperexpress.Linkable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity("Account")
public class AccountEntity extends AbstractMongodbEntity implements Linkable {

    private String nome;
    /*
     * Changing account balance from BigDecimal to Double because the mongodb (repoexpress) dependency uses an old
     * Morphia version that saves Double correctly on Mongo, but cannot read it back. This will work as a test,
     * but for production a Double is not a good representative because of lost precision.
     */
    private Double balance = 0d;
    private Integer accountType;
    private List<Identifier> transactions;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getBalance() {
        return balance;
    }

    public void addBalance(Double balance) {
        this.balance += balance;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public List<Identifier> getTransactions() {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        return transactions;
    }

    public void setTransactions(List<Identifier> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(nome, that.nome) &&
                Objects.equals(accountType, that.accountType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, accountType);
    }
}
