package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parseTodoDescription_validInput_returnsDescription() throws AliceException {
        String description = Parser.parseTodoDescription("todo read book");
        assertEquals("read book", description);
    }

    @Test
    public void parseTodoDescription_emptyDescription_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseTodoDescription("todo"));
    }

    @Test
    public void parseTodoDescription_onlyWhitespace_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseTodoDescription("todo    "));
    }

    @Test
    public void parseDeadline_validInput_returnsCorrectDeadline() throws AliceException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2/12/2019 1800");
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    public void parseDeadline_missingByKeyword_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseDeadline("deadline return book"));
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseDeadline("deadline  /by 2/12/2019 1800"));
    }

    @Test
    public void parseDeadline_invalidDateFormat_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseDeadline("deadline return book /by tomorrow"));
    }

    @Test
    public void parseEvent_validInput_returnsCorrectEvent() throws AliceException {
        Event event = Parser.parseEvent("event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), event.getTo());
    }

    @Test
    public void parseEvent_missingFromKeyword_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseEvent("event project meeting /to 2/12/2019 1600"));
    }

    @Test
    public void parseEvent_missingToKeyword_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseEvent("event project meeting /from 2/12/2019 1400"));
    }

    @Test
    public void parseIndexArgument_validInput_returnsZeroBasedIndex() throws AliceException {
        int zeroBasedIndex = Parser.parseIndexArgument("mark 3", "mark");
        assertEquals(2, zeroBasedIndex);
    }

    @Test
    public void parseIndexArgument_nonNumericInput_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseIndexArgument("mark abc", "mark"));
    }

    @Test
    public void parseIndexArgument_missingNumber_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseIndexArgument("mark", "mark"));
    }

    @Test
    public void parseIndexArgument_onlyWhitespaceAfterCommand_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseIndexArgument("delete   ", "delete"));
    }

    @Test
    public void parseEvent_toBeforeFrom_exceptionThrown() {
        String input = "event meeting /to 2/12/2019 1600 /from 2/12/2019 1400";
        assertThrows(AliceException.class, () -> Parser.parseEvent(input));
    }

    @Test
    public void parseEvent_endsBeforeItStarts_exceptionThrown() {
        String input = "event meeting /from 2/12/2019 1600 /to 2/12/2019 1400";
        assertThrows(AliceException.class, () -> Parser.parseEvent(input));
    }

    @Test
    public void parseEvent_emptyDescription_exceptionThrown() {
        String input = "event /from 2/12/2019 1400 /to 2/12/2019 1600";
        assertThrows(AliceException.class, () -> Parser.parseEvent(input));
    }

    @Test
    public void parseEvent_blankFromDate_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseEvent("event meeting /from  /to 2/12/2019 1600"));
    }

    @Test
    public void parseEvent_slashToInsideDescription_parsesCorrectly() throws AliceException {
        Event event = Parser.parseEvent("event walk /to the shop /from 2/12/2019 1400 /to 2/12/2019 1600");
        assertEquals("walk /to the shop", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getFrom());
    }

    @Test
    public void parseSnooze_validInput_returnsIndexAndDays() throws AliceException {
        int[] snoozeArgs = Parser.parseSnooze("snooze 2 3");
        assertEquals(1, snoozeArgs[0]);
        assertEquals(3, snoozeArgs[1]);
    }

    @Test
    public void parseSnooze_missingDays_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseSnooze("snooze 2"));
    }

    @Test
    public void parseSnooze_extraArgument_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseSnooze("snooze 2 3 4"));
    }

    @Test
    public void parseSnooze_noArguments_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseSnooze("snooze"));
    }

    @Test
    public void parseSnooze_nonPositiveDays_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseSnooze("snooze 2 0"));
    }

    @Test
    public void parseSnooze_nonNumericDays_exceptionThrown() {
        assertThrows(AliceException.class, () -> Parser.parseSnooze("snooze 2 abc"));
    }
}
