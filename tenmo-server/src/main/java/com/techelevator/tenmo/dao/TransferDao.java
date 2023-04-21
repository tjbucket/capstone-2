package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    void sendMoney(int fromAccountId, int toUserId,BigDecimal amount);

    void requestMoney(int fromAccountId, int toAccountId, BigDecimal amount);

    List<Transfer> listRequestsByAccountId(int accountId);

    List<Transfer> listTransfersByAccountId(int accountId);

    Transfer getTransferById(int transferId);

    Transfer updateTransferStatus(int transferId, int newStatus);


}
