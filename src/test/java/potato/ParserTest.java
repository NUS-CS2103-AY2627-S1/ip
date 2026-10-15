package potato.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import potato.command.AddCommand;
import potato.command.ExitCommand;
import potato.command.ListCommand;
import potato.exception.PotatoException;

/**
 * Contains unit tests for testing input parsing in the {@link Parser} class.
 */
public class ParserTest {

    /**
     * Tests that a valid 'todo' command string parses into an {@code AddCommand}.
     *
     * @throws PotatoException If parsing fails unexpectedly.
     */
    @Test
    public void parseTodoCommandSuccess() throws PotatoException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    /**
     * Tests that 'bye' parses into an {@code ExitCommand}.
     *
     * @throws PotatoException If parsing fails unexpectedly.
     */
    @Test
    public void parseByeCommandSuccess() throws PotatoException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    /**
     * Tests that 'list' parses into a {@code ListCommand}.
     *
     * @throws PotatoException If parsing fails unexpectedly.
     */
    @Test
    public void parseListCommandSuccess() throws PotatoException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    /**
     * Tests that entering 'todo' with no description throws a {@code PotatoException}.
     */
    @Test
    public void parseEmptyTodoDescriptionExceptionThrown() {
        assertThrows(PotatoException.class, () -> Parser.parse("todo "));
    }

    /**
     * Tests that an unrecognized command word throws a {@code PotatoException}.
     */
    @Test
    public void parseUnknownCommandExceptionThrown() {
        assertThrows(PotatoException.class, () -> Parser.parse("invalidCommand"));
    }

    /**
     * Tests that a deadline with a valid ISO date parses into an {@code AddCommand}.
     *
     * @throws PotatoException If parsing fails unexpectedly.
     */
    @Test
    public void parseDeadlineWithValidDateSuccess() throws PotatoException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2026-09-15"));
    }

    /**
     * Tests that a deadline with free-text date input is rejected.
     */
    @Test
    public void parseDeadlineWithInvalidDateFormatExceptionThrown() {
        assertThrows(PotatoException.class, () -> Parser.parse("deadline return book /by hello"));
    }

    /**
     * Tests that impossible calendar dates are rejected.
     */
    @Test
    public void parseDeadlineWithImpossibleDateExceptionThrown() {
        assertThrows(PotatoException.class, () -> Parser.parse("deadline return book /by 2026-02-31"));
    }

    /**
     * Tests that an event with valid ISO date-time arguments parses into an {@code AddCommand}.
     *
     * @throws PotatoException If parsing fails unexpectedly.
     */
    @Test
    public void parseEventWithValidDateTimesSuccess() throws PotatoException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event project meeting /from 2026-09-15 1400 /to 2026-09-15 16:00"));
    }

    /**
     * Tests that an event with free-text start time is rejected.
     */
    @Test
    public void parseEventWithInvalidStartDateFormatExceptionThrown() {
        assertThrows(PotatoException.class,
                () -> Parser.parse("event project meeting /from sunday /to 2026-09-15"));
    }

    /**
     * Tests that an event with an impossible end date is rejected.
     */
    @Test
    public void parseEventWithImpossibleEndDateExceptionThrown() {
        assertThrows(PotatoException.class,
                () -> Parser.parse("event project meeting /from 2026-09-15 /to 2026-02-31"));
    }
}
