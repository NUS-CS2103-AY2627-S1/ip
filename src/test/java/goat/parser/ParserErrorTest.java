package goat.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import goat.GoatException;

/**
 * Tests the input Goat has to refuse.
 *
 * Kept apart from ParserTest, which is about reading input that makes sense.
 * These are the cases where the right answer is a complaint, and where a
 * complaint that arrives but says the wrong thing is barely better than none.
 */
public class ParserErrorTest {

    @Test
    public void parse_descriptionWithASeparator_rejected() {
        // The save file separates fields with a bar, so this description would
        // come back missing everything after it.
        GoatException e = assertThrows(GoatException.class, () ->
                        Parser.parse("todo read | book"));
        assertTrue(e.getMessage().contains("'|'"), e.getMessage());
    }

    @Test
    public void parse_deadlineDescriptionWithASeparator_rejected() {
        assertThrows(GoatException.class, () ->
                        Parser.parse("deadline read | book /by 2019-12-02"));
    }

    @Test
    public void parse_eventDescriptionWithASeparator_rejected() {
        assertThrows(GoatException.class, () ->
                        Parser.parse("event a | b /from 2019-08-06 1400 /to 2019-08-06 1600"));
    }

    @Test
    public void parse_separatorInTheDatePart_stillRejectedAsADate() {
        // Not a description problem, so it is the date complaint that should
        // arrive, not the separator one.
        GoatException e = assertThrows(GoatException.class, () ->
                        Parser.parse("deadline x /by 2019-12-02 | 1800"));
        assertTrue(e.getMessage().contains("date"), e.getMessage());
    }

    @Test
    public void parse_deadlineWithTwoByMarkers_rejected() {
        GoatException e = assertThrows(GoatException.class, () ->
                        Parser.parse("deadline x /by 2019-12-02 /by 2019-12-03"));
        // The old behavior blamed the date, which sent the user looking in the
        // wrong place entirely.
        assertTrue(e.getMessage().contains("Only one /by"), e.getMessage());
    }

    @Test
    public void parse_eventWithTwoFromMarkers_rejected() {
        GoatException e = assertThrows(GoatException.class, () ->
                        Parser.parse("event x /from 2019-08-06 /from 2019-08-07 /to 2019-08-08"));
        assertTrue(e.getMessage().contains("Only one /from"), e.getMessage());
    }

    @Test
    public void parse_eventWithTwoToMarkers_rejected() {
        GoatException e = assertThrows(GoatException.class, () ->
                        Parser.parse("event x /from 2019-08-06 /to 2019-08-07 /to 2019-08-08"));
        assertTrue(e.getMessage().contains("Only one /to"), e.getMessage());
    }

    @Test
    public void parse_oneMarkerEach_accepted() {
        assertDoesNotThrow(() -> Parser.parse("deadline x /by 2019-12-02"));
        assertDoesNotThrow(() ->
                        Parser.parse("event x /from 2019-08-06 1400 /to 2019-08-06 1600"));
    }

    @Test
    public void parse_eventEndingBeforeItStarts_rejected() {
        GoatException e = assertThrows(GoatException.class, () ->
                        Parser.parse("event x /from 2019-12-02 1800 /to 2019-08-06 1400"));
        assertTrue(e.getMessage().contains("end after it starts"), e.getMessage());
    }

    @Test
    public void parse_eventEndingWhenItStarts_rejected() {
        // Zero length is the same mistake as backwards, just less obvious.
        assertThrows(GoatException.class, () ->
                        Parser.parse("event x /from 2019-08-06 1400 /to 2019-08-06 1400"));
    }

    @Test
    public void parse_eventEndingOneMinuteAfterItStarts_accepted() {
        assertDoesNotThrow(() ->
                        Parser.parse("event x /from 2019-08-06 1400 /to 2019-08-06 1401"));
    }

    @Test
    public void parse_eventSpanningDays_accepted() {
        assertDoesNotThrow(() -> Parser.parse("event holiday /from 2019-12-24 /to 2019-12-26"));
    }

    @Test
    public void parse_eventComplaint_showsBothTimesTheUserGave() {
        GoatException e = assertThrows(GoatException.class, () ->
                        Parser.parse("event x /from 2019-12-02 1800 /to 2019-08-06 1400"));
        assertTrue(e.getMessage().contains("Dec 02 2019"), e.getMessage());
        assertTrue(e.getMessage().contains("Aug 06 2019"), e.getMessage());
    }

    @Test
    public void parse_impossibleDate_rejected() {
        assertThrows(GoatException.class, () -> Parser.parse("deadline x /by 2019-02-30"));
    }

    @Test
    public void parse_surroundingAndRepeatedSpaces_tolerated() {
        // Extra spacing is a typing slip, not an error worth a complaint.
        assertDoesNotThrow(() -> Parser.parse("   todo    read book   "));
        assertDoesNotThrow(() -> Parser.parse("deadline   x   /by   2019-12-02  "));
    }
}
