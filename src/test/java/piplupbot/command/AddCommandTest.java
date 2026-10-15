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
import piplupbot.task.Deadline;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

/**
 * Tests {@link AddCommand}, which stores a task, confirms it, and saves the
 * list.
 *
 * <p>The order of those three steps is what most of these cases are about. The
 * task is added first, so the count in the confirmation includes it. A task the
 * list refuses is refused before anything is said or saved. And a save that
 * fails is reported after the confirmation, because the task really was added
 * for the rest of the session.</p>
 *
 * <p>Every case keeps its tasks in a folder JUnit creates and deletes for it, so
 * no test can read or damage the real save file.</p>
 */
public class AddCommandTest {

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
     * The task shows itself in the confirmation, date and all, and the count
     * after it includes the new task: a list that held one task now holds two.
     */
    @Test
    public void execute_newTask_addsConfirmsAndSaves() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new AddCommand(new Deadline("return book", "2019-10-15 1800"))
                .execute(tasks, ui, new Storage(getSaveFile()));

        assertEquals(List.of("Piplup! I've tucked this task under my wing:\n"
                + "  [D][ ] return book (by: Oct 15 2019 06:00 PM)\n"
                + "Now you have 2 tasks in the list."), ui.getReplies());
        assertEquals("T | 0 | read book\nD | 0 | return book | 2019-10-15T18:00\n",
                Files.readString(getSaveFile()));
    }

    /**
     * A task the list already holds is refused before anything happens: there
     * is no confirmation for a task that was not added, and no save. The save
     * file is not even created.
     */
    @Test
    public void execute_taskAlreadyInList_exceptionThrownAndNothingSaidOrSaved() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(PiplupBotException.class, () ->
                new AddCommand(new Todo("read book")).execute(tasks, ui, new Storage(getSaveFile())));

        assertEquals(1, tasks.size());
        assertTrue(ui.getReplies().isEmpty(), "Nothing should be confirmed, but was: " + ui.getReplies());
        assertFalse(Files.exists(getSaveFile()), "Nothing should be saved");
    }

    /**
     * A save that fails neither undoes the add nor hides its confirmation. The
     * task stays in the list for this session, as the confirmation says, and the
     * warning after it says what the confirmation cannot. A file stands where
     * the save file's folder should be, which makes the save fail on every
     * operating system.
     */
    @Test
    public void execute_saveFails_confirmsThenReportsTheFailure() throws Exception {
        Path notAFolder = tempDir.resolve("data");
        Files.writeString(notAFolder, "a file where the folder should be");
        TaskList tasks = new TaskList();

        new AddCommand(new Todo("read book"))
                .execute(tasks, ui, new Storage(notAFolder.resolve("piplupbot.txt")));

        assertArrayEquals(new String[] {"1.[T][ ] read book"}, tasks.toNumberedLines());
        List<String> replies = ui.getReplies();
        assertEquals(2, replies.size());
        assertTrue(replies.get(0).startsWith("Piplup! I've tucked this task under my wing:"),
                "Expected the confirmation first, but was: " + replies.get(0));
        assertTrue(replies.get(1).startsWith("I could not save your tasks to "),
                "Expected the failed save second, but was: " + replies.get(1));
    }
}
