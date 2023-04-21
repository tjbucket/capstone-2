package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Balance {
    private BigDecimal currentBalance;

    public BigDecimal getCurrentBalance(){
        return currentBalance;
    }

    public Balance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
}
