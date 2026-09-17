package lebron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Lebron}, the backend both the console and GUI front ends
 * call through {@link Lebron#getResponse(String)}. These are integration
 * tests across parsing, the task list and storage: {@link
 * lebron.parser.ParserTest} and the {@code task}/{@code storage} package
 * tests already cover those pieces' own edge cases in isolation, so the
 * cases here focus on {@code execute()}'s dispatch and the exact response
 * text each command produces, plus that saving/loading actually round-trips
 * through a real {@link lebron.storage.Storage}.
 */
public class LebronTest {
    @TempDir
    private Path tempDir;

    private Lebron newLebron() {
        return new Lebron(tempDir.resolve("lebron.txt"));
    }

    @Test
    public void getResponse_todo_addsAndConfirms() {
        Lebron lebron = newLebron();
        assertEquals("on the board: buy milk", lebron.getResponse("todo buy milk"));
    }

    @Test
    public void getResponse_duplicateTodo_appendsNote() {
        Lebron lebron = newLebron();
        lebron.getResponse("todo buy milk");
        assertEquals("on the board: buy milk (note: you already have a task just like this)",
                lebron.getResponse("todo buy milk"));
    }

    @Test
    public void getResponse_deadline_addsAndConfirms() {
        Lebron lebron = newLebron();
        assertEquals("on the board: [D][ ] pay rent (by: Jan 01 2025)",
                lebron.getResponse("deadline pay rent /by 2025-01-01"));
    }

    @Test
    public void getResponse_event_addsAndConfirms() {
        Lebron lebron = newLebron();
        assertEquals("on the board: [E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)",
                lebron.getResponse("event camp /from 2019-06-01 /to 2019-06-03"));
    }

    @Test
    public void getResponse_list_showsEveryAddedTaskInOrder() {
        Lebron lebron = newLebron();
        lebron.getResponse("todo read book");
        lebron.getResponse("todo return book");
        assertEquals("Here's the playbook:" + System.lineSeparator()
                + "1.[ ] read book" + System.lineSeparator()
                + "2.[ ] return book", lebron.getResponse("list"));
    }

    @Test
    public void getResponse_mark_confirmsAndUpdatesTaskList() {
        Lebron lebron = newLebron();
        lebron.getResponse("todo read book");
        assertEquals("Nice bucket! Marked as done:" + System.lineSeparator() + "  [X] read book",
                lebron.getResponse("mark 1"));
        assertEquals("Here's the playbook:" + System.lineSeparator() + "1.[X] read book",
                lebron.getResponse("list"));
    }

    @Test
    public void getResponse_markRecurringTask_rollsForwardInsteadOfCompleting() {
        Lebron lebron = newLebron();
        lebron.getResponse("deadline pay rent /by 2025-01-01 /every month");
        assertEquals("Nice bucket! Since this one's a repeat play, I've already queued up the next rep:"
                + System.lineSeparator() + "  [D][ ] pay rent (by: Feb 01 2025) (every: month)",
                lebron.getResponse("mark 1"));
    }

    @Test
    public void getResponse_unmark_confirms() {
        Lebron lebron = newLebron();
        lebron.getResponse("todo read book");
        lebron.getResponse("mark 1");
        assertEquals("Timeout -- back on the clock, not done yet:" + System.lineSeparator() + "  [ ] read book",
                lebron.getResponse("unmark 1"));
    }

    @Test
    public void getResponse_delete_confirmsAndReportsRemainingCount() {
        Lebron lebron = newLebron();
        lebron.getResponse("todo read book");
        lebron.getResponse("todo return book");
        assertEquals("Cut from the roster:" + System.lineSeparator() + "  [ ] read book"
                + System.lineSeparator() + "Now you've got 1 plays left in the game plan.",
                lebron.getResponse("delete 1"));
    }

    @Test
    public void getResponse_find_showsOnlyMatchingTasks() {
        Lebron lebron = newLebron();
        lebron.getResponse("todo read book");
        lebron.getResponse("todo buy bread");
        assertEquals("Here's what matches from the playbook:" + System.lineSeparator() + "1.[ ] read book",
                lebron.getResponse("find book"));
    }

    @Test
    public void getResponse_bye_confirmsAndSetsIsExit() {
        Lebron lebron = newLebron();
        assertEquals("Good game -- I'll see you back on the court soon!", lebron.getResponse("bye"));
        assertTrue(lebron.isExit());
    }

    @Test
    public void isExit_resetsToFalseOnNextNonByeCall() {
        Lebron lebron = newLebron();
        lebron.getResponse("bye");
        lebron.getResponse("list");
        assertFalse(lebron.isExit());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessageAndDoesNotThrow() {
        Lebron lebron = newLebron();
        String response = lebron.getResponse("blah");
        assertTrue(response.startsWith("OOPS!!!"));
    }

    @Test
    public void getResponse_markOutOfRange_returnsErrorInsteadOfThrowing() {
        Lebron lebron = newLebron();
        String response = lebron.getResponse("mark 5");
        assertTrue(response.startsWith("OOPS!!!"));
    }

    @Test
    public void tasksAddedInOneSession_areVisibleAfterReconstructingLebron() {
        Path dataFile = tempDir.resolve("lebron.txt");
        new Lebron(dataFile).getResponse("todo read book");

        // A fresh Lebron on the same file, as if the program had restarted.
        Lebron restarted = new Lebron(dataFile);
        assertEquals("Here's the playbook:" + System.lineSeparator() + "1.[ ] read book",
                restarted.getResponse("list"));
    }
}
