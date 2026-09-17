package potatobot.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;
import potatobot.logic.command.AddCommand;
import potatobot.logic.command.DeleteCommand;
import potatobot.logic.command.ExitCommand;
import potatobot.logic.command.FindCommand;
import potatobot.logic.command.ListCommand;
import potatobot.logic.command.MarkCommand;
import potatobot.logic.command.UndoCommand;
import potatobot.logic.command.UnmarkCommand;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_creationCommands_preservesDescriptionsAndDates() throws PotatoBotException {
        TaskList tasks = new TaskList();
        Storage unusedStorage = new Storage("unused.txt");
        parser.parse("add buy two potatoes").execute(tasks, unusedStorage);
        parser.parse("todo read two books").execute(tasks, unusedStorage);
        parser.parse("deadline submit report /by 2024-02-29").execute(tasks, unusedStorage);
        parser.parse("event team meeting /from 2026-12-31 /to 2027-01-01").execute(tasks, unusedStorage);
        assertEquals("buy two potatoes", tasks.get(0).toString());
        assertEquals("read two books (Todo)", tasks.get(1).toString());
        assertEquals("submit report (Deadline, by: Feb 29 2024)", tasks.get(2).toString());
        assertEquals("team meeting (Event, from: Dec 31 2026 to: Jan 01 2027)", tasks.get(3).toString());
    }

    @Test
    public void parse_unknownOrBlankInput_reportsUnknownCommand() {
        for (String input : new String[] { "", " ", "dance", "TODO book", " list" }) {
            assertEquals("Me no gets?", assertThrows(PotatoBotException.class,
                    () -> parser.parse(input)).getMessage());
        }
    }

    @Test
    public void parse_invalidNumbers_rejectsAllNumberedCommands() {
        for (String command : new String[] { "mark", "unmark", "delete" }) {
            for (String number : new String[] { "", "1.5", "2147483648", "-2147483649", "1 extra" }) {
                assertEquals("Do you hear yourself?? Task number must be a whole number.",
                        assertThrows(PotatoBotException.class,
                                () -> parser.parse(command + " " + number)).getMessage());
            }
        }
    }

    @Test
    public void parse_missingOrReversedDates_rejectsIncompleteTasks() {
        for (String input : new String[] {
            "deadline report /by ", "event meeting /from 2026-08-31 /to ",
            "event meeting /from 2026-08-31 /to invalid",
            "event meeting /from 2026-09-02 /to 2026-08-31"
        }) {
            assertThrows(PotatoBotException.class, () -> parser.parse(input));
        }
    }

    @Test
    public void parse_commandsWithoutArguments_correspondingCommandsReturned()
            throws PotatoBotException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(UndoCommand.class, parser.parse("undo"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
    }

    @Test
    public void parse_findCommand_findCommandReturned() throws PotatoBotException {
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    public void parse_findCommandWithoutKeyword_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> parser.parse("find"));
    }

    @Test
    public void parse_numberedCommands_correspondingCommandsReturned()
            throws PotatoBotException {
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 3"));
    }

    @Test
    public void parse_taskCreationCommands_addCommandsReturned()
            throws PotatoBotException {
        assertInstanceOf(AddCommand.class, parser.parse("add read book"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline submit report /by 2026-08-31"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event conference /from 2026-08-31 /to 2026-09-02"));
    }

    @Test
    public void parse_commandMissingRequiredArgument_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> parser.parse("add"));
        assertThrows(PotatoBotException.class, () -> parser.parse("todo"));
        assertThrows(PotatoBotException.class, () -> parser.parse("mark"));
        assertThrows(PotatoBotException.class, () -> parser.parse("unmark"));
        assertThrows(PotatoBotException.class, () -> parser.parse("delete"));
        assertThrows(PotatoBotException.class, () -> parser.parse("deadline"));
        assertThrows(PotatoBotException.class, () -> parser.parse("event"));
    }

    @Test
    public void parse_numberedCommandWithNonIntegerArgument_exceptionThrown() {
        PotatoBotException exception = assertThrows(
                PotatoBotException.class, () -> parser.parse("mark first"));

        assertEquals(
                "Do you hear yourself?? Task number must be a whole number.",
                exception.getMessage());
    }

    @Test
    public void parse_argumentGivenToArgumentlessCommand_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> parser.parse("list now"));
        assertThrows(PotatoBotException.class, () -> parser.parse("undo now"));
        assertThrows(PotatoBotException.class, () -> parser.parse("bye now"));
    }

    @Test
    public void parse_deadlineMissingByDelimiter_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> parser.parse("deadline submit report 2026-08-31"));
    }

    @Test
    public void parse_eventMissingDateDelimiter_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> parser.parse("event conference /to 2026-09-02"));
        assertThrows(PotatoBotException.class, () -> parser.parse("event conference /from 2026-08-31"));
    }

    @Test
    public void parse_invalidTaskDate_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> parser.parse("deadline submit report /by 2026-13-31"));
        assertThrows(PotatoBotException.class, () -> parser.parse(
                "event conference /from invalid /to 2026-09-02"));
    }
}
