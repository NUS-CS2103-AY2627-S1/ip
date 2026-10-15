package piplupbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import piplupbot.RecordingUi;
import piplupbot.Storage;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

/**
 * Tests {@link FindCommand}, which shows the tasks whose description contains
 * some text.
 *
 * <p>Which tasks match is {@link TaskList#find}'s question, and is tested
 * there. These cases check what the command does with the answer: the heading
 * above the matches, and that a search, which changes nothing, saves
 * nothing.</p>
 */
public class FindCommandTest {

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

    /** The matches are numbered among themselves, so "return book" is shown as 2 rather than 3. */
    @Test
    public void execute_keywordInSomeTasks_showsThoseNumberedFromOne() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write notes"));
        tasks.add(new Todo("return book"));

        new FindCommand("book").execute(tasks, ui, new Storage(getSaveFile()));

        assertEquals(List.of("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][ ] return book"), ui.getReplies());
        assertFalse(Files.exists(getSaveFile()), "A search should save nothing");
    }

    /** A search that finds nothing is still answered, with the heading alone. */
    @Test
    public void execute_keywordInNoTask_showsTheHeadingAlone() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new FindCommand("homework").execute(tasks, ui, new Storage(getSaveFile()));

        assertEquals(List.of("Here are the matching tasks in your list:"), ui.getReplies());
    }
}
