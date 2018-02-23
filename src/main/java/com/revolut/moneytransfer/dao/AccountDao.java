package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.exception.AccountNotFoundException;
import com.revolut.moneytransfer.exception.InsufficientFundsException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.MoneyTransfer;
import com.revolut.moneytransfer.util.Constants;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.revolut.moneytransfer.util.Constants.ACCOUNT_NOT_FOUND;
import static com.revolut.moneytransfer.util.Constants.INSUFFICIENT_FUNDS;

/**
 * DAO class that supports basic operations
 * on accounts.
 */
public class AccountDao {

    private static Log LOG = LogFactory.getLog(AccountDao.class);
    private static AccountDao instance;

    /**
     * Thread-safe map to store accounts in.
     */
    private ConcurrentHashMap<UUID, Account> accounts;

    /**
     * Private constructor to
     * prevent constructing multiple copies,
     */
    private AccountDao() {
        accounts = new ConcurrentHashMap<>();
    }

    /**
     * Singleton static methood.
     * @return Single instance of AccountDAO
     */
    public static synchronized AccountDao getInstance(){
        if(instance == null) {
            instance = new AccountDao();
        }
        return instance;
    }

    /**
     * Creates a new account in the map.
     * @param account Account object
     * @return Account object updated with an id.
     */
    public Account createAccount(Account account) {
        if (account.getId() == null) {
            account.setId(UUID.randomUUID());
        }
        if (account.getBalance() == null) {
            account.setBalance(0L);
        }

        accounts.putIfAbsent(account.getId(), account);
        return account;
    }

    /**
     * Gets all accounts.
     * @return List of Account objects
     */
    public List<Account> getAllAccounts() {
        if (accounts.isEmpty()) {
            return Collections.emptyList();
        }

        return Lists.newArrayList(accounts.values());
    }

    /**
     * Gets an Account object by an id
     * @param id Account id
     * @return Account object with the id
     */
    public Account getAccountById(UUID id) throws AccountNotFoundException {
        Account account = accounts.get(id);

        if (account != null) {
            return account;
        } else {
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);
        }
    }

    /**
     * Payrolls funds to an account
     * @param id Account id
     * @param money Sum of money
     * @return Updated Account object
     * @throws AccountNotFoundException, if no Account with such id exists
     */
    public synchronized Account payroll(UUID id, MoneyTransfer money)
            throws AccountNotFoundException {
        Account account = accounts.get(id);

        if (account != null) {
            account.setBalance(account.getBalance() + money.getAmount());
            return account;
        } else {
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);
        }
    }

    /**
     * Withdraws funds from an account
     * @param id Account id
     * @param money Sum of money
     * @return Updated Account object
     * @throws AccountNotFoundException, if no Account with such id exists
     * @throws InsufficientFundsException, if insufficient funds
     */
    public synchronized Account withdraw(UUID id, MoneyTransfer money)
            throws AccountNotFoundException, InsufficientFundsException {
        Account account = accounts.get(id);

        if (account != null) {
            if (account.getBalance() >= money.getAmount()) {
                account.setBalance(account.getBalance() - money.getAmount());
            } else {
                LOG.error(INSUFFICIENT_FUNDS + id);
                throw new InsufficientFundsException(INSUFFICIENT_FUNDS);
            }

            return account;
        } else {
            throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);
        }
    }

    public void transferMoney(MoneyTransfer moneyTransfer)
            throws AccountNotFoundException, InsufficientFundsException {
        Account fromAccount = accounts.get(moneyTransfer.getFrom());

        if (fromAccount == null) {
            throw new AccountNotFoundException(
                    Constants.ACCOUNT_NOT_FOUND + ": " + moneyTransfer.getFrom());
        }

        Account toAccount = accounts.get(moneyTransfer.getTo());

        if (toAccount == null) {
            throw new AccountNotFoundException(
                    Constants.ACCOUNT_NOT_FOUND + ": " + moneyTransfer.getTo());
        }

        if (moneyTransfer.getFrom().compareTo(moneyTransfer.getTo()) > 0) {
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    doTransfer(fromAccount, toAccount, moneyTransfer.getAmount());
                }
            }
        } else {
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    doTransfer(fromAccount, toAccount, moneyTransfer.getAmount());
                }
            }
        }
    }

    protected void doTransfer(Account fromAccount, Account toAccount, long amount)
            throws InsufficientFundsException {
        if (fromAccount.getBalance() >= amount) {
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);
        } else {
            LOG.error(INSUFFICIENT_FUNDS + ": " + fromAccount.getId());
            throw new InsufficientFundsException(INSUFFICIENT_FUNDS);
        }
    }

    public void deleteAllAccounts() {
        accounts.clear();
    }
}
