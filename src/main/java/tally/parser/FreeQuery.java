package tally.parser;

import java.time.LocalDate;

/**
 * What the user asked for with the free command.
 *
 * @param days how many free days in a row are wanted, always at least one.
 * @param earliestDate the earliest day that may be offered.
 */
public record FreeQuery(int days, LocalDate earliestDate) {
}
