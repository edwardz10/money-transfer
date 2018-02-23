package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.MoneyTransfer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Tests REST API implemented
 * in AccountRestService class
 */
public class AccountRestServiceTest extends JerseyTest {

    private static Log LOG = LogFactory.getLog(AccountDao.class);

    @Override
    protected Application configure() {
        return new ResourceConfig(AccountRestService.class);
    }

    /**
     * Before each unit test, creates
     * 2 accounts - "vasya" and "petya".
     */
    @Before
    public void addAccounts() {
        LOG.info("Create 'petya' account, balance is 0");
        Account account1 = new Account();
        account1.setName("petya");
        Entity<Account> entity = Entity.entity(account1, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts").request().post(entity, Account.class);

        LOG.info("Create 'vasya' account, balance is 0");
        Account account2 = new Account();
        account2.setName("vasya");
        entity = Entity.entity(account2, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts").request().post(entity, Account.class);
    }

    /**
     * After each unit test, deletes all accounts.
     */
    @After
    public void deleteAccounts() {
        LOG.info("Delete all accounts");
        target("/accounts/all").request().delete();
    }

    @Test
    public void testGetAllAccounts() {
        LOG.info("Test, that 2 accounts exist");
        Account[] accounts = target("/accounts/all").request().get(Account[].class);
        assertTrue(accounts.length == 2);
    }

    @Test
    public void testPayroll() {
        LOG.info("Get the 1st account id");
        Account[] accounts = target("/accounts/all").request().get(Account[].class);
        UUID firstAccountId = accounts[0].getId();

        LOG.info("Create a MoneyTransfer object with amount = 1000");
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setAmount(1000L);

        LOG.info("Payroll the MoneyTransfer object to the account");
        Entity<MoneyTransfer> entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts/payroll/" + firstAccountId.toString()).request().post(entity, Account.class);

        LOG.info("Get the account by its id");
        Account account1 = target("/accounts/" + firstAccountId.toString()).request().get(Account.class);

        LOG.info("Check, that account amount is 1000 now");
        assertTrue(account1.getBalance() == 1000L);
    }

    @Test
    public void testTransfer() {
        LOG.info("Get ids of the 1st and the 2nd accounts");
        Account[] accounts = target("/accounts/all").request().get(Account[].class);
        UUID firstAccountId = accounts[0].getId();
        UUID secondAccountId = accounts[1].getId();

        LOG.info("Create a MoneyTransfer object with amount = 1000");
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setAmount(1000L);

        LOG.info("Payroll the MoneyTransfer object to the account");
        Entity<MoneyTransfer> entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts/payroll/" + firstAccountId.toString()).request().post(entity, Account.class);

        LOG.info("Create a MoneyTransfer object with amount = 500" +
                ", 'from' and 'to' fields set to account ids");
        moneyTransfer = new MoneyTransfer(500L, firstAccountId, secondAccountId);

        LOG.info("Transfer money from the 1st to the 2nd account");
        entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts/transfer").request().post(entity);

        LOG.info("Get all accounts");
        accounts = target("/accounts/all").request().get(Account[].class);

        LOG.info("Check, that both accounts have 500 on their balance");
        assertTrue(accounts[0].getBalance() == 500L);
        assertTrue(accounts[1].getBalance() == 500L);
    }
}