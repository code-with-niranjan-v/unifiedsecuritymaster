package com.example.unifiedsecuritymaster.exception;

public class SecurityNotFoundException extends RuntimeException{

    public SecurityNotFoundException(){
        super("Security not found.");
    }

}
