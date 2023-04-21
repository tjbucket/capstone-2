package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void sendMoney(int fromAccountId, int toUserId, BigDecimal amount) {
        String sql = "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES (2,2,?,?,?)";
        jdbcTemplate.update(sql,fromAccountId,toUserId,amount);
    }

    @Override
    public void requestMoney(int fromAccountId,int toAccountId,BigDecimal amount) {
        String sql = "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES (1,1,?,?,?)";
        jdbcTemplate.update(sql,fromAccountId,toAccountId,amount);
    }

    @Override
    public List<Transfer> listRequestsByAccountId(int accountId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE " +
                "account_from = ? AND transfer_type_id = 1 AND transfer_status_id = 1 ORDER BY transfer_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        return mapTransfersToList(results);
    }

    @Override
    public List<Transfer> listTransfersByAccountId(int accountId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE " +
                "account_from = ? OR account_to = ? ORDER BY transfer_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        return mapTransfersToList(results);
    }

    @Override
    public Transfer getTransferById(int transactionId) {
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE " +
                "transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transactionId);
        if (results.next()){
            Transfer transfer = new Transfer();
            transfer.setTransfer_id(results.getInt("transfer_id"));
            transfer.setTransfer_type_id(results.getInt("transfer_type_id"));
            transfer.setTransfer_status_id(results.getInt("transfer_status_id"));
            transfer.setAccount_from(results.getInt("account_from"));
            transfer.setAccount_to(results.getInt("account_to"));
            transfer.setAmount(results.getBigDecimal("amount"));
            return transfer;
        }
        return null;
    }

    @Override
    public Transfer updateTransferStatus(int transferId, int newStatus) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, newStatus, transferId);
        sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE " +
                " transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()){
            Transfer transfer = new Transfer();
            transfer.setTransfer_id(results.getInt("transfer_id"));
            transfer.setTransfer_type_id(results.getInt("transfer_type_id"));
            transfer.setTransfer_status_id(results.getInt("transfer_status_id"));
            transfer.setAccount_from(results.getInt("account_from"));
            transfer.setAccount_to(results.getInt("account_to"));
            transfer.setAmount(results.getBigDecimal("amount"));
            return transfer;
        }
        return null;
    }

    private List<Transfer> mapTransfersToList(SqlRowSet results){
        List<Transfer> transfers = new ArrayList<>();
        while(results.next()){
            Transfer transfer = new Transfer();
            transfer.setTransfer_id(results.getInt("transfer_id"));
            transfer.setTransfer_type_id(results.getInt("transfer_type_id"));
            transfer.setTransfer_status_id(results.getInt("transfer_status_id"));
            transfer.setAccount_from(results.getInt("account_from"));
            transfer.setAccount_to(results.getInt("account_to"));
            transfer.setAmount(results.getBigDecimal("amount"));
            transfers.add(transfer);
        }
        return transfers;
    }

}
