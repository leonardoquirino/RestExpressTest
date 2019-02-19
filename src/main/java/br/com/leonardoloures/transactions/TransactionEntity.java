package br.com.leonardoloures.transactions;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.mongodb.AbstractMongodbEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.restexpress.plugin.hyperexpress.Linkable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity("Account")
public class TransactionEntity extends AbstractMongodbEntity implements Linkable {

    private Identifier source;
    private Identifier destination;

    private Double value;
    private TransactionStatusEnum status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, value);
    }

    public Identifier getSource() {
        return source;
    }

    public void setSource(Identifier source) {
        this.source = source;
    }

    public Identifier getDestination() {
        return destination;
    }

    public void setDestination(Identifier destination) {
        this.destination = destination;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public TransactionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TransactionStatusEnum status) {
        this.status = status;
    }


}
