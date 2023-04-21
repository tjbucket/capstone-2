package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;

public interface AccountDao {
    Balance viewAccountBalance(String username);
    void updateAccounts(Transfer transfer);
    int getAccountIdFromUserId(int userId);
    int getUserIdFromAccountId(int accountId);
}
