package br.com.leonardoloures.transactions;

import br.com.leonardoloures.account.AccountEntity;
import br.com.leonardoloures.account.AccountService;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.syntaxe.ValidationEngine;
import org.restexpress.common.query.FilterOperator;
import org.restexpress.common.query.QueryFilter;
import org.restexpress.common.query.QueryOrder;
import org.restexpress.common.query.QueryRange;

import java.util.List;

public class TransactionService {

    private TransactionRepository transactionRepository;
    private AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        super();
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public TransactionEntity create(TransactionEntity entity) {
        ValidationEngine.validateAndThrow(entity);
        entity.setStatus(TransactionStatusEnum.INITIAL);
        return transactionRepository.create(entity);
    }

    public TransactionEntity read(Identifier id) {
        return transactionRepository.read(id);
    }

    public TransactionEntity update(TransactionEntity entity) {
        ValidationEngine.validateAndThrow(entity);
        return transactionRepository.update(entity);
    }

    public TransactionEntity transfer(TransactionEntity entity) throws IllegalArgumentException {
        if (!TransactionStatusEnum.INITIAL.equals(entity.getStatus())) {
            throw new IllegalArgumentException("Transaction is not on initial status");
        }

        entity.setStatus(TransactionStatusEnum.PENDING);
        entity = this.update(entity);

        AccountEntity source = accountService.read(entity.getSource());
        AccountEntity destination = accountService.read(entity.getDestination());

        if (source.getBalance() <= 0.0d) {
            entity.setStatus(TransactionStatusEnum.FAILED);
            return this.update(entity);
        }

        source.getTransactions().add(entity.getId());
        source.addBalance(-entity.getValue());
        source = accountService.update(source);

        destination.addBalance(entity.getValue());
        destination.getTransactions().add(entity.getId());
        destination = accountService.update(destination);

        entity.setStatus(TransactionStatusEnum.APPLIED);
        entity = transactionRepository.update(entity);

        source.getTransactions().remove(entity.getId());
        destination.getTransactions().remove(entity.getId());
        accountService.update(source);
        accountService.update(destination);

        entity.setStatus(TransactionStatusEnum.DONE);
        return transactionRepository.update(entity);
    }

    public TransactionEntity readLastTransactionByStatus(TransactionStatusEnum status) {
        QueryFilter filter = new QueryFilter();
        filter.addCriteria("status", FilterOperator.EQUALS, status);
        QueryOrder order = new QueryOrder();
        order.addSort("updatedAt");

        List<TransactionEntity> transactionEntityList = transactionRepository.readAll(filter, new QueryRange(0L, 1), order);
        if (transactionEntityList == null || transactionEntityList.size() == 0) {
            return null;
        }
        return transactionEntityList.get(0);
    }

    public TransactionEntity recoverPending(TransactionEntity transaction) {
        AccountEntity source = accountService.read(transaction.getSource());
        if (source.getTransactions().size() == 0 ||
                !source.getTransactions().contains(transaction.getId())) {
            source = accountService.subtractBalance(transaction.getSource(), transaction.getValue(), transaction.getId());
        }

        AccountEntity destination = accountService.read(transaction.getDestination());
        if (destination.getTransactions().size() == 0 ||
                !destination.getTransactions().contains(transaction.getId())) {
            destination = accountService.addBalance(transaction.getDestination(), transaction.getValue(), transaction.getId());
        }

        transaction.setStatus(TransactionStatusEnum.APPLIED);
        transaction = transactionRepository.update(transaction);

        source.getTransactions().remove(transaction.getId());
        accountService.update(source);

        destination.getTransactions().remove(transaction.getId());
        accountService.update(destination);

        transaction.setStatus(TransactionStatusEnum.DONE);
        return transactionRepository.update(transaction);
    }

    public TransactionEntity recoverApplied(TransactionEntity transaction) {
        AccountEntity source = accountService.read(transaction.getSource());
        source.getTransactions().remove(transaction.getId());
        accountService.update(source);

        AccountEntity destination = accountService.read(transaction.getDestination());
        destination.getTransactions().remove(transaction.getId());
        accountService.update(destination);

        transaction.setStatus(TransactionStatusEnum.DONE);
        return transactionRepository.update(transaction);
    }

    public List<TransactionEntity> readAll(QueryFilter filter, QueryRange range, QueryOrder order) {
        return transactionRepository.readAll(filter, range, order);
    }

    public long count(QueryFilter filter) {
        return transactionRepository.count(filter);
    }
}
