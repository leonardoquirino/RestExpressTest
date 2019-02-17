package br.com.leonardoloures.account;

import com.strategicgains.repoexpress.mongodb.AbstractMongodbEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.restexpress.plugin.hyperexpress.Linkable;

import java.math.BigDecimal;
import java.util.List;

@Entity("Account")
public class AccountEntity extends AbstractMongodbEntity implements Linkable {

	public String nome;
	public BigDecimal balance;
	public Integer accountType;
	public List<ObjectId> transactions;
}
