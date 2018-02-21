package com.revolut.moneytransfer.resource;

import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.model.Account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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

}
