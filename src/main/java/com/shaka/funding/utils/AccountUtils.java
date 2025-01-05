package com.shaka.funding.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXIST_CODE = "001";
    public static final String ACCOUNT_EXIST_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account created successfully";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "This user does not have an account with us";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User Account Found";
    public static final String ACCOUNT_CREDITED_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account Credited Successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";
    public static final String ACCOUNT_DEBITED_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account Debited Successfully";
    public static final String ACCOUNT_TRANSFERRED_CODE = "008";
    public static final String ACCOUNT_TRANSFERRED_SUCCESS_MESSAGE = "Transfer Successful";
    public static final String ACCOUNT_CREATION_FAILURE_CODE = "009";
    public static final String ACCOUNT_CREATION_FAILURE_MESSAGE = "An unexpected error occurred while creating account";
    public static final String BALANCE_ENQUIRY_SUCCESS_CODE = "010";
    public static final String BALANCE_ENQUIRY_SUCCESS_MESSAGE = "Balance Enquiry Successful";
    public static final String ACCOUNT_NOT_FOUND_CODE = "011";
    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found";
    public static final String BALANCE_ENQUIRY_FAILURE_CODE = "012";
    public static final String BALANCE_ENQUIRY_FAILURE_MESSAGE = "An unexpected error occurred while performing balance enquiry";
    public static final String NAME_ENQUIRY_FAILURE_MESSAGE = "An unexpected error occurred while performing name enquiry";

    public static String generateAccountNumber(){
        /**
         *  Generating a random account number with current year
         * **/
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        //generate random number between min and max
        int randomNum = (int) Math.floor(Math.random() * (max - min + 1) + min);
        //convert randomNum to string
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randomNum);

        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
//        return String.valueOf((int) (Math.random() * 1000000000));
    }
}
