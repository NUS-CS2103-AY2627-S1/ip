package ted.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ted.Storage;
import ted.TedException;
import ted.Ui;
import ted.task.Tag;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link TagCommand}, which attaches tags to a task or detaches them.
 * <p>
 * Every test saves inside a {@link TempDir}, so running the tests can never
 * disturb the user's own tasks.
 */
public class TagCommandTest {
    /**
     * Verifies that tagging attaches the tags, confirms it, and saves them.
     */
    @Test
    public void execute_tag_attachesConfirmsAndSaves(@TempDir Path tempDir) throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Ui ui = new Ui();

        new TagCommand(0, List.of(new Tag("fun"), new Tag("school")), true).execute(tasks, ui, storage);

        assertTrue(ui.flush().contains("I've tagged it:"));
        assertEquals("T | 0 | #fun #school | read book", storage.load().get(0).toSaveFormat());
    }

    /**
     * Verifies that untagging detaches only the named tags and saves the result.
     */
    @Test
    public void execute_untag_detachesOnlyThoseTags(@TempDir Path tempDir) throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        Todo task = new Todo("read book");
        task.addTags(List.of(new Tag("fun"), new Tag("school")));
        TaskList tasks = new TaskList(List.of(task));
        Ui ui = new Ui();

        new TagCommand(0, List.of(new Tag("fun")), false).execute(tasks, ui, storage);

        assertTrue(ui.flush().contains("Tag removed."));
        assertEquals("T | 0 | #school | read book", storage.load().get(0).toSaveFormat());
    }
}
