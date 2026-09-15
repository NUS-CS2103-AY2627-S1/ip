package bob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.ToDo;

/**
 * Tests command parsing performed by {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();

    /**
     * Tests that a command with an argument is separated correctly.
     *
     * @throws BobException If the valid command is unexpectedly rejected.
     */
    @Test
    void parse_commandWithArgument_commandWordAndArgumentReturned() throws BobException {
        ParsedCommand command = parser.parse("todo read book");

        assertEquals("todo", command.getCommandWord());
        assertEquals("read book", command.getArgument());
    }

    /**
     * Tests that a command without an argument produces an empty argument.
     *
     * @throws BobException If the valid command is unexpectedly rejected.
     */
    @Test
    void parse_commandWithoutArgument_emptyArgumentReturned() throws BobException {
        ParsedCommand command = parser.parse("list");

        assertEquals("list", command.getCommandWord());
        assertEquals("", command.getArgument());
    }

    /**
     * Tests that surrounding and separating whitespace is normalized.
     *
     * @throws BobException If the valid command is unexpectedly rejected.
     */
    @Test
    void parse_commandWithExtraWhitespace_whitespaceNormalized() throws BobException {
        ParsedCommand command = parser.parse("  deadline   return book /by 2026-09-01  ");

        assertEquals("deadline", command.getCommandWord());
        assertEquals("return book /by 2026-09-01", command.getArgument());
    }

    /**
     * Tests that blank user input is rejected.
     */
    @Test
    void parse_blankInput_exceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse("   "));
    }

    /**
     * Tests that null user input is rejected without an unchecked exception.
     */
    @Test
    void parse_nullInput_bobExceptionThrown() {
        assertThrows(BobException.class, () -> parser.parse(null));
    }

    /**
     * Tests that a find command's keyword is returned.
     *
     * @throws BobException If the valid keyword is unexpectedly rejected.
     */
    @Test
    void parseKeyword_keywordPresent_keywordReturned() throws BobException {
        ParsedCommand command = new ParsedCommand("find", "book shelf");

        assertEquals("book shelf", parser.parseKeyword(command));
    }

    /**
     * Tests that a find command without a keyword is rejected.
     */
    @Test
    void parseKeyword_keywordMissing_exceptionThrown() {
        ParsedCommand command = new ParsedCommand("find", "   ");

        assertThrows(BobException.class, () -> parser.parseKeyword(command));
    }

    @Test
    void parseEditDetails_validDescriptionEdit_editDetailsReturned() throws BobException {
        ParsedCommand command = new ParsedCommand("edit", "2 description read two books");

        EditDetails editDetails = parser.parseEditDetails(command);

        assertEquals(2, editDetails.taskNumber());
        assertEquals(EditField.DESCRIPTION, editDetails.field());
        assertEquals("read two books", editDetails.newValue());
    }

    @Test
    void parseEditDetails_missingNewValue_exceptionThrown() {
        ParsedCommand command = new ParsedCommand("edit", "2 description");

        BobException exception = assertThrows(
                BobException.class, () -> parser.parseEditDetails(command));

        assertTrue(exception.getMessage().contains("description (all task types)"));
        assertTrue(exception.getMessage().contains("by (deadline tasks only)"));
        assertTrue(exception.getMessage().contains("from (event tasks only)"));
        assertTrue(exception.getMessage().contains("to (event tasks only)"));
    }

    @Test
    void parseEditDetails_unknownField_exceptionThrown() {
        ParsedCommand command = new ParsedCommand("edit", "2 priority high");

        BobException exception = assertThrows(
                BobException.class, () -> parser.parseEditDetails(command));

        assertTrue(exception.getMessage().contains("Editable fields:"));
    }

    @Test
    void parseEditDetails_nonNumericTaskNumber_editGuidanceShown() {
        ParsedCommand command = new ParsedCommand("edit", "two description read book");

        BobException exception = assertThrows(
                BobException.class, () -> parser.parseEditDetails(command));

        assertTrue(exception.getMessage().contains("Use: edit <task index> <field> <new value>."));
        assertTrue(exception.getMessage().contains("Editable fields:"));
    }

    @Test
    void createEditedTask_completedDeadlineDescriptionChanged_otherDetailsPreserved()
            throws BobException {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 10));
        deadline.markTask();
        EditDetails editDetails = new EditDetails(1, EditField.DESCRIPTION, "return notes");

        Task editedTask = parser.createEditedTask(deadline, editDetails);

        assertNotSame(deadline, editedTask);
        assertTrue(editedTask.isDone());
        assertEquals("D | 1 | return notes | 2026-09-10", editedTask.toDataString());
    }

    @Test
    void createEditedTask_deadlineDueDateChanged_descriptionPreserved() throws BobException {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 10));
        EditDetails editDetails = new EditDetails(1, EditField.DUE_DATE, "2026-09-20");

        Task editedTask = parser.createEditedTask(deadline, editDetails);

        assertEquals("D | 0 | return book | 2026-09-20", editedTask.toDataString());
    }

    @Test
    void createEditedTask_eventStartDateChanged_otherDetailsPreserved() throws BobException {
        Event event = new Event(
                "project meeting", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12));
        EditDetails editDetails = new EditDetails(1, EditField.START_DATE, "2026-09-11");

        Task editedTask = parser.createEditedTask(event, editDetails);

        assertEquals("E | 0 | project meeting | 2026-09-11 | 2026-09-12", editedTask.toDataString());
    }

    @Test
    void createEditedTask_eventEndDateChanged_otherDetailsPreserved() throws BobException {
        Event event = new Event(
                "project meeting", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12));
        EditDetails editDetails = new EditDetails(1, EditField.END_DATE, "2026-09-13");

        Task editedTask = parser.createEditedTask(event, editDetails);

        assertEquals("E | 0 | project meeting | 2026-09-10 | 2026-09-13", editedTask.toDataString());
    }

    @Test
    void createEditedTask_dueDateEditForToDo_exceptionThrown() {
        ToDo toDo = new ToDo("read book");
        EditDetails editDetails = new EditDetails(1, EditField.DUE_DATE, "2026-09-20");

        assertThrows(BobException.class, () -> parser.createEditedTask(toDo, editDetails));
    }

    @Test
    void createEditedTask_invalidDate_exceptionThrown() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 10));
        EditDetails editDetails = new EditDetails(1, EditField.DUE_DATE, "20 September");

        assertThrows(BobException.class, () -> parser.createEditedTask(deadline, editDetails));
    }
}
