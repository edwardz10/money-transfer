package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.MoneyTransferApplication;
import com.revolut.moneytransfer.model.Account;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;

public class AccountRestServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(AccountRestService.class);
    }

    @Test
    public void testGetAllAccounts() {
        final Account[] accounts = target("/accounts/all").request().get(Account[].class);
        assertEquals(accounts.length, 0);
    }
}
