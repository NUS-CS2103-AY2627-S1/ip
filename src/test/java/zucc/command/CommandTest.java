package zucc.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import zucc.ZuccException;
import zucc.task.Task;

/**
 * Tests command parsing and the validation shared by concrete commands.
 */
public class CommandTest {
    /**
     * Verifies that every supported command keyword produces its matching command type.
     */
    @Test
    public void parse_knownKeywords_returnsMatchingCommandTypes() {
        assertAll(
                () -> assertInstanceOf(TodoCommand.class, Command.parse("todo read")),
                () -> assertInstanceOf(DeadlineCommand.class,
                        Command.parse("deadline submit /by 1/9/2026 1800")),
                () -> assertInstanceOf(EventCommand.class,
                        Command.parse("event meeting /from 1/9/2026 1800 /to 1/9/2026 1900")),
                () -> assertInstanceOf(ListCommand.class, Command.parse("list")),
                () -> assertInstanceOf(FindCommand.class, Command.parse("find book")),
                () -> assertInstanceOf(OnCommand.class, Command.parse("on 1/9/2026")),
                () -> assertInstanceOf(MarkCommand.class, Command.parse("mark 1")),
                () -> assertInstanceOf(UnmarkCommand.class, Command.parse("unmark 1")),
                () -> assertInstanceOf(DeleteCommand.class, Command.parse("delete 1")),
                () -> assertInstanceOf(ExitCommand.class, Command.parse("bye")));
    }

    /**
     * Verifies that unknown and blank command keywords are rejected.
     */
    @Test
    public void parse_unknownOrBlankCommand_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class, () -> Command.parse("archive 1")),
                () -> assertThrows(ZuccException.class, () -> Command.parse("   ")));
    }

    /**
     * Verifies that parsing preserves repeated spaces within a to-do description.
     *
     * @throws ZuccException if the command or resulting task cannot be created.
     */
    @Test
    public void parse_todoWithRepeatedSpaces_spacesPreservedInDescription()
            throws ZuccException {
        TodoCommand command = assertInstanceOf(
                TodoCommand.class, Command.parse("  todo read  chapter  "));

        assertEquals("[T][ ] read  chapter", command.createTask().toString());
    }

    /**
     * Verifies that a slash within an argument word remains part of the description.
     *
     * @throws ZuccException if the command or resulting task cannot be created.
     */
    @Test
    public void parse_slashInsideArgumentWord_slashPreservedInDescription()
            throws ZuccException {
        TodoCommand command = assertInstanceOf(
                TodoCommand.class,
                Command.parse("todo review input/output handling"));

        assertEquals(
                "[T][ ] review input/output handling",
                command.createTask().toString());
    }

    /**
     * Verifies that a slash without an option name remains ordinary argument text.
     *
     * @throws ZuccException if the command or resulting task cannot be created.
     */
    @Test
    public void parse_standaloneSlash_slashPreservedInDescription() throws ZuccException {
        TodoCommand command = assertInstanceOf(
                TodoCommand.class, Command.parse("todo review / notes"));

        assertEquals("[T][ ] review / notes", command.createTask().toString());
    }

    /**
     * Verifies that a deadline command and its {@code /by} option create the expected task.
     *
     * @throws ZuccException if the valid command or task cannot be created.
     */
    @Test
    public void parse_deadlineWithOption_taskCreated() throws ZuccException {
        DeadlineCommand command = assertInstanceOf(
                DeadlineCommand.class,
                Command.parse("deadline submit report /by 2/9/2026 1800"));

        Task task = command.createTask();

        assertEquals("[D][ ] submit report (by: Sep 02 2026, 6:00PM)", task.toString());
    }

    /**
     * Verifies that event options create the same task regardless of their input order.
     *
     * @throws ZuccException if either valid command or task cannot be created.
     */
    @Test
    public void parse_eventOptionsInEitherOrder_tasksCreated() throws ZuccException {
        EventCommand usualOrder = assertInstanceOf(
                EventCommand.class,
                Command.parse("event workshop /from 2/9/2026 1800 /to 2/9/2026 2000"));
        EventCommand reversedOrder = assertInstanceOf(
                EventCommand.class,
                Command.parse("event workshop /to 2/9/2026 2000 /from 2/9/2026 1800"));

        assertEquals(usualOrder.createTask().toString(), reversedOrder.createTask().toString());
    }

    /**
     * Verifies that each add command accepts the shared optional priority in either notation.
     *
     * @throws ZuccException if a valid command or task cannot be created.
     */
    @Test
    public void createTask_optionalPriority_priorityDisplayedForEveryTaskType()
            throws ZuccException {
        TodoCommand todo = assertInstanceOf(
                TodoCommand.class,
                Command.parse("todo read chapter /priority high"));
        DeadlineCommand deadline = assertInstanceOf(
                DeadlineCommand.class,
                Command.parse("deadline submit /priority 2 /by 2/9/2026 1800"));
        EventCommand event = assertInstanceOf(
                EventCommand.class,
                Command.parse(
                        "event workshop /from 2/9/2026 1800 "
                                + "/to 2/9/2026 2000 /priority low"));

        assertAll(
                () -> assertEquals("[T][ ][P1] read chapter", todo.createTask().toString()),
                () -> assertEquals(
                        "[D][ ][P2] submit (by: Sep 02 2026, 6:00PM)",
                        deadline.createTask().toString()),
                () -> assertEquals(
                        "[E][ ][P3] workshop "
                                + "(from: Sep 02 2026, 6:00PM to: Sep 02 2026, 8:00PM)",
                        event.createTask().toString()));
    }

    /**
     * Verifies that omitting priority preserves the original task display.
     *
     * @throws ZuccException if the valid command or task cannot be created.
     */
    @Test
    public void createTask_priorityOmitted_taskHasNoPriorityMarker() throws ZuccException {
        TodoCommand command = assertInstanceOf(
                TodoCommand.class, Command.parse("todo read chapter"));

        assertEquals("[T][ ] read chapter", command.createTask().toString());
    }

    /**
     * Verifies that a blank or unsupported priority is rejected when the task is created.
     *
     * @throws ZuccException if parsing fails before priority validation.
     */
    @Test
    public void createTask_invalidPriority_exceptionThrown() throws ZuccException {
        TodoCommand blankPriority = assertInstanceOf(
                TodoCommand.class, Command.parse("todo read /priority"));
        TodoCommand unsupportedPriority = assertInstanceOf(
                TodoCommand.class, Command.parse("todo read /priority urgent"));

        assertAll(
                () -> assertThrows(ZuccException.class, blankPriority::createTask),
                () -> assertThrows(ZuccException.class, unsupportedPriority::createTask));
    }

    /**
     * Verifies that supplying the same named option twice is rejected.
     */
    @Test
    public void parse_duplicateOption_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> Command.parse(
                        "deadline submit /by 2/9/2026 1800 /by 3/9/2026 1800"));
    }

    /**
     * Verifies that a command rejects named options it does not support.
     */
    @Test
    public void parse_optionUnsupportedByCommand_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> Command.parse("todo read /by 2/9/2026 1800"));
    }

    /**
     * Verifies that task creation rejects commands missing required arguments or options.
     *
     * @throws ZuccException if parsing fails before the missing values can be tested.
     */
    @Test
    public void createTask_requiredArgumentOrOptionMissing_exceptionThrown()
            throws ZuccException {
        TodoCommand missingDescription = assertInstanceOf(
                TodoCommand.class, Command.parse("todo"));
        DeadlineCommand missingDeadline = assertInstanceOf(
                DeadlineCommand.class, Command.parse("deadline submit"));
        EventCommand missingEnd = assertInstanceOf(
                EventCommand.class,
                Command.parse("event workshop /from 2/9/2026 1800"));

        assertAll(
                () -> assertThrows(ZuccException.class, missingDescription::createTask),
                () -> assertThrows(ZuccException.class, missingDeadline::createTask),
                () -> assertThrows(ZuccException.class, missingEnd::createTask));
    }

    /**
     * Verifies that a one-based task number is converted to its zero-based list index.
     *
     * @throws ZuccException if the valid task number cannot be parsed.
     */
    @Test
    public void parseTaskIndex_validOneBasedNumber_returnsZeroBasedIndex()
            throws ZuccException {
        MarkCommand command = assertInstanceOf(MarkCommand.class, Command.parse("mark 3"));

        assertEquals(2, command.parseTaskIndex());
    }

    /**
     * Verifies that absent, nonnumeric, and overflowing task numbers are rejected.
     *
     * @throws ZuccException if command creation fails before the task numbers can be tested.
     */
    @Test
    public void parseTaskIndex_missingMalformedOrTooLargeNumber_exceptionThrown()
            throws ZuccException {
        MarkCommand missing = assertInstanceOf(MarkCommand.class, Command.parse("mark"));
        MarkCommand malformed = assertInstanceOf(MarkCommand.class, Command.parse("mark two"));
        MarkCommand tooLargeForInteger = assertInstanceOf(
                MarkCommand.class, Command.parse("mark 999999999999999999999"));

        assertAll(
                () -> assertThrows(ZuccException.class, missing::parseTaskIndex),
                () -> assertThrows(ZuccException.class, malformed::parseTaskIndex),
                () -> assertThrows(ZuccException.class, tooLargeForInteger::parseTaskIndex));
    }

    /**
     * Verifies that a command which accepts no argument rejects unexpected text.
     *
     * @throws ZuccException if command creation fails before validation can be tested.
     */
    @Test
    public void requireNoArgument_argumentSupplied_exceptionThrown() throws ZuccException {
        ListCommand command = assertInstanceOf(
                ListCommand.class, Command.parse("list unexpected"));

        assertThrows(ZuccException.class, command::requireNoArgument);
    }

    /**
     * Verifies that only the exit command reports that the application should stop.
     *
     * @throws ZuccException if either valid command cannot be parsed.
     */
    @Test
    public void isExit_exitAndRegularCommands_returnsExpectedValues() throws ZuccException {
        assertTrue(Command.parse("bye").isExit());
        assertFalse(Command.parse("list").isExit());
    }
}
