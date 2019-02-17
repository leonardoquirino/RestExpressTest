package br.com.leonardoloures.account;

import com.strategicgains.repoexpress.mongodb.MongoConfig;
import com.strategicgains.repoexpress.mongodb.MongodbEntityRepository;

public class AccountRepository extends MongodbEntityRepository<AccountEntity> {

	private MongoConfig mongoConfig;

	@SuppressWarnings("unchecked")
	public AccountRepository(MongoConfig mongoConfig) {
		super(mongoConfig.getClient(), mongoConfig.getDbName(), AccountEntity.class);
		this.mongoConfig = mongoConfig;
	}

}
