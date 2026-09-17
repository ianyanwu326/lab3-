import com.IK.ReserveMyPark;
import com.IK.Exceptions.GuestAgeReservationException;
import com.IK.Exceptions.NightReservationException;
import com.IK.Exceptions.ReservationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Implements the master (deduplicated) test cases from Lab3_Test_Design.md:
 * Equivalence Partitions, Boundary Values, and the Resident/Veteran Decision Table.
 */
class StayPriceCalculatorTest {

    private final ReserveMyPark calculator = new ReserveMyPark();
    private static final double DELTA = 0.001;

    // ---- Valid inputs: nights BVA, guestAge EP/BVA, and the resident/veteran decision table ----
    @ParameterizedTest(name = "[{index}] nights={0}, age={1}, resident={2}, veteran={3} -> {4}")
    @DisplayName("calculateStayPrice returns the correct price for valid inputs")
    @CsvSource({
            // nights boundary values (age=30 adult, no status discounts)
            "1,  30, false, false, 50.0",    // min boundary
            "2,  30, false, false, 100.0",   // min + 1
            "7,  30, false, false, 350.0",   // nominal / decision rule R1 (F,F)
            "13, 30, false, false, 650.0",   // max - 1
            "14, 30, false, false, 700.0",   // max boundary

            // guestAge partitions / boundaries (nights=7, no status discounts)
            "7,  0,  false, false, 175.0",   // child min boundary
            "7,  12, false, false, 175.0",   // child/adult edge
            "7,  13, false, false, 350.0",   // adult lower edge
            "7,  64, false, false, 350.0",   // adult/senior edge
            "7,  65, false, false, 280.0",   // senior lower edge

            // resident / veteran decision table (nights=7, age=30)
            "7,  30, true,  false, 340.0",   // rule R3: resident only
            "7,  30, false, true,  315.0",   // rule R2: veteran only
            "7,  30, true,  true,  306.0"    // rule R4: resident then veteran
    })
    void testValidPriceCalculations(int nights, int guestAge, boolean isArkansasResident,
                                    boolean hasVeteranDiscount, double expectedPrice) throws Exception {
        double actual = calculator.calculateStayPrice(nights, guestAge, isArkansasResident, hasVeteranDiscount);
        assertEquals(expectedPrice, actual, DELTA);
    }

    // ---- Invalid nights: below min and above max boundaries ----
    @ParameterizedTest(name = "[{index}] nights={0} should throw NightReservationException")
    @DisplayName("calculateStayPrice rejects nights outside the 1-14 range")
    @CsvSource({
            "0",   // below minimum boundary
            "15"   // above maximum boundary
    })
    void testInvalidNightsThrowsException(int nights) {
        assertThrows(NightReservationException.class,
                () -> calculator.calculateStayPrice(nights, 30, false, false));
    }

    // ---- Invalid guestAge: below the minimum boundary ----
    @Test
    @DisplayName("calculateStayPrice rejects a negative guestAge")
    void testInvalidAgeThrowsException() {
        assertThrows(GuestAgeReservationException.class,
                () -> calculator.calculateStayPrice(7, -1, false, false));
    }
}


