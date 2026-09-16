package walter.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import walter.DukeException;
import walter.command.AddCommand;
import walter.command.AddPlaceCommand;
import walter.command.DeleteCommand;
import walter.command.DeletePlaceCommand;
import walter.command.ExitCommand;
import walter.command.FindCommand;
import walter.command.ListCommand;
import walter.command.ListPlacesCommand;
import walter.command.MarkCommand;
import walter.command.OnCommand;
import walter.command.UnmarkCommand;

/**
 * Tests command recognition and validation performed by {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parse_validTodo_addCommandReturned() throws DukeException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_commandWithMixedWhitespace_commandAndArgumentRecognised() throws DukeException {
        assertInstanceOf(AddCommand.class, Parser.parse("\t todo\tread book   "));
        assertInstanceOf(MarkCommand.class, Parser.parse("\nmark\t1 "));
        assertInstanceOf(FindCommand.class, Parser.parse("  find\tbook\tclub  "));
    }

    @Test
    public void parse_emptyTodo_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_validDeadline_addCommandReturned() throws DukeException {
        assertInstanceOf(
                AddCommand.class,
                Parser.parse("deadline submit report /by 2026-08-30"));
    }

    @Test
    public void parse_validLeapDate_addCommandReturned() throws DukeException {
        assertInstanceOf(
                AddCommand.class,
                Parser.parse("deadline submit report /by 2028-02-29"));
        assertInstanceOf(
                AddCommand.class,
                Parser.parse("deadline far future /by 9999-12-31"));
    }

    @Test
    public void parse_invalidDeadlineDates_exceptionThrown() {
        DukeException nonexistentDate = assertThrows(DukeException.class, () ->
                Parser.parse("deadline report /by 2027-02-29"));
        DukeException malformedDate = assertThrows(DukeException.class, () ->
                Parser.parse("deadline report /by 2027-2-29"));
        assertEquals("Deadline date is not a valid calendar date.", nonexistentDate.getMessage());
        assertEquals("Deadline date must be in yyyy-MM-dd format.", malformedDate.getMessage());
        assertThrows(DukeException.class, () -> Parser.parse("deadline report /by 2026-02-30"));
        assertThrows(DukeException.class, () -> Parser.parse("deadline report /by 2026-04-31"));
        assertThrows(DukeException.class, () -> Parser.parse("deadline report /by 2026-01-00"));
        assertThrows(DukeException.class, () -> Parser.parse("deadline report /by +2026-01-01"));
        assertThrows(DukeException.class, () -> Parser.parse("deadline report /by 10000-01-01"));
        assertThrows(DukeException.class, () -> Parser.parse("deadline report /by 0000-01-01"));
        assertThrows(DukeException.class, () -> Parser.parse("deadline report /by -0001-01-01"));
    }

    @Test
    public void parse_duplicateDeadlineDelimiter_specificExceptionThrown() {
        String input = "deadline report /by 2026-08-30 /by 2026-09-01";

        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse(input));

        assertEquals("Deadline requires exactly one /by.", exception.getMessage());
    }

    @Test
    public void parse_validAtEvent_addCommandReturned() throws DukeException {
        assertInstanceOf(AddCommand.class, Parser.parse("event meeting /at 3pm"));
        assertInstanceOf(AddCommand.class, Parser.parse("event atlas meeting /at noon"));
    }

    @Test
    public void parse_validFromToEvent_addCommandReturned() throws DukeException {
        assertInstanceOf(
                AddCommand.class,
                Parser.parse("event meeting /from 2pm /to 4pm"));
    }

    @Test
    public void parse_isoDateEventRanges_orderValidated() throws DukeException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event trip /from 2026-12-10 /to 2026-12-20"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event release /from 2026-12-10 /to 2026-12-10"));

        DukeException exception = assertThrows(DukeException.class, () ->
                Parser.parse("event trip /from 2026-12-20 /to 2026-12-10"));
        assertEquals("Event start must not be after its end.", exception.getMessage());
    }

    @Test
    public void parse_datedTimeEventRanges_orderValidatedWithoutRejectingFreeText()
            throws DukeException {
        assertInstanceOf(AddCommand.class, Parser.parse(
                "event overnight /from 2026-12-10 23:00 /to 2026-12-11 01:00"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event free text /from Monday 11pm /to Tuesday 1am"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event ambiguous overnight /from 11pm /to 1am"));

        DukeException reversed = assertThrows(DukeException.class, () -> Parser.parse(
                "event meeting /from 2026-12-10 4pm /to 2026-12-10 2pm"));
        assertEquals("Event start must not be after its end.", reversed.getMessage());
    }

    @Test
    public void parse_malformedEvents_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("event meeting"));
        assertThrows(DukeException.class, () -> Parser.parse("event meeting /from 2pm"));
        assertThrows(DukeException.class, () -> Parser.parse("event meeting /to 4pm"));
        assertThrows(DukeException.class, () -> Parser.parse("event meeting /at"));
        assertThrows(DukeException.class, () -> Parser.parse("event x /atlas noon"));
        assertThrows(DukeException.class, () -> Parser.parse("event x /fromage a /tofu b"));
    }

    @Test
    public void parse_conflictingOrRepeatedEventDelimiters_specificExceptionThrown() {
        String mixedFormat = "event meeting /at 2pm /from 2pm /to 4pm";
        String repeatedDelimiter = "event meeting /from 2pm /to 4pm /to 5pm";
        String reversedDelimiters = "event meeting /to 4pm /from 2pm";

        DukeException mixedFormatException = assertThrows(
                DukeException.class, () -> Parser.parse(mixedFormat));
        DukeException repeatedDelimiterException = assertThrows(
                DukeException.class, () -> Parser.parse(repeatedDelimiter));
        DukeException reversedDelimiterException = assertThrows(
                DukeException.class, () -> Parser.parse(reversedDelimiters));

        assertEquals("Event must use either one /at or one /from and one /to.",
                mixedFormatException.getMessage());
        assertEquals("Event requires exactly one /from and one /to.",
                repeatedDelimiterException.getMessage());
        assertEquals("Event /from must appear before /to.",
                reversedDelimiterException.getMessage());
    }

    @Test
    public void parse_listCommand_listCommandReturned() throws DukeException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_byeCommand_exitCommandReturned() throws DukeException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_markAndDoneCommands_markCommandReturned() throws DukeException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("done 2"));
    }

    @Test
    public void parse_unmarkCommand_unmarkCommandReturned() throws DukeException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_deleteCommand_deleteCommandReturned() throws DukeException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_validOnDate_onCommandReturned() throws DukeException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2026-08-30"));
    }

    @Test
    public void parse_validFind_findCommandReturned() throws DukeException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_validPlace_addPlaceCommandReturned() throws DukeException {
        assertInstanceOf(
                AddPlaceCommand.class,
                Parser.parse("place Alex's home /at 123 Clementi Ave 3"));
    }

    @Test
    public void parse_invalidPlaceDetails_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("place"));
        assertThrows(DukeException.class, () -> Parser.parse("place Alex's home"));
        assertThrows(DukeException.class, () -> Parser.parse("place /at 123 Clementi Ave 3"));
        assertThrows(DukeException.class, () -> Parser.parse("place Alex's home /at"));
        assertThrows(DukeException.class, () ->
                Parser.parse("place home /at first /at second"));
        assertThrows(DukeException.class, () -> Parser.parse("place a/at b"));
        assertThrows(DukeException.class, () -> Parser.parse("place a /atb"));
        assertThrows(DukeException.class, () -> Parser.parse("place a /atlas b"));
    }

    @Test
    public void parse_placesCommand_listPlacesCommandReturned() throws DukeException {
        assertInstanceOf(ListPlacesCommand.class, Parser.parse("places"));
    }

    @Test
    public void parse_deletePlaceCommand_deletePlaceCommandReturned() throws DukeException {
        assertInstanceOf(DeletePlaceCommand.class, Parser.parse("deleteplace 1"));
    }

    @Test
    public void parse_invalidDeletePlaceIndex_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("deleteplace"));
        assertThrows(DukeException.class, () -> Parser.parse("deleteplace abc"));
    }

    @Test
    public void parse_missingFindKeyword_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("find"));
        assertThrows(DukeException.class, () -> Parser.parse("find   "));
    }

    @Test
    public void parse_invalidOnDate_exceptionThrown() {
        DukeException nonexistentDate = assertThrows(DukeException.class, () ->
                Parser.parse("on 2026-02-30"));
        DukeException malformedDate = assertThrows(DukeException.class, () ->
                Parser.parse("on 2026-2-28"));
        assertEquals("Date is not a valid calendar date.", nonexistentDate.getMessage());
        assertEquals("Date must be in yyyy-MM-dd format.", malformedDate.getMessage());
        assertThrows(DukeException.class, () -> Parser.parse("on +2026-01-01"));
        assertThrows(DukeException.class, () -> Parser.parse("on 10000-01-01"));
        assertThrows(DukeException.class, () -> Parser.parse("on 0000-01-01"));
        assertThrows(DukeException.class, () -> Parser.parse("on -0001-01-01"));
    }

    @Test
    public void parse_missingTaskNumbers_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("mark"));
        assertThrows(DukeException.class, () -> Parser.parse("done"));
        assertThrows(DukeException.class, () -> Parser.parse("unmark"));
        assertThrows(DukeException.class, () -> Parser.parse("delete"));
    }

    @Test
    public void parse_nonIntegerTaskNumbers_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("mark first"));
        assertThrows(DukeException.class, () -> Parser.parse("unmark 1.5"));
        assertThrows(DukeException.class, () -> Parser.parse("delete two"));
    }

    @Test
    public void parse_blankInput_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse(""));
        assertThrows(DukeException.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_nullInput_applicationExceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parse(null));

        assertEquals("Command cannot be null.", exception.getMessage());
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("archive book"));
    }

    @Test
    public void parse_argumentSuppliedToArgumentlessCommand_unknownCommandExceptionThrown() {
        assertThrows(DukeException.class, () -> Parser.parse("list now"));
        assertThrows(DukeException.class, () -> Parser.parse("places now"));
        assertThrows(DukeException.class, () -> Parser.parse("bye now"));
    }
}
