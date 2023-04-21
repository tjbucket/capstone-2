package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfer")
public class TransferController {
    JdbcTransferDao transferDao;
    JdbcUserDao userDao;
    JdbcAccountDao accountDao;

    public TransferController(JdbcTransferDao transferDao, JdbcAccountDao accountDao, JdbcUserDao userDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @GetMapping("/users")
    public List<User> retrieveUserList(Principal principal){
        return userDao.listAllUsersExceptPrincipal(userDao.findIdByUsername(principal.getName()));
    }

    @GetMapping("/getuser/{id}")
    public User retrieveUser(@PathVariable int id){
        return userDao.getUserById(accountDao.getUserIdFromAccountId(id));
    }


    @PostMapping("/send")
    public boolean sendMoney(@RequestBody Transfer requestedTransfer, Principal principal){
        //Ensures logged-in user has enough money in account to do transfer
        if(accountDao.viewAccountBalance(principal.getName()).getCurrentBalance().compareTo(requestedTransfer.getAmount()) >= 0 &&
                //Ensures account being transferred from is the logged-in account to protect against attacks from 3rd party clients(e.g. Postman)
                requestedTransfer.getAccount_from() == accountDao.getAccountIdFromUserId(userDao.findIdByUsername(principal.getName())) &&
                //Ensures account being transferred into exists
                accountDao.getUserIdFromAccountId(requestedTransfer.getAccount_to()) != 0) {
            transferDao.sendMoney(requestedTransfer.getAccount_from(), requestedTransfer.getAccount_to(), requestedTransfer.getAmount());
            accountDao.updateAccounts(requestedTransfer);
            return true;
        }
        return false;
    }

    @PostMapping("/request")
    public boolean requestMoney(@RequestBody Transfer requestedTransfer, Principal principal){
        if (accountDao.getUserIdFromAccountId(requestedTransfer.getAccount_to()) != 0){
            transferDao.requestMoney(requestedTransfer.getAccount_from(),requestedTransfer.getAccount_to(), requestedTransfer.getAmount());
            return true;
        }
        return false;
    }

    @GetMapping("/list")
    public List<Transfer> listTransfersForUserId(Principal principal){
        return transferDao.listTransfersByAccountId(accountDao.getAccountIdFromUserId(userDao.findIdByUsername(principal.getName())));
    }

    @GetMapping("/{id}")
    public Transfer getTransferByTransactionId(@PathVariable int id){
        return transferDao.getTransferById(id);
    }

    @GetMapping("/requests/list")
    public List<Transfer> listRequestsForUserId(Principal principal){
        return transferDao.listRequestsByAccountId(accountDao.getAccountIdFromUserId(userDao.findIdByUsername(principal.getName())));
    }
    @PutMapping("/request")
    public boolean updateRequestStatus(@RequestBody Transfer transfer, Principal principal) {
        Transfer updatedTransfer;
        if (accountDao.viewAccountBalance(principal.getName()).getCurrentBalance().compareTo(transfer.getAmount()) >= 0 &&
                transfer.getAccount_from() == accountDao.getAccountIdFromUserId(userDao.findIdByUsername(principal.getName()))) {
            updatedTransfer = transferDao.updateTransferStatus(transfer.getTransfer_id(), transfer.getTransfer_status_id());
            if (transfer.getTransfer_status_id() == 2) {
                accountDao.updateAccounts(updatedTransfer);
            }
            return true;
        }
        return false;
    }
}
