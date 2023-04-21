package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests{

    private JdbcTransferDao sut;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void sendMoney_throws_exception_when_transfer_from_and_to_are_the_same(){
        sut.sendMoney(2001,2001,new BigDecimal(500));
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void sendMoney_throws_exception_when_provided_0(){
        sut.sendMoney(2001,2002,new BigDecimal(0));
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void sendMoney_throws_exception_when_provided_negative_1(){
        sut.sendMoney(2001,2002,new BigDecimal(-1));
    }

    @Test
    public void sendMoney_success_generates_transfer_with_valid_properties() {
        sut.sendMoney(2001,2002, new BigDecimal(500));
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount" +
                " FROM transfer;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        results.first();
        Transfer transfer = new Transfer(results.getInt("transfer_id"), results.getInt("transfer_type_id"), results.getInt("transfer_status_id"), results.getInt("account_from"), results.getInt("account_to"), results.getBigDecimal("amount"));
        transferChecker(transfer,2,2,2001,2002,new BigDecimal("500.00"));
    }


    @Test
    public void listTransferByAccountId_lists_include_both_to_and_from() {
        sut.sendMoney(2001,2002, new BigDecimal(500));
        sut.sendMoney(2002,2001, new BigDecimal(500));
        sut.sendMoney(2002,2003, new BigDecimal(500));
        List<Transfer> transfers = sut.listTransfersByAccountId(2001);
        Assert.assertEquals(2,transfers.size());
        transfers = sut.listTransfersByAccountId(2002);
        Assert.assertEquals(3,transfers.size());
    }

    @Test
    public void getTransferById_returns_correct_transfer() {
        sut.sendMoney(2001,2002, new BigDecimal(500));
        List<Transfer> transfers = sut.listTransfersByAccountId(2001);
        Transfer transfer = sut.getTransferById(transfers.get(0).getTransfer_id());
        transferChecker(transfer,2,2,2001,2002,new BigDecimal("500.00"));
    }

    @Test
    public void getTransferById_returns_null_if_transfer_id_is_invalid(){
        Assert.assertNull(sut.getTransferById(12903));
    }


    public void transferChecker(Transfer transfer, int desiredTransferType, int desiredTransferStatus, int desiredAccountFrom, int desiredAccountTo, BigDecimal desiredAmount){
        Assert.assertEquals("Transfer type does not match", desiredTransferType, transfer.getTransfer_type_id());
        Assert.assertEquals("Transfer status does not match", desiredTransferStatus,transfer.getTransfer_status_id());
        Assert.assertEquals("Sending account does not match", desiredAccountFrom,transfer.getAccount_from());
        Assert.assertEquals("Receiving account does not match", desiredAccountTo,transfer.getAccount_to());
        Assert.assertEquals("Amount transferred does not match", desiredAmount, transfer.getAmount());
    }
}
