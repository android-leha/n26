package com.n26.exceptions;

public class InvalidTransaction extends Exception {

    public InvalidTransaction(final String s) {
        super(s);
    }
}
