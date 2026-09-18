package altair.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import altair.AltairException;
import altair.task.Task;

/**
 * Unit tests for {@link Parser}.
 *
 * <p>{@code Parser}'s methods are pure functions of their arguments, so each
 * test is a direct "given this command text, expect this task (or error)"
 * check, without needing an {@code Altair} instance or a save file.</p>
 */
public class ParserTest {

    // ----- parseTask -----

    @Test
    public void parseTask_todoCommand_returnsTodoWithDescription() throws AltairException {
        Task task = Parser.parseTask("todo borrow book");
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    public void parseTask_todoWithoutDescription_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class, () -> Parser.parseTask("todo"));
        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineCommand_returnsDeadlineWithParsedDate() throws AltairException {
        Task task = Parser.parseTask("deadline return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", task.toString());
    }

    @Test
    public void parseTask_deadlineWithoutByMarker_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class,
                () -> Parser.parseTask("deadline return book"));
        assertEquals("A deadline needs a date after /by.", exception.getMessage());
    }

    @Test
    public void parseTask_deadlineWithUnparseableDate_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class,
                () -> Parser.parseTask("deadline return book /by tomorrow"));
        assertEquals("A deadline date must use yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseTask_eventCommand_returnsEventWithBothDates() throws AltairException {
        Task task = Parser.parseTask("event camp /from 2019-10-16 /to 2019-10-17");
        assertEquals("[E][ ] camp (from: Oct 16 2019 to: Oct 17 2019)", task.toString());
    }

    @Test
    public void parseTask_eventFromEqualsTo_returnsSameDayEvent() throws AltairException {
        Task task = Parser.parseTask("event workshop /from 2019-10-15 /to 2019-10-15");
        assertEquals("[E][ ] workshop (from: Oct 15 2019 to: Oct 15 2019)", task.toString());
    }

    @Test
    public void parseTask_eventFromAfterTo_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class,
                () -> Parser.parseTask("event camp /from 2019-10-17 /to 2019-10-16"));
        assertEquals("An event's start date cannot be after its end date.", exception.getMessage());
    }

    @Test
    public void parseTask_descriptionWithPipe_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class,
                () -> Parser.parseTask("todo bad | data"));
        assertEquals("Task details cannot contain the '|' character.", exception.getMessage());
    }

    @Test
    public void parseTask_unknownCommand_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class, () -> Parser.parseTask("blah"));
        assertEquals("I do not understand your command. Try again, perhaps?", exception.getMessage());
    }

    // ----- textAfterCommand -----

    @Test
    public void textAfterCommand_removesCommandWordAndTrims() {
        assertEquals("book", Parser.textAfterCommand("find   book", "find"));
    }

    // ----- parseTaskNumber -----

    @Test
    public void parseTaskNumber_validNumber_returnsIt() throws AltairException {
        assertEquals(2, Parser.parseTaskNumber("mark 2", "mark"));
    }

    @Test
    public void parseTaskNumber_nonNumericArgument_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class,
                () -> Parser.parseTaskNumber("mark two", "mark"));
        assertEquals("Please use a valid task number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_extraArgument_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class,
                () -> Parser.parseTaskNumber("mark 1 2", "mark"));
        assertEquals("Please use: mark <task number>.", exception.getMessage());
    }

    // ----- ensureNoArguments -----

    @Test
    public void ensureNoArguments_bareCommand_doesNotThrow() {
        assertDoesNotThrow(() -> Parser.ensureNoArguments("list", "list"));
    }

    @Test
    public void ensureNoArguments_withArgument_throwsAltairException() {
        AltairException exception = assertThrows(AltairException.class,
                () -> Parser.ensureNoArguments("list now", "list"));
        assertEquals("Please use: list.", exception.getMessage());
    }
}
