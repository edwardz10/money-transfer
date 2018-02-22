package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.MoneyTransfer;
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

public class AccountRestServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(AccountRestService.class);
    }

    @Before
    public void addAccounts() {
        Account account1 = new Account();
        account1.setName("petya");
        Entity<Account> entity = Entity.entity(account1, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts").request().post(entity, Account.class);

        Account account2 = new Account();
        account2.setName("vasya");
        entity = Entity.entity(account2, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts").request().post(entity, Account.class);
    }

    @After
    public void deleteAccounts() {
        target("/accounts/all").request().delete();
    }

    @Test
    public void testGetAllAccounts() {
        Account[] accounts = target("/accounts/all").request().get(Account[].class);
        assertTrue(accounts.length == 2);
    }

    @Test
    public void testPayroll() {
        Account[] accounts = target("/accounts/all").request().get(Account[].class);
        UUID firstAccountId = accounts[0].getId();

        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setAmount(1000L);

        Entity<MoneyTransfer> entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts/payroll/" + firstAccountId.toString()).request().post(entity, Account.class);

        Account account1 = target("/accounts/" + firstAccountId.toString()).request().get(Account.class);

        assertTrue(account1.getBalance() == 1000L);
    }

    @Test
    public void testTransfer() {
        Account[] accounts = target("/accounts/all").request().get(Account[].class);
        UUID firstAccountId = accounts[0].getId();
        UUID secondAccountId = accounts[1].getId();

        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setAmount(1000L);

        Entity<MoneyTransfer> entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts/payroll/" + firstAccountId.toString()).request().post(entity, Account.class);

        moneyTransfer = new MoneyTransfer(500L, firstAccountId, secondAccountId);
        entity = Entity.entity(moneyTransfer, MediaType.APPLICATION_JSON_TYPE);
        target("/accounts/transfer").request().post(entity);

        accounts = target("/accounts/all").request().get(Account[].class);
        assertTrue(accounts[0].getBalance() == 500L);
        assertTrue(accounts[1].getBalance() == 500L);
    }
}