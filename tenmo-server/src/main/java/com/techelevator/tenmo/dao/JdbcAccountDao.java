package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcAccountDao implements AccountDao{
    JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Balance viewAccountBalance(String username) {
        String sql = "SELECT balance FROM account AS a JOIN tenmo_user AS tu ON " +
                "a.user_id = tu.user_id where username = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        result.next();
        return new Balance(result.getBigDecimal("balance"));
        // when you do a query for row set you have to look for next line
        // when you're mapping your results its populating with nothing because it's not looking at a next line
        // u need user id account and balance, and we cna just look it up by user_id since it's part of an account
    }

    @Override
    public void updateAccounts(Transfer transfer) {
        String sql = "BEGIN TRANSACTION;" +
                "UPDATE account SET balance = balance - ? WHERE account_id = ?;" +
                "UPDATE account SET balance = balance + ? WHERE account_id = ?;" +
                "COMMIT;";
        jdbcTemplate.update(sql,transfer.getAmount(),transfer.getAccount_from(),transfer.getAmount(), transfer.getAccount_to());
        // gonna have to write a whole nother method that reverses this?
    }

    @Override
    public int getAccountIdFromUserId(int userId) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.first()) {
            return results.getInt("account_id");
        }
        return 0;
    }

    @Override
    public int getUserIdFromAccountId(int accountId) {
        String sql = "SELECT user_id FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if (results.first()) {
            return results.getInt("user_id");
        }
        return 0;
    }


    private List<Account> mapAccountsToList(SqlRowSet results){
        List<Account> accounts = new ArrayList<>();
        while(results.next()){
            Account account = new Account(results.getInt("account_id"), results.getInt("user_id"), results.getBigDecimal("balance"));
            accounts.add(account);
        }
        return accounts;
    }
}
