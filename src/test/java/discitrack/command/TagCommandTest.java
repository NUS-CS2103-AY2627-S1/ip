package discitrack.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import discitrack.exception.DisciTrackException;
import discitrack.parser.Parser;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.task.Todo;
import discitrack.ui.Ui;

/**
 * Verifies no-op commands and rollback after a failed atomic file replacement.
 */
public class TagCommandTest {
    @TempDir
    public Path tempDir;

    @Test
    public void saveFailure_tagAndUntag_restoreOriginalMemoryAndFile() throws Exception {
        Path path = tempDir.resolve("tasks.txt");
        TestStorage storage = new TestStorage(path);
        Task task = new Todo("slides");
        task.replaceTags(List.of("School", "CS2103T", "urgent"));
        task.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(task);
        storage.save(tasks.asList());
        String original = Files.readString(path);
        storage.shouldFail = true;
        for (String input : List.of("tag 1 personal", "untag 1 cs2103t")) {
            Ui ui = new Ui(false);
            Command command = Parser.parse(input);
            DisciTrackException error = assertThrows(DisciTrackException.class, () ->
                    command.execute(tasks, ui, storage));
            assertTrue(error.getMessage().startsWith("UHOH! I could not save your tasks. No changes were made."));
            assertEquals(List.of("School", "CS2103T", "urgent"), task.getTags());
            assertEquals("slides", task.getActivity());
            assertTrue(task.isDone());
            assertEquals(original, Files.readString(path));
            assertEquals("", ui.getLastResponse());
            try (Stream<Path> files = Files.list(tempDir)) {
                assertEquals(List.of(path), files.toList());
            }
        }
    }

    @Test
    public void saveFailure_taggedCreation_restoresCountForAllTaskTypes() throws Exception {
        Path path = tempDir.resolve("tasks.txt");
        TestStorage storage = new TestStorage(path);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("existing"));
        storage.save(tasks.asList());
        String original = Files.readString(path);
        storage.shouldFail = true;
        for (String input : List.of("todo sleep /tag personal",
                "deadline report /by 2026-10-01 /tag school",
                "event meeting /from 2026-10-01 /to 2026-10-02 /tag project")) {
            Command command = Parser.parse(input);
            assertThrows(DisciTrackException.class, () -> command.execute(tasks, new Ui(false), storage));
            assertEquals(1, tasks.size());
            assertEquals("existing", tasks.get(1).getActivity());
            assertEquals(original, Files.readString(path));
        }
    }

    @Test
    public void duplicateMissingAndInvalidTags_doNotSave() throws Exception {
        TestStorage storage = new TestStorage(tempDir.resolve("tasks.txt"));
        storage.shouldFail = true;
        Task task = new Todo("slides");
        task.addTag("School");
        TaskList tasks = new TaskList();
        tasks.add(task);
        Ui ui = new Ui(false);
        Parser.parse("tag 1 school").execute(tasks, ui, storage);
        assertEquals("Task 1 already has tag \"School\".", ui.getLastResponse());
        Parser.parse("untag 1 urgent").execute(tasks, ui, storage);
        assertEquals("Task 1 does not have tag \"urgent\".", ui.getLastResponse());
        assertThrows(DisciTrackException.class, () ->
                Parser.parse("tag 1 #bad").execute(tasks, ui, storage));
        assertEquals(0, storage.saveCount);
        assertEquals(List.of("School"), task.getTags());
    }

    /**
     * Injects an I/O failure after the temporary file is written, before replacement.
     */
    private static class TestStorage extends Storage {
        private boolean shouldFail;
        private int saveCount;

        TestStorage(Path path) {
            super(path.toString());
        }

        @Override
        public void save(List<Task> tasks) throws IOException {
            saveCount++;
            super.save(tasks);
        }

        @Override
        protected void replaceFile(Path temporary, Path target) throws IOException {
            if (shouldFail) {
                throw new IOException("Simulated replacement failure");
            }
            super.replaceFile(temporary, target);
        }
    }
}
