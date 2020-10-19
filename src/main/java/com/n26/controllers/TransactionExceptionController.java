package com.n26.controllers;

import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.InvalidTransaction;
import com.n26.exceptions.OldTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class TransactionExceptionController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Handle old transactions
     * @param e OldTransactionException
     */
    @ExceptionHandler(OldTransactionException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleException(OldTransactionException e) {
        logger.warn(e.getMessage());
    }

    /**
     * Handle wrong request
     * @param e InvalidRequestException
     */
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(InvalidRequestException e) {
        logger.warn(e.getMessage());
    }

    /**
     * Handle wrong JSON
     * @param e HttpMessageNotReadableException
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(HttpMessageNotReadableException e) {
        logger.warn(e.getMessage());
    }

    /**
     * Handle bad transactions
     * @param e InvalidTransaction
     */
    @ExceptionHandler(InvalidTransaction.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public void handleException(InvalidTransaction e) {
        logger.warn(e.getMessage());
    }
}
