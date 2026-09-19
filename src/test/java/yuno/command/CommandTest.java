package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.InvalidTaskNumberException;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

class CommandTest {
    private final TestCommand command = new TestCommand();

    @Test
    void parseInputDate_validDate_returnsDate() throws InvalidCommandFormatException {
        assertEquals(LocalDate.of(2024, 2, 29), command.parseDate("2024-02-29"));
    }

    @Test
    void parseInputDate_invalidDate_throwsInvalidCommandFormatException() {
        InvalidCommandFormatException exception = assertThrows(
                InvalidCommandFormatException.class, () -> command.parseDate("2025-02-29"));

        assertEquals(
                "Memorize the date format before you even type. It's supposed to be yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    void parseInputDateTime_validDateTime_returnsDateTime() throws InvalidCommandFormatException {
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 23, 59),
                command.parseDateTime("2026-08-26 2359"));
    }

    @Test
    void parseInputDateTime_dateOnly_returnsStartOfDay() throws InvalidCommandFormatException {
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 0, 0),
                command.parseDateTime("2026-08-26"));
    }

    @Test
    void parseInputDateTime_invalidInput_throwsInvalidCommandFormatException() {
        InvalidCommandFormatException exception = assertThrows(
                InvalidCommandFormatException.class, () -> command.parseDateTime("26 August 2026"));

        assertEquals(
                "Memorize the date format before you even type. It's either yyyy-MM-dd HHmm or yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    void parseInputDateTime_midnight_returnsMidnight() throws InvalidCommandFormatException {
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 0, 0),
                command.parseDateTime("2026-08-26 0000"));
    }

    @Test
    void parseInputDateTime_invalidTime_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class,
                () -> command.parseDateTime("2026-08-26 2400"));
        assertThrows(
                InvalidCommandFormatException.class,
                () -> command.parseDateTime("2026-08-26 1260"));
        assertThrows(
                InvalidCommandFormatException.class,
                () -> command.parseDateTime("2026-08-26 1200 extra"));
    }

    @Test
    void parseTaskNumber_validInteger_returnsInteger() throws InvalidTaskNumberException {
        assertEquals(42, new TestCommand("42").parseNumber());
    }

    @Test
    void parseTaskNumber_blankOrOverflow_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class, () -> new TestCommand("").parseNumber());
        assertThrows(
                InvalidTaskNumberException.class,
                () -> new TestCommand("2147483648").parseNumber());
    }

    private static class TestCommand extends Command {
        TestCommand() {
            this("");
        }

        TestCommand(String commandArguments) {
            super(commandArguments);
        }

        LocalDate parseDate(String dateText) throws InvalidCommandFormatException {
            return parseInputDate(dateText);
        }

        LocalDateTime parseDateTime(String dateTimeText) throws InvalidCommandFormatException {
            return parseInputDateTime(dateTimeText);
        }

        int parseNumber() throws InvalidTaskNumberException {
            return parseTaskNumber();
        }

        @Override
        public CommandResult execute(TaskList taskList, Ui ui, Storage storage) {
            return CommandResult.CONTINUE;
        }
    }
}
