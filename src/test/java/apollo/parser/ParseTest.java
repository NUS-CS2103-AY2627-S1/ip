package apollo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import apollo.parser.Parser.Command;
import apollo.task.Deadline;
import apollo.task.Event;

public class ParseTest {

    @Test
    public void parseCommand_differentCapitalisation_returnsCorrectCommand() {
        Parser parser = new Parser();

        Assertions.assertEquals(Command.LIST, parser.parseCommand("LIST"));
        Assertions.assertEquals(Command.LIST, parser.parseCommand("list"));
        Assertions.assertEquals(Command.LIST, parser.parseCommand("liST"));
        Assertions.assertEquals(Command.UNDO, parser.parseCommand("undo"));
    }

    @Test
    public void parseCommand_dateQueryAliasesWithWhitespaceAndMixedCase_returnsCorrectCommands() {
        Parser parser = new Parser();

        Assertions.assertEquals(Command.DUE_TODAY, parser.parseCommand("  dUeToDaY  "));
        Assertions.assertEquals(Command.ONGOING_NOW, parser.parseCommand("OnGoInGnOw"));
        Assertions.assertEquals(Command.DUE_THIS_DATE,
                parser.parseCommand("  DuEtHiSdAtE 25/8/2026  "));
    }

    @Test
    public void parseIndex_userFacingIndex_returnsZeroBasedIndex() {
        Parser parser = new Parser();

        Assertions.assertEquals(0, parser.parseIndex("mark 1"));
        Assertions.assertEquals(2, parser.parseIndex("delete 3"));
    }

    @Test
    public void parseDeadline_validInput_returnsCorrectDeadline() {
        Parser parser = new Parser();

        Deadline deadline = parser.parseDeadline("deadline test /by 30/8/2026 1800");

        Assertions.assertEquals(
                LocalDateTime.of(2026, 8, 30, 18, 0),
                deadline.getBy());
        Assertions.assertEquals(
                "D | 0 | test | 2026-08-30T18:00:00",
                deadline.toFileString());
    }

    @Test
    public void parseDateOnly_deadlineAndEvent_useCommandSpecificDefaultTimes() {
        Parser parser = new Parser();

        Deadline deadline = parser.parseDeadline("deadline submit report /by 30/8/2026");
        Event event = parser.parseEvent("event camp /from 30/8/2026 /to 31/8/2026");

        Assertions.assertEquals(LocalDateTime.of(2026, 8, 30, 23, 59), deadline.getBy());
        Assertions.assertEquals(LocalDateTime.of(2026, 8, 30, 0, 0), event.getFrom());
        Assertions.assertEquals(LocalDateTime.of(2026, 8, 31, 23, 59), event.getTo());
    }

    @Test
    public void parseEvent_endBeforeStart_throwsIllegalArgumentException() {
        Parser parser = new Parser();
        String eventWithReversedTimes =
                "event lecture /from 30/8/2026 1000 /to 30/8/2026 0900";

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                parser.parseEvent(eventWithReversedTimes));
    }

    @Test
    public void parseEvent_equalStartAndEnd_returnsEvent() {
        Parser parser = new Parser();
        LocalDateTime eventTime = LocalDateTime.of(2026, 8, 30, 10, 0);

        Event event = parser.parseEvent("event consultation /from 30/8/2026 1000 /to 30/8/2026 1000");

        Assertions.assertEquals(eventTime, event.getFrom());
        Assertions.assertEquals(eventTime, event.getTo());
    }

    @Test
    public void parseDate_strictCalendarValidation_acceptsOnlyRealDatesAndTimes() {
        Assertions.assertEquals(LocalDate.of(2028, 2, 29), DateParser.parseDate("29/2/2028"));
        Assertions.assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("29/2/2027"));
        Assertions.assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("31/4/2026"));
        Assertions.assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("10/13/2026"));
        Assertions.assertThrows(DateTimeParseException.class, () ->
                DateParser.parseDateTime("25/8/2026 2400", LocalTime.MIDNIGHT));
    }

    @Test
    public void parseDeadline_missingByMarker_throwsException() {
        Parser parser = new Parser();

        Assertions.assertThrows(RuntimeException.class, () ->
                parser.parseDeadline("deadline submit report 30/8/2026"));
    }

    @Test
    public void parseEvent_missingOrMisorderedMarkers_throwsException() {
        Parser parser = new Parser();

        Assertions.assertThrows(RuntimeException.class, () ->
                parser.parseEvent("event lecture /from 30/8/2026"));
        Assertions.assertThrows(RuntimeException.class, () ->
                parser.parseEvent("event lecture /to 30/8/2026 /from 29/8/2026"));
    }

    @Test
    public void parseDeadlineOrEvent_emptyDescription_throwsIllegalArgumentException() {
        Parser parser = new Parser();

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                parser.parseDeadline("deadline /by 30/8/2026"));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                parser.parseEvent("event /from 30/8/2026 /to 31/8/2026"));
    }
}
