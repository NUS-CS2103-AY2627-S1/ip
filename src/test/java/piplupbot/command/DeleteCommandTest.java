package piplupbot.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import piplupbot.PiplupBotException;
import piplupbot.RecordingUi;
import piplupbot.Storage;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

/**
 * Tests {@link DeleteCommand}, which removes a task, confirms it, and saves the
 * list.
 *
 * <p>The confirmation names the task that was removed -- not the task that has
 * its number now -- and counts the tasks that are left. Looking at the list a
 * moment too late gets the first wrong, and a moment too early gets the second
 * wrong, so the first case deletes from a list where both mistakes would
 * show.</p>
 *
 * <p>Every case keeps its tasks in a folder JUnit creates and deletes for it, so
 * no test can read or damage the real save file.</p>
 */
public class DeleteCommandTest {

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

    @Test
    public void execute_firstOfTwoTasks_removesConfirmsAndSaves() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write notes"));

        new DeleteCommand(1).execute(tasks, ui, new Storage(getSaveFile()));

        assertArrayEquals(new String[] {"1.[T][ ] write notes"}, tasks.toNumberedLines());
        assertEquals(List.of("Splash! I've washed this task away:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list."), ui.getReplies());
        assertEquals("T | 0 | write notes\n", Files.readString(getSaveFile()));
    }

    /** A number that names no task changes nothing, says nothing, and saves nothing. */
    @Test
    public void execute_numberNamingNoTask_exceptionThrownAndNothingSaved() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(PiplupBotException.class, () ->
                new DeleteCommand(2).execute(tasks, ui, new Storage(getSaveFile())));

        assertArrayEquals(new String[] {"1.[T][ ] read book"}, tasks.toNumberedLines());
        assertTrue(ui.getReplies().isEmpty(), "Nothing should be confirmed, but was: " + ui.getReplies());
        assertFalse(Files.exists(getSaveFile()), "Nothing should be saved");
    }
}
