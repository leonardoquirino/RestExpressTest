package br.com.leonardoloures.transactions;

import com.strategicgains.repoexpress.mongodb.AbstractMongodbEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.restexpress.plugin.hyperexpress.Linkable;

import java.math.BigDecimal;
import java.util.List;

@Entity("Account")
public class TransactionEntity extends AbstractMongodbEntity implements Linkable {

	public ObjectId source;
	public ObjectId destination;

	public BigDecimal value;
	public TransactionStatusEnum status;

}
