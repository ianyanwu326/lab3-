package com.IK;

import com.IK.Exceptions.GuestAgeReservationException;
import com.IK.Exceptions.NightReservationException;
import com.IK.Exceptions.ReservationException;

public class StayPriceCalculator {

    private static final double BASE_PRICE = 50.0;
    private static final int MIN_STAY = 1;
    private static final int MAX_STAY = 14;

    private static final int CHILD_MAX_AGE = 12;
    private static final double CHILD_DISCOUNT = 0.5;

    private static final int SENIOR_MIN_AGE = 65;
    private static final double SENIOR_DISCOUNT = 0.2;

    private static final double RESIDENT_DISCOUNT = 10.0;
    private static final double VETERAN_DISCOUNT = 0.1;

    public double calculateStayPrice(
            int nights,
            int guestAge,
            boolean isArkansasResident,
            boolean hasVeteranDiscount
    ) throws ReservationException {

        if (nights < MIN_STAY || nights > MAX_STAY) {
            throw new NightReservationException(nights);
        }

        if (guestAge < 0) {
            throw new GuestAgeReservationException(guestAge);
        }

        // Base price
        double price = nights * BASE_PRICE;

        // Age-based discounts
        if (guestAge <= CHILD_MAX_AGE) {
            price -= price * CHILD_DISCOUNT;
        }

        if (guestAge >= SENIOR_MIN_AGE) {
            price -= price * SENIOR_DISCOUNT;
        }

        // Status-based discounts
        if (isArkansasResident) {
            price -= RESIDENT_DISCOUNT;
        }

        if (hasVeteranDiscount) {
            price -= price * VETERAN_DISCOUNT;
        }

        return price;
    }
}