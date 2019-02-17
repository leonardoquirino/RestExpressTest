package br.com.leonardoloures.transactions;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.syntaxe.ValidationEngine;
import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;

import java.util.List;

public class TransactionService {
    private TransactionRepository transactionRepository;

    public TransactionService (TransactionRepository transactionRepository)
    {
        super();
        this.transactionRepository = transactionRepository;
    }

    public TransactionService create(TransactionEntity entity)
    {
        ValidationEngine.validateAndThrow(entity);
        //TODO
        return null;
    }

    public TransactionEntity read(Identifier id)
    {
        return transactionRepository.read(id);
    }

    public void update(TransactionEntity entity)
    {
        ValidationEngine.validateAndThrow(entity);
        //TODO
    }

    public void delete(Identifier id)
    {
        //TODO

    }

    public List<TransactionEntity> readAll(QueryFilter filter, QueryRange range, QueryOrder order)
    {
        //TODO
        return transactionRepository.readAll(filter, range, order);
    }

    public long count(QueryFilter filter)
    {
        //TODO
        return transactionRepository.count(filter);
    }
}
