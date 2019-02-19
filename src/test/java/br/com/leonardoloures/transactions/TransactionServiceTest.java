package br.com.leonardoloures.transactions;

import br.com.leonardoloures.account.AccountEntity;
import br.com.leonardoloures.account.AccountRepository;
import br.com.leonardoloures.account.AccountService;
import com.strategicgains.repoexpress.domain.Identifier;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AccountRepository.class})
public class TransactionServiceTest {

    private AccountService accountService;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @Before
    public void setup() {
        accountService = PowerMockito.mock(AccountService.class);
        transactionRepository = PowerMockito.mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository, accountService);
    }

    private AccountEntity getSourceAccountWithBalance() {
        AccountEntity account = new AccountEntity();
        account.setId(new Identifier(new ObjectId("5c686ead8ea4acb1811105d4")));
        account.setAccountType(0);
        account.addBalance(new Double(500));
        account.setNome("Source");

        return account;
    }

    private AccountEntity getSourceAccountWithoutBalance() {
        AccountEntity account = new AccountEntity();
        account.setId(new Identifier(new ObjectId("5953ed9b7f3ed8f6ba645b42")));
        account.setAccountType(0);
        account.addBalance(new Double(0));
        account.setNome("Source");

        return account;
    }

    private AccountEntity getDestinationAccount() {
        AccountEntity account = new AccountEntity();
        account.setId(new Identifier(new ObjectId("595515b87f3e49ddeeac797c")));
        account.setAccountType(0);
        account.addBalance(new Double(100));
        account.setNome("Destination");

        return account;
    }

    private TransactionEntity getSimpleTransaction() {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSource(getSourceAccountWithBalance().getId());
        transactionEntity.setDestination(getDestinationAccount().getId());
        transactionEntity.setValue(new Double(250));
        return transactionEntity;
    }

    private TransactionEntity getFailTransaction() {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSource(getSourceAccountWithoutBalance().getId());
        transactionEntity.setDestination(getDestinationAccount().getId());
        transactionEntity.setValue(new Double(250));
        return transactionEntity;
    }

    @Test
    public void transactionCreateTest() {
        TransactionEntity start = getSimpleTransaction();
        TransactionEntity result = getSimpleTransaction();
        Identifier identifier = new Identifier(new ObjectId("5955499ce4b045fb5c593935"));
        result.setId(identifier);
        result.setStatus(TransactionStatusEnum.INITIAL);

        PowerMockito.doReturn(result).when(transactionRepository).create(start);

        TransactionEntity transactionEntity = transactionService.create(start);
        Assert.assertThat(transactionEntity.getId(), is(result.getId()));
        Assert.assertThat(transactionEntity.getStatus(), is(TransactionStatusEnum.INITIAL));
    }

    @Test
    public void transferFailTest() {
        TransactionEntity start = getFailTransaction();
        start.setStatus(TransactionStatusEnum.INITIAL);

        TransactionEntity entity = getFailTransaction();
        entity.setId(new Identifier(new ObjectId("5955499ce4b045fb5c593935")));
        entity.setStatus(TransactionStatusEnum.PENDING);

        AccountEntity sourceAccount = this.getSourceAccountWithoutBalance();
        AccountEntity destinationAccount = this.getDestinationAccount();

        PowerMockito.doReturn(entity).when(transactionRepository).create(start);
        PowerMockito.doReturn(sourceAccount).when(accountService).read(entity.getSource());
        PowerMockito.doReturn(destinationAccount).when(accountService).read(entity.getDestination());
        PowerMockito.doReturn(entity).when(transactionRepository).update(start);

        TransactionEntity transactionEntity = transactionService.transfer(start);

        Assert.assertThat(transactionEntity.getId(), is(entity.getId()));
        Assert.assertThat(transactionEntity.getStatus(), is(TransactionStatusEnum.FAILED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void transactionNotInitialTest() {
        TransactionEntity start = getFailTransaction();
        start.setStatus(TransactionStatusEnum.APPLIED);

        TransactionEntity transactionEntity = transactionService.transfer(start);
    }

    @Test
    public void tranferSuccessTest() {
        TransactionEntity transactionEntity = getSimpleTransaction();
        transactionEntity.setId(new Identifier(new ObjectId("5955499ce4b045fb5c593935")));
        transactionEntity.setStatus(TransactionStatusEnum.INITIAL);

        List<Identifier> identifierList = new ArrayList<>();
        identifierList.add(transactionEntity.getId());
        AccountEntity sourceAccount = this.getSourceAccountWithBalance();
        AccountEntity destinationAccount = this.getDestinationAccount();

        PowerMockito.doReturn(transactionEntity).when(transactionRepository).update(transactionEntity);
        PowerMockito.doReturn(sourceAccount).when(accountService).read(transactionEntity.getSource());
        PowerMockito.doReturn(destinationAccount).when(accountService).read(transactionEntity.getDestination());
        PowerMockito.doReturn(sourceAccount).when(accountService).update(sourceAccount);
        PowerMockito.doReturn(destinationAccount).when(accountService).update(destinationAccount);


        TransactionEntity result = transactionService.transfer(transactionEntity);

        Assert.assertThat(result.getId(), is(transactionEntity.getId()));
        Assert.assertThat(result.getStatus(), is(TransactionStatusEnum.DONE));
        Assert.assertThat(sourceAccount.getBalance(), is(this.getSourceAccountWithBalance().getBalance() - 250.0d));
        Assert.assertThat(destinationAccount.getBalance(), is(this.getDestinationAccount().getBalance() + 250.0d));
        Assert.assertThat(sourceAccount.getTransactions().size(), is(0));
        Assert.assertThat(destinationAccount.getTransactions().size(), is(0));
    }

    @Test
    public void testRetryPendingState() throws Exception {
        TransactionEntity transactionEntity = getSimpleTransaction();
        transactionEntity.setId(new Identifier(new ObjectId("5955499ce4b045fb5c593935")));
        transactionEntity.setStatus(TransactionStatusEnum.PENDING);

        AccountEntity sourceAccount = this.getSourceAccountWithBalance();
        //Middle of a Transaction
        sourceAccount.getTransactions().add(transactionEntity.getId());
        AccountEntity sourceAccountFinal = this.getSourceAccountWithBalance();
        AccountEntity sourceAccountUpdated = this.getSourceAccountWithBalance();
        sourceAccountUpdated.getTransactions().add(transactionEntity.getId());

        AccountEntity destinationAccount = this.getDestinationAccount();
        AccountEntity destinationAccountUpdated = this.getDestinationAccount();
        destinationAccountUpdated.getTransactions().add(transactionEntity.getId());
        destinationAccountUpdated.addBalance(100.0d);
        AccountEntity destinationAccountFinal = this.getDestinationAccount();
        destinationAccountFinal.addBalance(100.0d);

        PowerMockito.doReturn(sourceAccount).when(accountService).read(transactionEntity.getSource());
        PowerMockito.doReturn(sourceAccountUpdated).when(accountService).subtractBalance(
                transactionEntity.getSource(),
                transactionEntity.getValue(),
                transactionEntity.getId());
        PowerMockito.doReturn(sourceAccountFinal).when(accountService).update(sourceAccountUpdated);

        PowerMockito.doReturn(destinationAccount).when(accountService).read(transactionEntity.getDestination());
        PowerMockito.doReturn(destinationAccountUpdated).when(accountService).addBalance(
                transactionEntity.getDestination(),
                transactionEntity.getValue(),
                transactionEntity.getId());
        PowerMockito.doReturn(destinationAccountFinal).when(accountService).update(sourceAccountUpdated);

        PowerMockito.doReturn(transactionEntity).when(transactionRepository).update(transactionEntity);

        TransactionEntity result = transactionService.recoverPending(transactionEntity);

        Assert.assertThat(sourceAccountFinal.getBalance(), is(500.0d));
        Assert.assertThat(sourceAccountFinal.getTransactions().size(), is(0));

        Assert.assertThat(destinationAccountFinal.getBalance(), is(200.0d));
        Assert.assertThat(destinationAccountFinal.getTransactions().size(), is(0));

        Assert.assertThat(result.getStatus(), is(TransactionStatusEnum.DONE));
    }


    @Test
    public void testRecoverAppliedState() throws Exception {
        TransactionEntity transactionEntity = getSimpleTransaction();
        transactionEntity.setId(new Identifier(new ObjectId("5955499ce4b045fb5c593935")));
        transactionEntity.setStatus(TransactionStatusEnum.APPLIED);

        AccountEntity sourceAccount = this.getSourceAccountWithBalance();
        //Already modified by the unfinished transaction
        sourceAccount.getTransactions().add(transactionEntity.getId());
        AccountEntity sourceAccountFinal = this.getSourceAccountWithBalance();

        AccountEntity destinationAccount = this.getDestinationAccount();
        //Already modified by the unfinished transaction
        destinationAccount.getTransactions().add(transactionEntity.getId());
        AccountEntity destinationAccountFinal = this.getDestinationAccount();

        PowerMockito.doReturn(sourceAccount).when(accountService).read(transactionEntity.getSource());
        PowerMockito.doReturn(sourceAccountFinal).when(accountService).update(sourceAccount);
        PowerMockito.doReturn(destinationAccount).when(accountService).read(transactionEntity.getDestination());
        PowerMockito.doReturn(destinationAccountFinal).when(accountService).update(destinationAccount);
        PowerMockito.doReturn(transactionEntity).when(transactionRepository).update(transactionEntity);

        TransactionEntity result = transactionService.recoverApplied(transactionEntity);

        Assert.assertThat(sourceAccountFinal.getBalance(), is(500.0d));
        Assert.assertThat(sourceAccountFinal.getTransactions().size(), is(0));

        Assert.assertThat(destinationAccountFinal.getBalance(), is(100.0d));
        Assert.assertThat(destinationAccountFinal.getTransactions().size(), is(0));

        Assert.assertThat(result.getStatus(), is(TransactionStatusEnum.DONE));
    }

}