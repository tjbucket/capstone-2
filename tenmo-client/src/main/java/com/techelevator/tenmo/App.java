package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private final AccountService accountService = new AccountService(API_BASE_URL, currentUser);
    private final TransferService transferService = new TransferService(API_BASE_URL, currentUser);

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            transferService.setCurrentUser(currentUser);
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            accountService.setAuthToken(currentUser.getToken());
            transferService.setAuthToken(currentUser.getToken());
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        BigDecimal balance = accountService.getCurrentBalance();
        System.out.println("Your current account balance is: $" + balance + ".");
	}


	private void viewTransferHistory() {
        System.out.println("----------------------Transfer History----------------------");
        boolean isEmpty = consoleService.displayTransfersArray(transferService,true);
        if (isEmpty){
            return;
        }
        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if(transferId == 0){
            return;
        }
        Transfer[] transfers = transferService.displayListOfTransfers();
        boolean transferInArray = false;
        for (Transfer transfer : transfers) {
            if (transferId == transfer.getTransfer_id()){
                transferInArray = true;
            }
        }
        if (!transferInArray){
            System.out.println("Transfer ID entered is not associated with your account. Please try again.");
            return;
        }
        Transfer transfer = transferService.getTransferById(transferId);
        consoleService.displayTransferDetails(transfer,transferService.getUserName(transfer.getAccount_from()),transferService.getUserName(transfer.getAccount_to()));
	}

	private void viewPendingRequests() {
        Transfer[] requests = transferService.displayListOfRequests();
        System.out.println("-----------------Pending Requests-----------------");
        boolean isEmpty = consoleService.displayTransfersArray(transferService, false);
        if (isEmpty){
            return;
        }
        int transferId = consoleService.promptForInt("Please enter transfer ID to approve or reject (0 to cancel): ");
        if(transferId == 0){
            return;
        }
        boolean requestInArray = false;
        for (Transfer request : requests) {
            if (transferId == request.getTransfer_id()){
                requestInArray = true;
            }
        }
        if (!requestInArray){
            System.out.println("Transfer ID entered is not associated with a pending request. Please try again.");
            return;
        }
        System.out.println("You've selected: " + transferId);
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("------------------------");
        int userSelection = consoleService.promptForInt("Please choose an option: ");
        if (userSelection == 0){
            return;
        } else if (userSelection >= 3){
            System.out.println("Invalid user option. Returning you to main menu.");
            return;
        } else {
            boolean success = transferService.updateRequestStatus(transferId,userSelection + 1);
            if (success){
                System.out.println("Transfer status successfully updated.");
            } else {
                System.out.println("Status change unsuccessful, please try again.");
            }
        }
	}

	private void sendBucks() {
        User[] users = transferService.listUsers();
        consoleService.displayUserArray(users);
        int toId = consoleService.promptForInt("Please enter user ID of user you would like to send funds to (0 to cancel): ");
        if (toId == 0){
            return;
        }
        if (currentUser.getUser().getId() == toId){
            System.out.println("You cannot transfer to yourself. Please try again.");
            return;
        }
        BigDecimal amountToTransfer = consoleService.promptForBigDecimal("Please enter amount to transfer: ");
        if (amountToTransfer.compareTo(new BigDecimal(0)) <= 0){
            System.out.println("Amount to transfer cannot be zero or less. Please try again.");
            return;
        }
        if (!isValidAmount(amountToTransfer)){
            System.out.println("Number input was not a valid amount of money. Please try again.");
            return;
        }
        boolean success = transferService.postNewTransfer(toId, amountToTransfer);
        if (success){
            System.out.println("Transfer completed successfully.");
        } else {
            System.out.println("Transfer unsuccessful. Please try again.");
        }
	}

	private void requestBucks() {
		User[] users = transferService.listUsers();
        consoleService.displayUserArray(users);
        int fromId = consoleService.promptForInt("Please enter user ID of user you would like to request funds from (0 to cancel): ");
        if (fromId == 0){
            return;
        }
        if (currentUser.getUser().getId() == fromId){
            System.out.println("You cannot request funds from yourself. Please try again.");
            return;
        }
        BigDecimal amountToTransfer = consoleService.promptForBigDecimal("Please enter amount to request: ");
        if (amountToTransfer.compareTo(new BigDecimal(0)) <= 0){
            System.out.println("Amount requested cannot be zero or less. Please try again.");
            return;
        }
        if (!isValidAmount(amountToTransfer)){
            System.out.println("Number input was not a valid amount of money. Please try again.");
            return;
        }
        boolean success = transferService.postNewRequest(fromId, amountToTransfer);
        if (success){
            System.out.println("Request made successfully.");
        } else {
            System.out.println("Request unsuccessful. Please try again.");
        }
	}

    private boolean isValidAmount(BigDecimal amount){
        String amountAsString = amount.toPlainString();
        if (amountAsString.contains(".")) {
            String[] split = amount.toPlainString().split("\\.");
            if (split[0].length() > 13){
                return false;
            }
            if (split[1].length() > 2) {
                return false;
            }
        }
        return true;
    }
}
