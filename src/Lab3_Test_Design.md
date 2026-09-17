# Lab Three: Test Design
## `calculateStayPrice(int nights, int guestAge, boolean isArkansasResident, boolean hasVeteranDiscount)`

Pricing logic (from `IK.ReserveMyPark.java`):
- Base price = `nights * $50`
- Child (age 0–12): 50% off
- Adult (age 13–64): full price
- Senior (age 65+): 20% off
- Arkansas Resident: additional flat $10 off
- Veteran: additional 10% off (applied to whatever remains **after** the resident discount)
- Invalid `nights` (< 1 or > 14) → `NightReservationException`
- Invalid `guestAge` (< 0) → `GuestAgeReservationException`

---

## Step One: Equivalence Partitions

### TCIs (Test Condition Items)

| ID | Parameter | Partition | Valid? |
|----|-----------|-----------|--------|
| EP-N1 | nights | < 1 | Invalid |
| EP-N2 | nights | 1 – 14 | Valid |
| EP-N3 | nights | > 14 | Invalid |
| EP-A1 | guestAge | < 0 | Invalid |
| EP-A2 | guestAge | 0 – 12 (Child) | Valid |
| EP-A3 | guestAge | 13 – 64 (Adult) | Valid |
| EP-A4 | guestAge | ≥ 65 (Senior) | Valid |
| EP-R1 | isArkansasResident | true | Valid |
| EP-R2 | isArkansasResident | false | Valid |
| EP-V1 | hasVeteranDiscount | true | Valid |
| EP-V2 | hasVeteranDiscount | false | Valid |

### Test Cases

| # | nights | guestAge | resident | veteran | TCIs covered | Expected Result |
|---|--------|----------|----------|---------|---------------|------------------|
| TC-EP1 | -5 | 30 | false | false | EP-N1 | `NightReservationException` |
| TC-EP2 | 7 | 30 | false | false | EP-N2, EP-A3, EP-R2, EP-V2 | 350.0 |
| TC-EP3 | 20 | 30 | false | false | EP-N3 | `NightReservationException` |
| TC-EP4 | 7 | -3 | false | false | EP-A1 | `GuestAgeReservationException` |
| TC-EP5 | 7 | 8 | false | false | EP-A2 | 175.0 |
| TC-EP6 | 7 | 90 | false | false | EP-A4 | 280.0 |
| TC-EP7 | 7 | 30 | true | false | EP-R1 | 340.0 |
| TC-EP8 | 7 | 30 | false | true | EP-V1 | 315.0 |

---

## Step Two: Boundary Values

### TCIs

| ID | Parameter | Boundary | Valid? |
|----|-----------|----------|--------|
| BV-N1 | nights | 0 (min - 1) | Invalid |
| BV-N2 | nights | 1 (min) | Valid |
| BV-N3 | nights | 2 (min + 1) | Valid |
| BV-N4 | nights | 13 (max - 1) | Valid |
| BV-N5 | nights | 14 (max) | Valid |
| BV-N6 | nights | 15 (max + 1) | Invalid |
| BV-A1 | guestAge | -1 (min - 1) | Invalid |
| BV-A2 | guestAge | 0 (min) | Valid |
| BV-A3 | guestAge | 12 (child/adult edge) | Valid |
| BV-A4 | guestAge | 13 (adult lower edge) | Valid |
| BV-A5 | guestAge | 64 (adult/senior edge) | Valid |
| BV-A6 | guestAge | 65 (senior lower edge) | Valid |

### Test Cases

| # | nights | guestAge | resident | veteran | TCI covered | Expected Result |
|---|--------|----------|----------|---------|---------------|------------------|
| TC-BV1 | 0 | 30 | false | false | BV-N1 | `NightReservationException` |
| TC-BV2 | 1 | 30 | false | false | BV-N2 | 50.0 |
| TC-BV3 | 2 | 30 | false | false | BV-N3 | 100.0 |
| TC-BV4 | 13 | 30 | false | false | BV-N4 | 650.0 |
| TC-BV5 | 14 | 30 | false | false | BV-N5 | 700.0 |
| TC-BV6 | 15 | 30 | false | false | BV-N6 | `NightReservationException` |
| TC-BV7 | 7 | -1 | false | false | BV-A1 | `GuestAgeReservationException` |
| TC-BV8 | 7 | 0 | false | false | BV-A2 | 175.0 |
| TC-BV9 | 7 | 12 | false | false | BV-A3 | 175.0 |
| TC-BV10 | 7 | 13 | false | false | BV-A4 | 350.0 |
| TC-BV11 | 7 | 64 | false | false | BV-A5 | 350.0 |
| TC-BV12 | 7 | 65 | false | false | BV-A6 | 280.0 |

---

## Step Three: Decision Table

Conditions: **C1** = isArkansasResident, **C2** = hasVeteranDiscount.
Action: resident discount ($10 flat) is applied first, then the veteran discount (10%) is applied to whatever remains.

| Rule | C1: Resident | C2: Veteran | Action |
|------|:---:|:---:|--------|
| R1 | F | F | No status discount |
| R2 | F | T | price × 0.9 |
| R3 | T | F | price − 10 |
| R4 | T | T | (price − 10) × 0.9 |

### Test Cases
(Held constant: nights = 7, guestAge = 30 → base price before status discounts = 350.0)

| # | Rule | resident | veteran | Expected Result |
|---|------|----------|---------|------------------|
| TC-DT1 | R1 | false | false | 350.0 |
| TC-DT2 | R2 | false | true | 315.0 |
| TC-DT3 | R3 | true | false | 340.0 |
| TC-DT4 | R4 | true | true | 306.0 |

---

## Step Four: Eliminate Duplicates — Master Test Case Table

`TC-EP2` and `TC-DT1` are identical inputs (7, 30, F, F → 350.0); duplicate removed.
`TC-EP1` (deep-invalid nights) is subsumed by the `nights = 0` boundary case; duplicate removed.
`TC-EP6` (age = 90 senior) is subsumed by the `age = 65` senior boundary case, since both exercise the same senior partition and produce the same discount formula; duplicate removed.

| # | nights | guestAge | resident | veteran | Expected Result | Covers |
|---|--------|----------|----------|---------|------------------|--------|
| 1 | 0 | 30 | false | false | `NightReservationException` | nights below min |
| 2 | 15 | 30 | false | false | `NightReservationException` | nights above max |
| 3 | 1 | 30 | false | false | 50.0 | nights min boundary |
| 4 | 2 | 30 | false | false | 100.0 | nights min+1 boundary |
| 5 | 7 | 30 | false | false | 350.0 | nominal / decision rule R1 |
| 6 | 13 | 30 | false | false | 650.0 | nights max-1 boundary |
| 7 | 14 | 30 | false | false | 700.0 | nights max boundary |
| 8 | 7 | -1 | false | false | `GuestAgeReservationException` | age below min |
| 9 | 7 | 0 | false | false | 175.0 | child min boundary |
| 10 | 7 | 12 | false | false | 175.0 | child/adult edge |
| 11 | 7 | 13 | false | false | 350.0 | adult lower edge |
| 12 | 7 | 64 | false | false | 350.0 | adult/senior edge |
| 13 | 7 | 65 | false | false | 280.0 | senior lower edge / EP senior |
| 14 | 7 | 30 | true | false | 340.0 | decision rule R3 |
| 15 | 7 | 30 | false | true | 315.0 | decision rule R2 |
| 16 | 7 | 30 | true | true | 306.0 | decision rule R4 |

16 unique test cases total.
