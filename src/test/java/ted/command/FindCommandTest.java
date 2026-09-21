package ted.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ted.Storage;
import ted.Ui;
import ted.task.Tag;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link FindCommand} and {@link FindByTagCommand}, which show the tasks
 * matching a keyword or a tag.
 * <p>
 * The number shown next to each match matters most: it is the number the
 * user types next, so it must point at the same task in the full list.
 */
public class FindCommandTest {
    /**
     * Verifies that a match further down the list is shown with its number
     * in the full list, not its position among the matches.
     */
    @Test
    public void execute_keywordMatchesLaterTask_showsItsFullListNumber(@TempDir Path tempDir) {
        TaskList tasks = new TaskList(List.of(new Todo("buy milk"), new Todo("read book")));
        Ui ui = new Ui();

        new FindCommand("book").execute(tasks, ui, new Storage(tempDir.resolve("ted.txt").toString()));

        String reply = ui.flush();
        assertTrue(reply.contains("2.[T][ ] read book"));
        assertFalse(reply.contains("1.[T][ ] read book"));
    }

    /**
     * Verifies that a tag search numbers its matches by the full list too.
     */
    @Test
    public void execute_tagMatchesLaterTask_showsItsFullListNumber(@TempDir Path tempDir) {
        Todo taggedTask = new Todo("read book");
        taggedTask.addTags(List.of(new Tag("fun")));
        TaskList tasks = new TaskList(List.of(new Todo("buy milk"), taggedTask));
        Ui ui = new Ui();

        new FindByTagCommand(new Tag("fun")).execute(tasks, ui, new Storage(tempDir.resolve("ted.txt").toString()));

        assertTrue(ui.flush().contains("2.[T][ ] read book #fun"));
    }

    /**
     * Verifies that searching neither changes the list nor writes the save file.
     */
    @Test
    public void execute_anySearch_listUnchangedAndNothingSaved(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("ted.txt");
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        new FindCommand("book").execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals(1, tasks.size());
        assertFalse(Files.exists(dataFile));
    }
}
