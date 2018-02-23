package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.exception.AccountNotFoundException;
import com.revolut.moneytransfer.exception.InsufficientFundsException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.MoneyTransfer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

/**
 * JAX RS controller that provides
 * REST API for managing accounts
 */
@Path("accounts")
public class AccountRestService {

    private static Log LOG = LogFactory.getLog(AccountDao.class);

    private AccountDao accountDao = AccountDao.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(Account newAccount) {
        LOG.info("Request to create a new account " + newAccount + "...");
        return accountDao.createAccount(newAccount);
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAllAccounts() {
        LOG.info("Request to get all accounts");
        return accountDao.getAllAccounts();
    }

    @DELETE
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteAllAccounts() {
        LOG.info("Request to delete all accounts");
        accountDao.deleteAllAccounts();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccountById(@PathParam("id") UUID id) throws ProcessingException {
        LOG.info("Request to get an account by id: " + id + "...");

        try {
            return accountDao.getAccountById(id);
        } catch (AccountNotFoundException e) {
            LOG.error("Account " + id + " not found: " + e);
            throw new javax.ws.rs.ProcessingException(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payroll/{id}")
    public Account payroll(@PathParam("id") UUID id, MoneyTransfer money) {
        LOG.info("Request to payroll " + money.getAmount()
            + " to account " + id + "...");

        try {
            return accountDao.payroll(id, money);
        } catch (AccountNotFoundException e) {
            LOG.error("Account " + id + " not found: " + e);
            throw new javax.ws.rs.ProcessingException(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/withdraw/{id}")
    public Account withdraw(@PathParam("id") UUID id, MoneyTransfer money)
            throws ProcessingException {
        LOG.info("Request to withdraw " + money.getAmount()
                + " from account " + id + "...");
        try {
            return accountDao.withdraw(id, money);
        } catch (AccountNotFoundException acnfe) {
            LOG.error("Account " + id + " not found: " + acnfe);
            throw new javax.ws.rs.ProcessingException(acnfe.getMessage());
        } catch (InsufficientFundsException ife) {
            LOG.error("Insufficient funds on account " + id + ": " + ife);
            throw new javax.ws.rs.ProcessingException(ife.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/transfer")
    public void withdraw(MoneyTransfer money) throws ProcessingException {
        LOG.info("Request to transfer " + money.getAmount() + " from account "
                + money.getFrom() + " to account " + money.getTo() + "...");

        try {
            accountDao.transferMoney(money);
        } catch (AccountNotFoundException anfe) {
            LOG.error("Account not found: " + anfe);
            throw new javax.ws.rs.ProcessingException(anfe.getMessage());
        } catch (InsufficientFundsException ife) {
            LOG.error("Insufficient funds on account " + money.getFrom() + ": " + ife);
            throw new javax.ws.rs.ProcessingException(ife.getMessage());
        }
    }

}
