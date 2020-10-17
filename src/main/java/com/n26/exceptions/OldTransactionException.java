package com.n26.exceptions;

public class OldTransactionException extends Exception {

    public OldTransactionException(long diff) {
        super("Transaction too old: " + diff);
    }
}
