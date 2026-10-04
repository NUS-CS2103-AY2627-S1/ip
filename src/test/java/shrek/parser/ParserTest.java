package shrek.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import shrek.CommandType;
import shrek.ShrekException;
import shrek.task.Deadline;
import shrek.task.Event;
import shrek.task.Todo;

public class ParserTest {

    @Test
    public void parseCommand_normalizesWhitespace() {
        assertEquals(CommandType.TODO, Parser.parseCommandType("  todo   read   book  "));
        assertEquals("read book", Parser.parseArgs("  todo   read   book  "));
    }

    @Test
    public void parseTodo_validDescription_createsTodo() throws ShrekException {
        Todo todo = Parser.parseTodo("read book");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    public void parseTodo_trailingTags_extractsAndNormalizesTags() throws ShrekException {
        Todo todo = Parser.parseTodo("play game #Weekend #fun");
        assertEquals("play game", todo.getDescription());
        assertEquals(java.util.List.of("#fun", "#weekend"), todo.getTags());
    }

    @Test
    public void parseTodo_invalidTag_rejectsWholeCommand() {
        assertThrows(ShrekException.class, () -> Parser.parseTodo("play game #invalid!"));
    }

    @Test
    public void parseTodo_duplicateTags_rejectsWholeCommand() {
        assertThrows(ShrekException.class, () -> Parser.parseTodo("play game #fun #FUN"));
    }

    @Test
    public void parseTodo_moreThanThreeTags_rejectsWholeCommand() {
        assertThrows(ShrekException.class, () -> Parser.parseTodo("play game #one #two #three #four"));
    }

    @Test
    public void parseTags_multipleTags_returnsNormalizedTags() throws ShrekException {
        assertEquals(java.util.List.of("#fun", "#school"), Parser.parseTags("#FUN #school"));
    }

    @Test
    public void parseTodo_emptyDescription_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_validInput_createsDeadlineWithCorrectDate() throws ShrekException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_trailingTag_keepsDateParsingIntact() throws ShrekException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15 #school");
        assertEquals(java.util.List.of("#school"), deadline.getTags());
    }

    @Test
    public void parseEvent_trailingTag_keepsTimeParsingIntact() throws ShrekException {
        Event event = Parser.parseEvent("party /from 2pm /to 4pm #fun");
        assertEquals(java.util.List.of("#fun"), event.getTags());
    }

    @Test
    public void parseDeadline_missingByKeyword_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_invalidDateFormat_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseDeadline("return book /by tomorrow"));
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws ShrekException {
        int index = Parser.parseTaskIndex("2", "mark", 5);
        assertEquals(1, index);
    }

    @Test
    public void parseTaskIndex_outOfRange_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseTaskIndex("99", "mark", 5));
    }

    @Test
    public void parseTaskIndex_nonNumeric_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseTaskIndex("abc", "mark", 5));
    }

    @Test
    public void parseTaskIndex_multipleNumbers_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseTaskIndex("1 2", "mark", 5));
    }

    @Test
    public void parseDeadline_duplicateByMarker_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseDeadline(
                "return book /by 2019-10-15 /by 2019-10-16"));
    }

    @Test
    public void parseEvent_duplicateMarkers_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseEvent(
                "party /from 2pm /from 3pm /to 4pm"));
        assertThrows(ShrekException.class, () -> Parser.parseEvent(
                "party /from 2pm /to 4pm /to 5pm"));
    }

    @Test
    public void parseEvent_invalidOrNonIncreasingTimes_throwsShrekException() {
        assertThrows(ShrekException.class, () -> Parser.parseEvent(
                "party /from 25:00 /to 26:00"));
        assertThrows(ShrekException.class, () -> Parser.parseEvent(
                "party /from 4pm /to 2pm"));
        assertThrows(ShrekException.class, () -> Parser.parseEvent(
                "party /from 2pm /to 2pm"));
    }

    @Test
    public void parseEvent_isoDateTimes_areAccepted() throws ShrekException {
        Event event = Parser.parseEvent(
                "party /from 2026-09-13T14:00 /to 2026-09-13T15:00");
        assertEquals("party", event.getDescription());
    }
}
