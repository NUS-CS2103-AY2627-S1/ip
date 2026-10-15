package piplupbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import piplupbot.RecordingUi;
import piplupbot.Storage;
import piplupbot.Ui;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

/**
 * Tests the behavior {@link Command} shares with every command: the sentence
 * that counts the tasks, saving the list, and not ending the conversation.
 *
 * <p>The case that matters most for {@link Command#save} is a save that fails.
 * The text-UI test plan has no way to make one fail, so this is where it is
 * checked that the failure is reported to the user, rather than thrown out of
 * the command that asked for the save.</p>
 *
 * <p>The saving cases keep their file in a folder JUnit creates and deletes for
 * them, so no test can read or damage the real save file.</p>
 */
public class CommandTest {

    /**
     * A folder JUnit creates for each test and deletes afterwards. It is left
     * package-private because {@code @TempDir} cannot fill in a private field.
     */
    @TempDir
    Path tempDir;

    /** Catches what the command says. */
    private final RecordingUi ui = new RecordingUi();

    // ---------- Counting the tasks ----------

    /**
     * The singular is the whole reason this method exists: before it, the bot
     * said "1 tasks". This case fails if the check is removed.
     */
    @Test
    public void describeTaskCount_oneTask_singular() {
        assertEquals("Now you have 1 task in the list.", Command.describeTaskCount(1));
    }

    /**
     * Zero is the easiest count to get wrong, since a rule such as
     * {@code taskCount <= 1} would read naturally and still be wrong. This is
     * what a user sees after deleting the last task.
     */
    @Test
    public void describeTaskCount_zeroTasks_plural() {
        assertEquals("Now you have 0 tasks in the list.", Command.describeTaskCount(0));
    }

    /**
     * Eleven is checked as well as two, so a rule that looks at the last digit
     * rather than the whole number would fail.
     */
    @Test
    public void describeTaskCount_severalTasks_plural() {
        assertEquals("Now you have 2 tasks in the list.", Command.describeTaskCount(2));
        assertEquals("Now you have 11 tasks in the list.", Command.describeTaskCount(11));
    }

    // ---------- Saving the list ----------

    /** A save that works writes the whole list, and is not worth a word to the user. */
    @Test
    public void save_fileCanBeWritten_writesTheListAndSaysNothing() throws Exception {
        Path saveFile = tempDir.resolve("piplupbot.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new SavingCommand().execute(tasks, ui, new Storage(saveFile));

        assertEquals("T | 0 | read book\n", Files.readString(saveFile));
        assertEquals(List.of(), ui.getReplies());
    }

    /**
     * A save that fails is reported rather than thrown, so the command that
     * asked for it still finishes, and the conversation goes on. A file stands
     * where the save file's folder should be, which makes the save fail on every
     * operating system.
     */
    @Test
    public void save_fileCannotBeWritten_reportsTheFailureInsteadOfThrowing() throws Exception {
        Path notAFolder = tempDir.resolve("data");
        Files.writeString(notAFolder, "a file where the folder should be");

        new SavingCommand().execute(new TaskList(), ui, new Storage(notAFolder.resolve("piplupbot.txt")));

        List<String> replies = ui.getReplies();
        assertEquals(1, replies.size());
        assertTrue(replies.get(0).startsWith("I could not save your tasks to "),
                "Expected the failed save to be reported, but was: " + replies.get(0));
    }

    // ---------- Ending the conversation ----------

    /**
     * A command ends the conversation only if it says so itself, so a new
     * command that never mentions the subject lets the conversation go on.
     */
    @Test
    public void isExit_commandThatDoesNotOverrideIt_false() {
        assertFalse(new SavingCommand().isExit());
    }

    /**
     * Represents the smallest command that saves. It does nothing else, so a test can watch
     * {@link Command#save} on its own, called the way every real command calls
     * it.
     */
    private static class SavingCommand extends Command {
        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) {
            save(tasks, ui, storage);
        }
    }
}
