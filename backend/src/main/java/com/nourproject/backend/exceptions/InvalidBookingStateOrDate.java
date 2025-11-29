package com.nourproject.backend.exceptions;

public class InvalidBookingStateOrDate extends RuntimeException{
    public InvalidBookingStateOrDate(String message) {
        super(message);
    }
}
