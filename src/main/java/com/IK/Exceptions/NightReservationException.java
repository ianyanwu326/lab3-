package com.IK.Exceptions;

public class NightReservationException extends RuntimeException {
    public NightReservationException(int input) {
        super("night reservation invalid");
    }
}
