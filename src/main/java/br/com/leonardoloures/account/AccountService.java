package br.com.leonardoloures.account;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.syntaxe.ValidationEngine;
import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;

import java.math.BigDecimal;
import java.util.List;

/**
 * This is the 'service' or 'business logic' layer, where business logic, syntactic and semantic
 * domain validation occurs, along with calls to the persistence layer.
 */
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        super();
        this.accountRepository = accountRepository;
    }

    public AccountEntity create(AccountEntity entity) {
        ValidationEngine.validateAndThrow(entity);
        return accountRepository.create(entity);
    }

    public AccountEntity read(Identifier id) {
        return accountRepository.read(id);
    }

    public AccountEntity update (AccountEntity entity) throws IllegalArgumentException{
        ValidationEngine.validateAndThrow(entity);
        if (entity.getId() == null){
            throw new IllegalArgumentException("Entity does not contain ID");
        }
        return accountRepository.update(entity);
    }

    public AccountEntity subtractBalance (Identifier id, BigDecimal amount) throws UnsupportedOperationException {
        AccountEntity accountEntity = accountRepository.read(id);
        if (accountEntity == null){
            return null;
        }

        if (accountEntity.getBalance().equals(new BigDecimal(0)) ||
                accountEntity.getBalance().compareTo(amount) < 0) {
            throw new UnsupportedOperationException(
                    "The account does not have the balance to complete this operation.");
        }
        accountEntity.setBalance(accountEntity.getBalance().subtract(amount));
        accountEntity = accountRepository.update(accountEntity);
        return accountEntity;
    }

    public AccountEntity addBalance (Identifier id, BigDecimal amount) {
        AccountEntity accountEntity = accountRepository.read(id);
        if (accountEntity == null){
            return accountEntity;
        }

        accountEntity.setBalance(accountEntity.getBalance().add(amount));
        accountEntity = accountRepository.update(accountEntity);
        return accountEntity;
    }

    public void delete(Identifier id) throws UnsupportedOperationException {
        AccountEntity accountEntity = accountRepository.read(id);
        if (accountEntity.getBalance().compareTo(new BigDecimal(0)) > 0) {
            throw new UnsupportedOperationException("The account has values to be withdrawn.");
        }
        accountRepository.delete(accountEntity);
    }

    public List<AccountEntity> readAll(QueryFilter filter, QueryRange range, QueryOrder order) {
        return accountRepository.readAll(filter, range, order);
    }

    public long count(QueryFilter filter) {
        return accountRepository.count(filter);
    }
}
