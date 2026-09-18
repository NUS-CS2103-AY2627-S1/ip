package hermes.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hermes.HermesException;
import hermes.task.Deadline;
import hermes.task.LogBook;
import hermes.task.Storage;
import hermes.task.ToDo;

/**
 * Tests that each command carries out the change or query it stands for.
 *
 * <p>Commands only pass what they hold on to LogBook, whose replies are tested in
 * full elsewhere, so these cases check the effect and that the reply comes back.
 */
public class CommandTest {

    @TempDir
    Path tempDir;

    private LogBook logBook;

    @BeforeEach
    public void setUp() throws HermesException {
        logBook = new LogBook(new Storage(tempDir.resolve("Hermes.txt").toString()));
        logBook.log(new ToDo("read"));
        logBook.log(new ToDo("write"));
    }

    @Test
    public void isExit_eachCommand_onlyByeEndsTheConversation() {
        assertTrue(new ByeCommand().isExit());

        List<Command> others = List.of(
                new AddCommand(new ToDo("rest")), new DeleteCommand(0), new DueCommand(LocalDateTime.MAX),
                new FindCommand("read"), new ListCommand(), new MarkCommand(0), new SortCommand(),
                new UndoCommand(), new UnknownCommand("dance"), new UnmarkCommand(0));
        for (Command command : others) {
            assertFalse(command.isExit(), command.getClass().getSimpleName());
        }
    }

    @Test
    public void execute_bye_bidsFarewell() {
        assertEquals("Fare thee well. Shouldst thou have need of me, "
                + "call, and I shall come swifter than the wind.", new ByeCommand().execute(logBook));
    }

    @Test
    public void execute_unknown_quotesTheWord() {
        assertEquals("Alas, 'dance' is a word unknown even upon Olympus. I cannot act upon it.",
                new UnknownCommand("dance").execute(logBook));
    }

    @Test
    public void execute_add_logsTheTask() throws HermesException {
        String reply = new AddCommand(new ToDo("rest")).execute(logBook);

        assertTrue(reply.contains("[T][ ] rest"));
        assertEquals("1. [T][ ] read\n2. [T][ ] write\n3. [T][ ] rest\n", logBook.listTasks());
    }

    @Test
    public void execute_list_returnsTheNumberedTasks() {
        assertEquals("1. [T][ ] read\n2. [T][ ] write\n", new ListCommand().execute(logBook));
    }

    @Test
    public void execute_markThenUnmark_changesTheNamedTasks() throws HermesException {
        new MarkCommand(0, 1).execute(logBook);
        assertEquals("1. [T][X] read\n2. [T][X] write\n", logBook.listTasks());

        new UnmarkCommand(1).execute(logBook);
        assertEquals("1. [T][X] read\n2. [T][ ] write\n", logBook.listTasks());
    }

    @Test
    public void execute_delete_removesTheNamedTask() throws HermesException {
        new DeleteCommand(0).execute(logBook);

        assertEquals("1. [T][ ] write\n", logBook.listTasks());
    }

    @Test
    public void execute_commandNamingNoTask_propagatesTheException() {
        assertThrows(HermesException.class, () -> new MarkCommand(9).execute(logBook));
        assertThrows(HermesException.class, () -> new UnmarkCommand(9).execute(logBook));
        assertThrows(HermesException.class, () -> new DeleteCommand(9).execute(logBook));
    }

    @Test
    public void execute_due_listsTasksDueByTheCutoff() throws HermesException {
        logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 27, 15, 0)));

        assertEquals("[D][ ] essay (by: 27/08/2026 1500)",
                new DueCommand(LocalDateTime.of(2026, 8, 28, 0, 0)).execute(logBook));
    }

    @Test
    public void execute_find_listsMatchingTasks() {
        assertEquals("[T][ ] write", new FindCommand("wri").execute(logBook));
    }

    @Test
    public void execute_sortThenUndo_ordersAndRestores() throws HermesException {
        logBook.log(new Deadline("essay", LocalDateTime.of(2026, 8, 27, 15, 0)));

        new SortCommand().execute(logBook);
        assertEquals("1. [D][ ] essay (by: 27/08/2026 1500)\n2. [T][ ] read\n3. [T][ ] write\n",
                logBook.listTasks());

        new UndoCommand().execute(logBook);
        assertEquals("1. [T][ ] read\n2. [T][ ] write\n3. [D][ ] essay (by: 27/08/2026 1500)\n",
                logBook.listTasks());
    }
}
