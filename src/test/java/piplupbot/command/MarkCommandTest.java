package piplupbot.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import piplupbot.PiplupBotException;
import piplupbot.RecordingUi;
import piplupbot.Storage;
import piplupbot.task.Task;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

/**
 * Tests {@link MarkCommand}, which sets a task's done status for both
 * {@code mark} and {@code unmark}.
 *
 * <p>The case worth guarding is a task that already has the status asked for.
 * It is refused rather than confirmed, and nothing is saved. That branch is easy
 * to lose, because every ordinary {@code mark} and {@code unmark} still works
 * without it; the cases below fail if it is removed.</p>
 *
 * <p>Every case keeps its tasks in a folder JUnit creates and deletes for it, so
 * no test can read or damage the real save file.</p>
 */
public class MarkCommandTest {

    /**
     * A folder JUnit creates for each test and deletes afterwards. It is left
     * package-private because {@code @TempDir} cannot fill in a private field.
     */
    @TempDir
    Path tempDir;

    /** Catches what the command says. */
    private final RecordingUi ui = new RecordingUi();

    /**
     * Returns the save file this test should use, inside the temporary folder.
     *
     * @return The path to a save file of this test's own.
     */
    private Path getSaveFile() {
        return tempDir.resolve("piplupbot.txt");
    }

    /**
     * Returns a list holding one todo, done or not.
     *
     * @param isDone Whether the todo starts out done.
     * @return A list holding just that todo, as task 1.
     */
    private static TaskList createListWithOneTodo(boolean isDone) {
        Todo todo = new Todo("read book");
        if (isDone) {
            todo.markAsDone();
        }
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);
        return new TaskList(tasks);
    }

    // ---------- Changing the status ----------

    @Test
    public void execute_markTaskNotDone_marksConfirmsAndSaves() throws Exception {
        TaskList tasks = createListWithOneTodo(false);

        new MarkCommand(1, true).execute(tasks, ui, new Storage(getSaveFile()));

        assertTrue(tasks.get(1).isDone());
        assertEquals(List.of("Piplup! One more fish in the bucket. I've marked this task as done:\n"
                + "  [T][X] read book"), ui.getReplies());
        assertEquals("T | 1 | read book\n", Files.readString(getSaveFile()));
    }

    @Test
    public void execute_unmarkDoneTask_unmarksConfirmsAndSaves() throws Exception {
        TaskList tasks = createListWithOneTodo(true);

        new MarkCommand(1, false).execute(tasks, ui, new Storage(getSaveFile()));

        assertFalse(tasks.get(1).isDone());
        assertEquals(List.of("Pip-pip, no rush! I've marked this task as not done yet:\n"
                + "  [T][ ] read book"), ui.getReplies());
        assertEquals("T | 0 | read book\n", Files.readString(getSaveFile()));
    }

    // ---------- A task that already has that status ----------

    /**
     * Marking a done task is refused, and the message shows the task so a
     * mistyped number is easy to spot. No confirmation is shown, and the save
     * file is never written -- it does not even exist afterwards.
     */
    @Test
    public void execute_markDoneTask_exceptionThrownAndNothingSaved() {
        TaskList tasks = createListWithOneTodo(true);

        PiplupBotException exception = assertThrows(PiplupBotException.class, () ->
                new MarkCommand(1, true).execute(tasks, ui, new Storage(getSaveFile())));

        assertArrayEquals(new String[] {
            "Pip... This task is already marked as done:",
            "  [T][X] read book",
        }, exception.getMessageLines());
        assertTrue(ui.getReplies().isEmpty(), "Nothing should be confirmed, but was: " + ui.getReplies());
        assertFalse(Files.exists(getSaveFile()), "Nothing should be saved");
    }

    /**
     * The same for {@code unmark} on a task that was never done. Its wording is
     * separate, so it has a case of its own.
     */
    @Test
    public void execute_unmarkTaskNotDone_exceptionThrownAndNothingSaved() {
        TaskList tasks = createListWithOneTodo(false);

        PiplupBotException exception = assertThrows(PiplupBotException.class, () ->
                new MarkCommand(1, false).execute(tasks, ui, new Storage(getSaveFile())));

        assertArrayEquals(new String[] {
            "Pip... This task is not marked as done yet:",
            "  [T][ ] read book",
        }, exception.getMessageLines());
        assertTrue(ui.getReplies().isEmpty(), "Nothing should be confirmed, but was: " + ui.getReplies());
        assertFalse(Files.exists(getSaveFile()), "Nothing should be saved");
    }
}
