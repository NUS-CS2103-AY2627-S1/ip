package discitrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import discitrack.exception.SaveException;
import discitrack.parser.Parser;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.task.Todo;
import discitrack.ui.Ui;

/**
 * Checks invalid input and failed persistence without changing real user data.
 */
public class MoreErrorHandlingTest {
    @TempDir
    public Path directory;

    @TestFactory
    public Stream<DynamicTest> invalidCommands_specificErrors_preserveTasksAndFile() {
        String[][] cases = {
            {"deadline /by 2026-09-30", "description"},
            {"deadline report /by", "date after /by"},
            {"deadline report /by 2026-09-30 /by 2026-10-01", "/by only once"},
            {"deadline report /by 2026-02-30", "valid deadline date"},
            {"event /from 2026-09-30 /to 2026-10-01", "description"},
            {"event camp /from /to 2026-10-01", "date after /from"},
            {"event camp /from 2026-09-30 /to", "date after /to"},
            {"event camp /from 2026-09-30", "Include /to"},
            {"event camp /to 2026-10-01", "Include /from"},
            {"event camp /to 2026-10-01 /from 2026-09-30", "Put /from before /to"},
            {"event camp /from 2026-09-30 /from 2026-09-30 /to 2026-10-01", "/from only once"},
            {"event camp /from 2026-09-30 /to 2026-10-01 /to 2026-10-02", "/to only once"},
            {"event camp /from 2026-02-30 /to 2026-10-01", "start date is invalid"},
            {"event camp /from 2026-09-30 /to 2026-02-30", "end date is invalid"},
            {"event camp /from 2026-10-05 /to 2026-10-01", "end date cannot be before"},
            {"mark 1 2", "No tasks were changed"},
            {"unmark 1 2", "No tasks were changed"},
            {"delete 1 2", "No tasks were changed"},
            {"list extra", "takes no arguments"},
            {"help extra", "takes no arguments"},
            {"bye extra", "takes no arguments"},
            {"todo read | notes", "avoid ' | '"},
            {"todo read\nnotes", "one line"},
            {"checkdate 2026-02-30", "valid date"}
        };
        return Arrays.stream(cases).map(entry -> DynamicTest.dynamicTest(entry[0], () -> {
            Path file = Files.createTempDirectory(directory, "invalid-").resolve("tasks.txt");
            DisciTrack app = new DisciTrack(file.toString());
            app.getResponse("todo existing");
            String original = Files.readString(file);
            assertTrue(app.getResponse(entry[0]).contains(entry[1]), entry[0]);
            assertTrue(app.hasResponseError(), entry[0]);
            assertFalse(app.shouldExit());
            assertEquals(1, app.getTasks().size());
            assertFalse(app.getTasks().getFirst().isDone());
            assertEquals(original, Files.readString(file), entry[0]);
        }));
    }

    @Test
    public void validBoundaries_tabsPastDatesSameDayAndDuplicates_remainAllowed() {
        DisciTrack app = new DisciTrack(directory.resolve("tasks.txt").toString());
        for (String input : List.of("  todo\t sleep  ", "todo sleep", "deadline old /by 2020-02-29",
                "event meeting /from 2026-09-30 /to 2026-09-30")) {
            app.getResponse(input);
            assertFalse(app.hasResponseError(), input);
        }
        assertEquals(4, app.getTasks().size());
    }

    @Test
    public void malformedRecords_blockStartupAndSaving_preserveOriginalFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        for (String record : List.of("D | 0 | report | 2026-02-30",
                "E | 0 | camp | 2026-09-30 | invalid", "T | 0 | ",
                "E | 0 | camp | 2026-10-05 | 2026-10-01")) {
            String content = "T | 0 | valid\n" + record;
            Files.writeString(file, content);
            DisciTrack app = new DisciTrack(file.toString());
            assertTrue(app.hasLoadError());
            assertTrue(app.getGreeting().contains("line 2"));
            if (record.contains("2026-02-30")) {
                assertTrue(app.getGreeting().contains("2026-02-30"));
            }
            assertTrue(app.getResponse("todo overwrite").contains("disabled"));
            assertEquals(content, Files.readString(file));
            Storage storage = new Storage(file.toString());
            assertThrows(IOException.class, storage::load);
            assertThrows(IOException.class, () -> storage.save(List.of(new Todo("overwrite"))));
        }
    }

    @Test
    public void saveFailure_allMutations_restoreMemoryAndDisk() throws Exception {
        Path file = directory.resolve("tasks.txt");
        FailingStorage storage = new FailingStorage(file);
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        first.addTag("school");
        Task second = new Todo("second");
        second.markAsDone();
        tasks.add(first);
        tasks.add(second);
        storage.save(tasks.asList());
        String original = Files.readString(file);
        storage.shouldFail = true;
        for (String input : List.of("todo new", "deadline new /by 2026-10-01",
                "event new /from 2026-10-01 /to 2026-10-02", "todo new /tag school",
                "mark 1", "unmark 2", "delete 1", "delete 2", "tag 1 urgent", "untag 1 school")) {
            Ui ui = new Ui(false);
            assertThrows(SaveException.class, () -> Parser.parse(input).execute(tasks, ui, storage), input);
            assertEquals(List.of(first, second), tasks.asList(), input);
            assertFalse(first.isDone(), input);
            assertTrue(second.isDone(), input);
            assertEquals(List.of("school"), first.getTags(), input);
            assertEquals(original, Files.readString(file), input);
            assertEquals("", ui.getLastResponse());
        }
        try (var files = Files.list(directory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    public void repeatedCompletion_reportsNoChange_withoutSaving() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("exercise"));
        FailingStorage storage = new FailingStorage(directory.resolve("tasks.txt"));
        storage.shouldFail = true;
        Ui ui = new Ui(false);
        Parser.parse("unmark 1").execute(tasks, ui, storage);
        assertTrue(ui.getLastResponse().contains("already not done"));
        tasks.get(1).markAsDone();
        Parser.parse("mark 1").execute(tasks, ui, storage);
        assertTrue(ui.getLastResponse().contains("already completed"));
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    private static class FailingStorage extends Storage {
        private boolean shouldFail;

        FailingStorage(Path path) {
            super(path.toString());
        }

        @Override
        protected void replaceFile(Path temporary, Path target) throws IOException {
            if (shouldFail) {
                throw new IOException("Simulated failure before replacing saved data");
            }
            super.replaceFile(temporary, target);
        }
    }
}
