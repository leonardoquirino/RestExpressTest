package br.com.leonardoloures.transactions;

import com.strategicgains.repoexpress.mongodb.MongoConfig;
import com.strategicgains.repoexpress.mongodb.MongodbEntityRepository;

public class TransactionRepository extends MongodbEntityRepository<TransactionEntity> {

    private MongoConfig mongoConfig;

    @SuppressWarnings("unchecked")
    public TransactionRepository(MongoConfig mongoConfig) {
        super(mongoConfig.getClient(), mongoConfig.getDbName(), TransactionEntity.class);
        this.mongoConfig = mongoConfig;
    }

}
