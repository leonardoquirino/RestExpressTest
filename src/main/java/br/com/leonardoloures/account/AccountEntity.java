package br.com.leonardoloures.account;

import com.strategicgains.repoexpress.mongodb.AbstractMongodbEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.restexpress.plugin.hyperexpress.Linkable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity("Account")
public class AccountEntity extends AbstractMongodbEntity implements Linkable {

	private String nome;
    private BigDecimal balance;
    private Integer accountType;
    private List<ObjectId> transactions;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public List<ObjectId> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<ObjectId> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(nome, that.nome) &&
                Objects.equals(accountType, that.accountType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, accountType);
    }
}
