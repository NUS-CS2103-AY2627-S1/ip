package kojispawn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kojispawn.command.CommandType;

/**
 * Tests command responses used by both the console and JavaFX interfaces.
 */
public class KojisPawnTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_failedAdd_preservesTasksAndUndo() throws Exception {
        assertFailedChangePreservesState("todo third task", false);
    }

    @Test
    public void getResponse_failedMark_preservesTasksAndUndo() throws Exception {
        assertFailedChangePreservesState("mark 1", false);
    }

    @Test
    public void getResponse_failedUnmark_preservesTasksAndUndo() throws Exception {
        assertFailedChangePreservesState("unmark 1", true);
    }

    @Test
    public void getResponse_failedDelete_preservesTasksAndUndo() throws Exception {
        assertFailedChangePreservesState("delete 1", false);
    }

    @Test
    public void getResponse_failedUndo_preservesStateAndAllowsRetry() throws Exception {
        Path dataFile = tempDirectory.resolve("kojispawn.txt");
        KojisPawn koji = new KojisPawn(dataFile);
        koji.getResponse("todo first task");
        koji.getResponse("todo second task");
        Path backup = tempDirectory.resolve("saved.txt");
        Files.move(dataFile, backup);
        Files.createDirectory(dataFile);

        assertEquals("I could not save the task list to " + dataFile + ".", koji.getResponse("undo"));
        assertEquals(CommandType.UNKNOWN, koji.getLastCommandType());
        assertEquals("1.[T][ ] first task\n2.[T][ ] second task", koji.getResponse("list"));

        Files.delete(dataFile);
        Files.move(backup, dataFile);
        assertEquals("The previous task change has been undone.", koji.getResponse("undo"));
        assertEquals("1.[T][ ] first task", koji.getResponse("list"));
        assertEquals(List.of("T | 0 | first task"), Files.readAllLines(dataFile));
        assertEquals("There is no task change to undo.", koji.getResponse("undo"));
    }

    /**
     * Uses a directory at the save path to simulate a write failure on every operating system.
     */
    private void assertFailedChangePreservesState(String command, boolean isInitiallyMarked)
            throws Exception {
        Path dataFile = tempDirectory.resolve("kojispawn.txt");
        KojisPawn koji = new KojisPawn(dataFile);
        koji.getResponse("todo first task");
        koji.getResponse("todo second task");
        if (isInitiallyMarked) {
            koji.getResponse("mark 1");
        }
        String originalList = koji.getResponse("list");
        List<String> originalData = Files.readAllLines(dataFile);
        Path backup = tempDirectory.resolve("saved.txt");
        Files.move(dataFile, backup);
        Files.createDirectory(dataFile);

        assertEquals("I could not save the task list to " + dataFile + ".", koji.getResponse(command));
        assertEquals(CommandType.UNKNOWN, koji.getLastCommandType());
        assertEquals(originalList, koji.getResponse("list"));
        assertEquals(originalData, Files.readAllLines(backup));

        Files.delete(dataFile);
        Files.move(backup, dataFile);
        assertEquals("The previous task change has been undone.", koji.getResponse("undo"));
        String expectedList = isInitiallyMarked
                ? "1.[T][ ] first task\n2.[T][ ] second task" : "1.[T][ ] first task";
        assertEquals(expectedList, koji.getResponse("list"));
        assertEquals(expectedList, new KojisPawn(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_taskLifecycle_returnsResponsesAndPersistsChanges() throws Exception {
        Path dataFile = tempDirectory.resolve("data/kojispawn.txt");
        KojisPawn koji = new KojisPawn(dataFile);

        assertEquals(
                "Got it. I've added this task:\n"
                        + "  [T][ ] control the board\n"
                        + "Now you have 1 task in the list.",
                koji.getResponse("todo control the board"));
        assertEquals(CommandType.TODO, koji.getLastCommandType());
        assertEquals("1.[T][ ] control the board", koji.getResponse("list"));
        assertEquals(
                "Another variable falls into place. This task is now complete:\n"
                        + "  [T][X] control the board",
                koji.getResponse("mark 1"));
        assertEquals(CommandType.MARK, koji.getLastCommandType());
        assertEquals(List.of("T | 1 | control the board"), Files.readAllLines(dataFile));
    }

    @Test
    public void getResponse_searchCommands_returnMatchesAndEmptyResults() throws Exception {
        KojisPawn koji = new KojisPawn(tempDirectory.resolve("kojispawn.txt"));
        koji.getResponse("todo read book");
        koji.getResponse("deadline return book /by 2019-12-02");
        koji.getResponse("event project meeting /from book club /to evening");

        assertEquals("Dated tasks occurring on 2019-12-02:\n"
                + "1.[D][ ] return book (by: Dec 2 2019)", koji.getResponse("on 2019-12-02"));
        assertEquals(CommandType.ON, koji.getLastCommandType());
        assertEquals("No dated tasks occur on 2019-12-03.", koji.getResponse("on 2019-12-03"));
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n2.[D][ ] return book (by: Dec 2 2019)",
                koji.getResponse("find book"));
        assertEquals(CommandType.FIND, koji.getLastCommandType());
        assertEquals("Here are the matching tasks in your list:", koji.getResponse("find Board"));
    }

    @Test
    public void getResponse_deadlineEventUnmarkAndDelete_returnResponsesAndPersistChanges()
            throws Exception {
        Path dataFile = tempDirectory.resolve("kojispawn.txt");
        KojisPawn koji = new KojisPawn(dataFile);

        assertEquals("Got it. I've added this task:\n"
                + "  [D][ ] return book (by: Dec 2 2019)\n"
                + "Now you have 1 task in the list.",
                koji.getResponse("deadline return book /by 2019-12-02"));
        assertEquals("Got it. I've added this task:\n"
                + "  [E][ ] meeting (from: Monday to: Tuesday)\n"
                + "Now you have 2 tasks in the list.",
                koji.getResponse("event meeting /from Monday /to Tuesday"));

        koji.getResponse("mark 1");
        assertEquals("Even regression has its purpose. This task is incomplete once more:\n"
                + "  [D][ ] return book (by: Dec 2 2019)", koji.getResponse("unmark 1"));
        assertEquals("A disposable piece has left the board. This task has been removed:\n"
                + "  [E][ ] meeting (from: Monday to: Tuesday)\n"
                + "Now you have 1 task in the list.", koji.getResponse("delete 2"));
        assertEquals(List.of("D | 0 | return book | 2019-12-02"), Files.readAllLines(dataFile));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorAndUnknownType() throws Exception {
        KojisPawn koji = new KojisPawn(tempDirectory.resolve("kojispawn.txt"));

        String response = koji.getResponse("move knight");

        assertEquals(
                "That command was never part of the plan. "
                        + "Try todo, deadline, event, list, mark, unmark, delete, undo, on, find, or bye.",
                response);
        assertEquals(CommandType.UNKNOWN, koji.getLastCommandType());
        assertFalse(koji.isExitRequested());
    }

    @Test
    public void getResponse_bye_returnsFarewellAndRequestsExit() throws Exception {
        KojisPawn koji = new KojisPawn(tempDirectory.resolve("kojispawn.txt"));

        String response = koji.getResponse("bye");

        assertEquals(
                "Leaving already? How predictable. Your return was already part of the plan.",
                response);
        assertEquals(CommandType.BYE, koji.getLastCommandType());
        assertTrue(koji.isExitRequested());
    }

    @Test
    public void getResponse_undoTaskChanges_restoresMostRecentStateAndPersistsIt() throws Exception {
        Path dataFile = tempDirectory.resolve("data/kojispawn.txt");
        KojisPawn koji = new KojisPawn(dataFile);

        koji.getResponse("todo first task");
        koji.getResponse("todo second task");
        koji.getResponse("mark 1");
        assertEquals("The previous task change has been undone.", koji.getResponse("undo"));
        assertEquals("1.[T][ ] first task\n2.[T][ ] second task", koji.getResponse("list"));

        koji.getResponse("delete 1");
        assertEquals("The previous task change has been undone.", koji.getResponse("undo"));
        assertEquals("1.[T][ ] first task\n2.[T][ ] second task", koji.getResponse("list"));

        assertEquals(List.of("T | 0 | first task", "T | 0 | second task"),
                Files.readAllLines(dataFile));
    }

    @Test
    public void getResponse_readOnlyAndFailedCommands_preserveUndoHistory() throws Exception {
        KojisPawn koji = new KojisPawn(tempDirectory.resolve("kojispawn.txt"));

        koji.getResponse("todo task");
        koji.getResponse("list");
        koji.getResponse("move knight");

        assertEquals("The previous task change has been undone.", koji.getResponse("undo"));
        assertEquals("", koji.getResponse("list"));
        assertEquals("There is no task change to undo.", koji.getResponse("undo"));
        assertEquals(CommandType.UNKNOWN, koji.getLastCommandType());
    }
}
