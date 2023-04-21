package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }


    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void displayUserArray(User[] users){
        System.out.println("--------------User List--------------");
        System.out.println("ID     NAME                          ");
        System.out.println("-------------------------------------");
        for (User user : users) {
            int id = user.getId();
            System.out.print(id + spacingGenerator(7,id));
            String name = checkNameLength(user.getUsername());
            System.out.println(name);
        }
        System.out.println("-------------------------------------");
    }

    public boolean displayTransfersArray(TransferService transferService, boolean toAndFrom){
        Transfer[] transfers;
        if (toAndFrom) {
            transfers = transferService.displayListOfTransfers();
            System.out.println("ID     FROM/TO                              AMOUNT   STATUS");
        } else {
            transfers = transferService.displayListOfRequests();
            System.out.println("ID     FROM                                 AMOUNT   STATUS");
        }
        System.out.println("------------------------------------------------------------");
        if (transfers.length == 0){
            System.out.println(toAndFrom?"Transfer history is empty.":"You don't have any pending requests.");
            return true;
        }
        for (Transfer transfer : transfers) {
            int id = transfer.getTransfer_id();
            String toName = checkNameLength(transferService.getUserName(transfer.getAccount_to()));
            String fromName = checkNameLength(transferService.getUserName(transfer.getAccount_from()));
            System.out.print(id + spacingGenerator(7,id));
            System.out.print(transferService.convertUserIdToAccountId(transferService.getCurrentUser().getUser().getId())==transfer.getAccount_from()?
                             "TO:   "+ toName + spacingGenerator(30, toName):
                             "FROM: "+ fromName + spacingGenerator(30,fromName));
            System.out.print("$"+ transfer.getAmount()+spacingGenerator(8,transfer.getAmount().toPlainString()));
            System.out.println((transfer.getTransfer_status_id() == 1?"(P)":(transfer.getTransfer_status_id() == 2?"(A)":"(R)")));
        }
        System.out.println("------------------------------------------------------------");
        return false;
    }

    public void displayTransferDetails(Transfer transfer, String fromName,String toName){
        System.out.println("------------------------------");
        System.out.println("       Transfer Details");
        System.out.println("------------------------------");
        System.out.println("ID:     " + transfer.getTransfer_id());
        System.out.println("FROM:   "+ fromName);
        System.out.println("TO:     "+ toName);
        int transferTypeId = transfer.getTransfer_type_id();
        System.out.println("TYPE:   " + (transferTypeId==1?"REQUEST":"SEND"));
        int transferStatusId = transfer.getTransfer_status_id();
        System.out.println("STATUS: " +(transferStatusId == 1?"PENDING":(transferStatusId == 2?"APPROVED":"REJECTED")));
        System.out.println("AMOUNT: $"+ transfer.getAmount());
    }

    private String checkNameLength(String name){
        if(name.length() > 27){
            name = name.substring(0,26)+"...";
        }
        return name;
    }

    private String spacingGenerator(int totalLength, String stringToCheck){
        int difference = totalLength - stringToCheck.length();
        if (difference <= 0){
            return " ";
        }
        return " ".repeat(difference);
    }
    private String spacingGenerator(int totalLength, int intToCheck){
        int difference = totalLength - Integer.toString(intToCheck).length();
        if (difference <= 0){
            return " ";
        }
        return " ".repeat(difference);
    }


    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
