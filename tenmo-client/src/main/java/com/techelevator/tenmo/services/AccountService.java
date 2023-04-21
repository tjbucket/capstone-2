package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Balance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private String baseApiUrl;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate;
    private String authToken;

    public AccountService(String baseApiUrl, AuthenticatedUser currentUser) {
        this.baseApiUrl = baseApiUrl;
        this.currentUser = currentUser;
        this.restTemplate = new RestTemplate();
    }

    public void setAuthToken (String authToken) {
        this.authToken = authToken;
    }

    // METHOD FOR STEP 3: AS AN AUTHENTICATED USER I MUST BE ABLE TO SEE MY ACCOUNT BALANCE
    //TODO handle api exceptions - see pg.8, integrate authentication token to the method below
    public BigDecimal getCurrentBalance() {
//        String url = baseApiUrl + "/account";
//
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.setBearerAuth(currentUser.getToken());
//
//        HttpEntity<Object> entity = new HttpEntity<>(headers);
//        try {
//            //Makes an HTTP Request to the API Server to get the account
//            Account currentAccount = restTemplate.getForObject(url, Account.class, entity);
//            // The client receives the JSON response from the server and deserializes
//            // the JSON into a new account java object
//            return currentAccount.getBalance();
//            // retrieves the balance from the newly deserialized account and returns it.
//        } catch (RestClientResponseException e) {
//        }
//        return null;

        BigDecimal balance;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entityForGet = new HttpEntity<>(headers);
        ResponseEntity<Balance> response = restTemplate.exchange(
                baseApiUrl + "account", HttpMethod.GET, entityForGet, Balance.class);
        balance = response.getBody().getCurrentBalance();
        return balance;
    }

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }



    /*
   starting point - attribute 'balance' represents current balance of account plus a constructor
   that takes the initial balance as a parameter and sets the 'balance' attribute respectively.

   the 'getBalance()' method returns current balance of account

   * */


}


