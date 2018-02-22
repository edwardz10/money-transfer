package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.MoneyTransfer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("accounts")
public class AccountRestService {

    private static Log LOG = LogFactory.getLog(AccountDao.class);

    private AccountDao accountDao = AccountDao.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(Account newAccount) {
        return accountDao.createAccount(newAccount);
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccountById(@PathParam("id") UUID id) {
        return accountDao.getAccountById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payroll/{id}")
    public Account payroll(@PathParam("id") UUID id, MoneyTransfer money) {
        try {
            return accountDao.payroll(id, money);
        } catch (Exception e) {
            throw new javax.ws.rs.ProcessingException(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/withdraw/{id}")
    public Account withdraw(@PathParam("id") UUID id, MoneyTransfer money) throws ProcessingException {
        try {
            return accountDao.withdraw(id, money);
        } catch (Exception e) {
            throw new javax.ws.rs.ProcessingException(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/transfer")
    public void withdraw(MoneyTransfer money) throws ProcessingException {
        try {
            accountDao.transferMoney(money);
        } catch (Exception e) {
            throw new javax.ws.rs.ProcessingException(e.getMessage());
        }
    }

}
