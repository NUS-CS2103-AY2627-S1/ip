package quu.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests that each {@link QuuException} builds its user-facing message from its inputs.
 *
 * <p>These messages are what the user actually reads when a command fails, so they are worth
 * pinning down: a careless edit to one of them changes the program's behaviour.
 */
public class ExceptionMessageTest {

    @Test
    public void invalidDate_messageNamesTheOffendingInputAndTheExpectedFormat() {
        assertEquals("'Sunday' is not a valid date. Use yyyy-mm-dd, e.g. 2026-06-06.",
                new InvalidDateException("Sunday").getMessage());
    }

    @Test
    public void invalidIndex_messageQuotesTheOffendingInput() {
        assertEquals("\"abc\" isn't a task number", new InvalidIndexException("abc").getMessage());
    }

    @Test
    public void missingArgument_messageShowsTheExpectedUsage() {
        assertEquals("Invalid format. Please follow this format: todo <task>",
                new MissingArgumentException("todo <task>").getMessage());
    }

    @Test
    public void taskNotFound_messageNamesTheIndexAndTheRangeThatWouldWork() {
        assertEquals("There's no task 5. Your list has 2 tasks, numbered 1 to 2.",
                new TaskNotFoundException(5, 2).getMessage());
    }

    @Test
    public void taskNotFound_singleTask_countReadsAsSingular() {
        assertEquals("There's no task 5. Your list has 1 task, numbered 1 to 1.",
                new TaskNotFoundException(5, 1).getMessage());
    }

    @Test
    public void taskNotFound_emptyList_saysToAddATaskFirst() {
        assertEquals("There's no task 5. Your list is empty, so add a task first.",
                new TaskNotFoundException(5, 0).getMessage());
    }

    @Test
    public void unknownCommand_messageListsWhatIsUnderstood() {
        assertEquals(String.format("I don't know what \"blah\" does.%nI understand: todo, list"),
                new UnknownCommandException("blah", "todo, list").getMessage());
    }

    @Test
    public void unexpectedArgument_messageNamesTheBareCommand() {
        assertEquals("bye takes no arguments. Just type: bye",
                new UnexpectedArgumentException("bye").getMessage());
    }

    @Test
    public void invalidDuration_messageShowsBothDates() {
        assertEquals("2026-08-08 to 2026-08-06 is not a valid duration",
                new InvalidDurationException(LocalDate.parse("2026-08-08"),
                        LocalDate.parse("2026-08-06")).getMessage());
    }

    @Test
    public void everyFailure_isCatchableAsASingleQuuException() {
        assertInstanceOf(QuuException.class, new InvalidDateException("Sunday"));
        assertInstanceOf(QuuException.class, new InvalidIndexException("abc"));
        assertInstanceOf(QuuException.class, new MissingArgumentException("todo <task>"));
        assertInstanceOf(QuuException.class, new TaskNotFoundException(5, 2));
        assertInstanceOf(QuuException.class, new UnknownCommandException("blah", "todo, list"));
        assertInstanceOf(QuuException.class, new InvalidFileContents("corrupted line"));
        assertInstanceOf(QuuException.class, new UnexpectedArgumentException("bye"));
        assertInstanceOf(QuuException.class, new SaveFileException("is a folder"));
    }
}
