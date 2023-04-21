package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcAccountTests extends BaseDaoTests{
    private JdbcAccountDao sut;
    private JdbcTemplate jdbcTemplate;

    private static Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal("1000.00"));
    private static Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal("1000.00"));
    private static Account ACCOUNT_3 = new Account(2003, 1003, new BigDecimal("1000.00"));


    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void viewAccountBalance_returns_correct_balance() {
        Assert.assertEquals(ACCOUNT_1.getBalance(), sut.viewAccountBalance("user1").getCurrentBalance());
    }

    @Test
    public void updateAccounts_updates_both_account_balances() {
        Transfer transfer = new Transfer(3001,2,2,ACCOUNT_1.getAccountId(),ACCOUNT_2.getAccountId(),new BigDecimal(250));
        sut.updateAccounts(transfer);
        Assert.assertEquals(new BigDecimal("750.00"), sut.viewAccountBalance("user1").getCurrentBalance());
        Assert.assertEquals(new BigDecimal("1250.00"), sut.viewAccountBalance("user2").getCurrentBalance());

    }

    @Test
    public void getAccountIdFromUserId_returns_0_when_provided_invalid_user_id() {
        Assert.assertEquals(0, sut.getAccountIdFromUserId(91283));
    }

    @Test
    public void getAccountIdFromUserId_returns_valid_account_id_provided_valid_user_id(){
        Assert.assertEquals(ACCOUNT_1.getAccountId(), sut.getAccountIdFromUserId(ACCOUNT_1.getUserId()));
    }

    @Test
    public void getUserIdFromAccountId_returns_0_when_provided_invalid_account_id() {
        Assert.assertEquals(0, sut.getUserIdFromAccountId(7987));
    }

    @Test
    public void getUserIdFromAccountId_returns_valid_user_id_provided_valid_account_id(){
        Assert.assertEquals(ACCOUNT_3.getUserId(), sut.getUserIdFromAccountId(ACCOUNT_3.getAccountId()));
    }
}
