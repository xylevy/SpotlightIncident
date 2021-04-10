package com.spotlight.incident;

public class ValidationManager {

    static final String VALID_EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    static final String VALID_PASSWORD_REGEX = "((?=.*\\d)(?=.*[A-Za-z]).{8,16})";
    static final String VALID_PHONE_REGEX ="^(0?)(7(?:(?:[0-9][0-9])|(?:0[0-8])|(9[0-2]))[0-9]{6})$";

    private ValidationManager(){}


    public static boolean isFieldEmpty(String data) {
        boolean isFieldEmpty = false;
        if (data == null || data.equals("") || data.length() < 2) {
            isFieldEmpty = true;
        }

        return isFieldEmpty;
    }

    public static boolean isEmailValid(String email) {

        boolean isFieldValid = true;

        if (!email.isEmpty() && !email.matches(VALID_EMAIL_REGEX)) {
            isFieldValid = false;
        }

        return !isFieldValid;
    }

    public static boolean isValidMobileNumber(String mobileNumber) {

        boolean isFieldValid = true;

        if (mobileNumber.length() < 9) {
            isFieldValid = false;
        }

        if (!mobileNumber.isEmpty() && !mobileNumber.matches(VALID_PHONE_REGEX)){
            isFieldValid=false;
        }

        return !isFieldValid;
    }

    public static boolean isValidPassword(String password){

        boolean isFieldValid = true;

        if (!password.isEmpty() && !password.matches(VALID_PASSWORD_REGEX)) {
            isFieldValid = false;
        }

        return !isFieldValid;
    }

}
