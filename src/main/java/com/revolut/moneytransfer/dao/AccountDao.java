package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.MoneyTransfer;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.revolut.moneytransfer.util.Constants.ACCOUNT_NOT_FOUND;
import static com.revolut.moneytransfer.util.Constants.INSUFFICIENT_FUNDS;

public class AccountDao {

    private static Log LOG = LogFactory.getLog(AccountDao.class);
    private static AccountDao instance;

    private ConcurrentHashMap<UUID, Account> accounts;

    private AccountDao() {
        accounts = new ConcurrentHashMap<>();
    }

    public static synchronized AccountDao getInstance(){
        if(instance == null) {
            instance = new AccountDao();
        }
        return instance;
    }

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

    public List<Account> getAllAccounts() {
        if (accounts.isEmpty()) {
            return Collections.emptyList();
        }

        return Lists.newArrayList(accounts.values());
    }

    public Account getAccountById(UUID id) {
        return accounts.get(id);
    }

    public synchronized Account payroll(UUID id, MoneyTransfer money) throws Exception {
        Account account = accounts.get(id);

        if (account != null) {
            account.setBalance(account.getBalance() + money.getAmount());
            return account;
        } else {
            throw new Exception(ACCOUNT_NOT_FOUND);
        }
    }

    public synchronized Account withdraw(UUID id, MoneyTransfer money) throws Exception {
        Account account = accounts.get(id);

        if (account != null) {
            if (account.getBalance() >= money.getAmount()) {
                account.setBalance(account.getBalance() - money.getAmount());
            } else {
                LOG.error(INSUFFICIENT_FUNDS + id);
                throw new Exception(INSUFFICIENT_FUNDS);
            }

            return account;
        } else {
            throw new Exception(ACCOUNT_NOT_FOUND);
        }
    }

    public void transferMoney(MoneyTransfer moneyTransfer) throws Exception {
        Account fromAccount = accounts.get(moneyTransfer.getFrom());
        Account toAccount = accounts.get(moneyTransfer.getTo());

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

    protected void doTransfer(Account fromAccount, Account toAccount, long amount) throws Exception {
        if (fromAccount.getBalance() >= amount) {
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);
        } else {
            LOG.error(INSUFFICIENT_FUNDS + fromAccount.getId());
            throw new Exception(INSUFFICIENT_FUNDS);
        }
    }
}
