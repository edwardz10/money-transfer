package com.revolut.moneytransfer.dao;

import com.revolut.moneytransfer.model.Account;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    public void transferMoney(Account from, Account end, long amount) throws Exception {

    }
}
