package br.com.leonardoloures.account;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.syntaxe.ValidationEngine;
import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;

import java.util.List;

/**
 * This is the 'service' or 'business logic' layer, where business logic, syntactic and semantic
 * domain validation occurs, along with calls to the persistence layer.
 */
public class AccountService
{
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository)
    {
        super();
        this.accountRepository = accountRepository;
    }

    public AccountEntity create(AccountEntity entity)
    {
        ValidationEngine.validateAndThrow(entity);
        //TODO
        return null;
    }

    public AccountEntity read(Identifier id)
    {
        return accountRepository.read(id);
    }

    public void update(AccountEntity entity)
    {
        ValidationEngine.validateAndThrow(entity);
        //TODO
    }

    public void delete(Identifier id)
    {
        //TODO

    }

    public List<AccountEntity> readAll(QueryFilter filter, QueryRange range, QueryOrder order)
    {
        //TODO
        return accountRepository.readAll(filter, range, order);
    }

    public long count(QueryFilter filter)
    {
        //TODO
        return accountRepository.count(filter);
    }
}
