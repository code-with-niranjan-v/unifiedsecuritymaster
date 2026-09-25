package com.example.unifiedsecuritymaster.exception;

public class BondNotFoundException extends RuntimeException{

    public BondNotFoundException(){
        super("Bond not found");
    }
}
