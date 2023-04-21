package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Balance;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping(path = "/account")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    JdbcAccountDao dao;

    public AccountController(JdbcAccountDao dao) {
        this.dao = dao;
    }

    @GetMapping
    public Balance showAccountBalance(Principal principal){
        return dao.viewAccountBalance(principal.getName());
    }

    @GetMapping("/{id}")
    public int getAccountIdFromUserId(@PathVariable int id){
        return dao.getAccountIdFromUserId(id);
    }
}
