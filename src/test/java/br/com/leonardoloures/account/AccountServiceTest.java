package br.com.leonardoloures.account;

import com.strategicgains.repoexpress.domain.Identifier;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AccountRepository.class})
public class AccountServiceTest {
    
    private AccountRepository accountRepository;
    private AccountService accountService;

    @Before
    public void setup() {
        accountRepository = PowerMockito.mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);
    }

    private AccountEntity getTestAccount(){
        AccountEntity account = new AccountEntity();
        account.setAccountType(0);
        account.setBalance(new BigDecimal(500));
        account.setNome("Foo");

        return account;
    }

    @Test
    public void accountCreateTest() {
        AccountEntity result = getTestAccount();
        result.setId(new Identifier(new ObjectId("5c686ead8ea4acb1811105d4")));
        AccountEntity start = getTestAccount();

        PowerMockito.doReturn(result).when(accountRepository).create(start);

        AccountEntity account = accountService.create(start);
        Assert.assertThat(account.getId(), is(result.getId()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void accountDeleteFailTest() throws Exception {
        AccountEntity result = getTestAccount();
        Identifier id = new Identifier(new ObjectId("5c686ead8ea4acb1811105d4"));
        result.setId(id);

        PowerMockito.doReturn(result).when(accountRepository).read(id);

        accountService.delete(id);
    }

    @Test
    public void accountSubtractSuccessTest() throws UnsupportedOperationException {
        Identifier id = new Identifier(new ObjectId("5c686ead8ea4acb1811105d4"));
        AccountEntity start = getTestAccount();
        start.setId(id);
        AccountEntity result = getTestAccount();
        result.setId(id);

        PowerMockito.doReturn(result).when(accountRepository).read(id);
        PowerMockito.doReturn(result).when(accountRepository).update(result);

        result = accountService.subtractBalance(start.getId(), new BigDecimal(100));
        Assert.assertThat(result.getId(), is(start.getId()));
        Assert.assertThat(result.getBalance(), is(start.getBalance().subtract(new BigDecimal(100))));
    }

    @Test
    public void accountSubtractNullTest() throws UnsupportedOperationException {
        Identifier id = new Identifier(new ObjectId("5c686ead8ea4acb1811105d4"));
        AccountEntity start = getTestAccount();
        AccountEntity result = getTestAccount();
        result.setId(id);

        PowerMockito.doReturn(null).when(accountRepository).read(id);
        PowerMockito.doReturn(result).when(accountRepository).update(result);

        result = accountService.subtractBalance(start.getId(), new BigDecimal(100));
        Assert.assertEquals(null, result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void accountSubtractFailTest() throws UnsupportedOperationException {
        Identifier id = new Identifier(new ObjectId("5c686ead8ea4acb1811105d4"));
        AccountEntity start = getTestAccount();
        AccountEntity result = getTestAccount();
        result.setId(id);

        PowerMockito.doReturn(start).when(accountRepository).read(id);
        PowerMockito.doReturn(result).when(accountRepository).update(result);

        result = accountService.subtractBalance(start.getId(), new BigDecimal(100));
    }

    @Test
    public void accountAddTest() {
        Identifier id = new Identifier(new ObjectId("5c686ead8ea4acb1811105d4"));
        AccountEntity start = getTestAccount();
        start.setId(id);
        AccountEntity result = getTestAccount();
        result.setId(id);

        PowerMockito.doReturn(result).when(accountRepository).read(id);
        PowerMockito.doReturn(result).when(accountRepository).update(result);

        result = accountService.addBalance(start.getId(), new BigDecimal(100));
        Assert.assertThat(result.getId(), is(start.getId()));
        Assert.assertThat(result.getBalance(), is(start.getBalance().add(new BigDecimal(100))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void accountUpdateFailTest() {
        AccountEntity start = getTestAccount();

        PowerMockito.doReturn(start).when(accountRepository).update(start);

        start = accountService.update(start);
    }

    @Test
    public void accountUpdateSuccessTest() {
        Identifier id = new Identifier(new ObjectId("5c686ead8ea4acb1811105d4"));
        AccountEntity start = getTestAccount();
        start.setId(id);

        PowerMockito.doReturn(start).when(accountRepository).update(start);

        AccountEntity result = accountService.update(start);

        Assert.assertEquals(result, start);
    }
}
