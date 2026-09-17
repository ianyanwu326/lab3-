package com.IK.Exceptions;

public class GuestAgeReservationException extends RuntimeException {
    public GuestAgeReservationException(int dummy) {
        super("guest age invalid");
    }
}
