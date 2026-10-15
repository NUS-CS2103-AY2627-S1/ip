package tuesday.processor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tuesday.task.Deadline;
import tuesday.task.Event;
import tuesday.task.Task;
import tuesday.task.TaskList;
import tuesday.task.Todo;
import tuesday.ui.Strings;

/**
 * Verifies persistence compatibility and unchanged task state after I/O failures.
 */
public class PersistenceTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void processCommand_saveFailure_restoresEveryAffectedTask() throws IOException {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "original bytes");
        CommandProcessor commandProcessor = new CommandProcessor(blockedParent.resolve("tasks.txt").toString());
        for (boolean wasDone : new boolean[] {false, true}) {
            TaskList tasks = new TaskList();
            Task first = new Todo("first");
            Task second = new Todo("second");
            tasks.add(first);
            tasks.add(second);
            if (wasDone) {
                first.mark();
            }
            for (String input : new String[] {"todo third", "delete 1", "delete 2", "mark 1", "unmark 1"}) {
                assertEquals(Strings.SAVE_FAILURE, commandProcessor.processCommand(input, tasks));
                assertEquals(2, tasks.size());
                assertSame(first, tasks.get(0));
                assertSame(second, tasks.get(1));
                assertEquals(wasDone, first.isDone());
                assertEquals("original bytes", Files.readString(blockedParent));
            }
            commandProcessor.processCommand("todo FIRST", tasks);
            assertTrue(commandProcessor.processCommand("new", tasks).endsWith("The existing task was kept."));
            assertSame(first, tasks.get(0));
            assertEquals(wasDone, first.isDone());
            assertEquals(Strings.DUPLICATE_CHOICE_UNAVAILABLE, commandProcessor.processCommand("old", tasks));
        }
    }

    @Test
    public void loadData_readOrCloseFailure_keepsCallerUnchanged() {
        for (boolean failsOnClose : new boolean[] {false, true}) {
            TaskList tasks = new TaskList();
            Task original = new Todo("original");
            tasks.add(original);
            IOException failure = new IOException("read or close failed");
            BufferedReader reader = new FailingTaskReader(failsOnClose, failure);
            assertSame(failure, assertThrows(IOException.class, () -> Storage.loadData(tasks, () -> reader)));
            assertEquals(1, tasks.size());
            assertSame(original, tasks.get(0));
        }
    }

    @Test
    public void loadData_missingFileOrDirectory_reportsDifferentFailures() {
        TaskList tasks = new TaskList();
        assertThrows(NoSuchFileException.class, () ->
                Storage.loadData(temporaryDirectory.resolve("missing.txt").toString(), tasks));
        IOException failure = assertThrows(IOException.class, () ->
                Storage.loadData(temporaryDirectory.toString(), tasks));
        assertFalse(failure instanceof NoSuchFileException);
        assertEquals(0, tasks.size());
    }

    @Test
    public void saveNewData_existingRawContent_preservesBytesAndAppends() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        String original = "[T][ ] café\r\nmalformed row\n[T][X] café\n";
        Files.write(target, original.getBytes(Charset.defaultCharset()));
        Storage.saveNewData(target.toString(), "[T][ ] new");
        assertArrayEquals((original + "[T][ ] new" + System.lineSeparator()).getBytes(Charset.defaultCharset()),
                Files.readAllBytes(target));
    }

    @Test
    public void modifyData_allDateTimeForms_roundTripsWithStatusAndDuplicates() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        Event.DateTimeValue[] values = {
            new Event.DateTimeValue(LocalDate.of(2026, 9, 20), null),
            new Event.DateTimeValue(null, LocalTime.of(10, 30)),
            new Event.DateTimeValue(LocalDate.of(2026, 9, 20), LocalTime.of(10, 30))
        };
        for (Event.DateTimeValue from : values) {
            Task deadline = new Deadline("duplicate", from.date(), from.time());
            deadline.mark();
            tasks.add(deadline);
            for (Event.DateTimeValue to : values) {
                tasks.add(new Event("duplicate", from, to));
            }
        }
        Storage.modifyData(target.toString(), tasks);
        TaskList loaded = new TaskList();
        Storage.loadData(target.toString(), loaded);
        assertEquals(tasks.size(), loaded.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).toString(), loaded.get(i).toString());
        }
    }

    @Test
    public void loadData_smartSavedValuesAndMalformedRows_preservesCompatibility() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(target, "[D][ ] date (by: Feb 29 2025)\n[D][ ] time (by: 24:00)\n"
                + "[D][ ] both (by: Sep 20 2026 24:00)\n"
                + "[?][ ] bad\n[T][?] bad\n[T]bad\n[D][ ] bad (by: nonsense)\n"
                + "[E][ ] bad (from: 10:00 to: bad)\n[T][ ] last\n");
        TaskList tasks = new TaskList();
        Storage.loadData(target.toString(), tasks);
        assertEquals(4, tasks.size());
        assertEquals("[D][ ] date (by: Feb 28 2025)", tasks.get(0).toString());
        assertEquals("[D][ ] time (by: 00:00)", tasks.get(1).toString());
        assertEquals("[D][ ] both (by: Sep 20 2026 00:00)", tasks.get(2).toString());
        assertEquals("[T][ ] last", tasks.get(3).toString());
    }

    @Test
    public void processCommand_successfulChanges_persistBeforeConfirmation() throws IOException {
        Path target = temporaryDirectory.resolve("tasks.txt");
        CommandProcessor commandProcessor = new CommandProcessor(target.toString());
        TaskList tasks = new TaskList();
        commandProcessor.processCommand("todo first", tasks);
        commandProcessor.processCommand("todo second", tasks);
        assertTrue(commandProcessor.processCommand("mark +1", tasks).startsWith(Strings.MARK));
        assertTrue(Files.readString(target).startsWith("[T][X] first"));
        assertTrue(commandProcessor.processCommand("unmark 1", tasks).startsWith(Strings.UNMARK));
        assertTrue(Files.readString(target).startsWith("[T][ ] first"));
        assertTrue(commandProcessor.processCommand("delete 1", tasks).startsWith("Noted."));
        assertEquals("[T][ ] second" + System.lineSeparator(), Files.readString(target));
    }

    /**
     * Supplies one valid task before failing on the next read or when the reader is closed.
     */
    private static class FailingTaskReader extends BufferedReader {
        private final boolean failsOnClose;
        private final IOException failure;
        private boolean hasRead;

        /**
         * Selects the failure stage and preserves the exception instance for identity assertions.
         */
        FailingTaskReader(boolean failsOnClose, IOException failure) {
            super(new StringReader("[T][ ] staged\n"));
            this.failsOnClose = failsOnClose;
            this.failure = failure;
        }

        @Override
        public String readLine() throws IOException {
            if (hasRead && !failsOnClose) {
                throw failure;
            }
            hasRead = true;
            return super.readLine();
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (failsOnClose) {
                throw failure;
            }
        }
    }
}
