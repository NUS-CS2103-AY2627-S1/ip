package meowmeow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meowmeow.task.Deadline;
import meowmeow.task.Event;
import meowmeow.task.Task;
import meowmeow.task.TaskDateTime;
import meowmeow.task.TaskPriority;
import meowmeow.task.TaskStatus;
import meowmeow.task.Todo;
import meowmeow.ui.MessageStyle;
import meowmeow.ui.Ui;

/**
 * Tests for {@link Storage} - loading the task list from disk and saving it
 * back.
 *
 * <p>The high-value logic is in the private {@code parseTask} that
 * {@link Storage#load()} runs on every line: it must rebuild the right
 * {@link Task} subclass, reject a malformed line (unknown type tag, a
 * done-flag other than {@code 0}/{@code 1}, too few fields, an unparseable
 * date) by skipping just that line, and keep every good line around it.
 * These are exercised through the public {@code load}/{@code save}.
 *
 * <p>{@code @TempDir} gives each test its own throwaway folder, so nothing
 * here touches the real {@code ./data} file.
 */
public class StorageTest {

    private static final long SEED = 42L;

    @TempDir
    private Path tempDir;

    /** A Storage rooted at the per-test temp folder, writing warnings to a real Ui. */
    private Storage storageAt(String... segments) {
        String first = segments[0];
        String[] more = new String[segments.length - 1];
        System.arraycopy(segments, 1, more, 0, more.length);
        return new Storage(new Ui(MessageStyle.PLAIN, new Random(SEED)), tempDir.resolve(first).toString(), more);
    }

    private void writeSaveFile(String name, String... lines) throws IOException {
        Files.write(tempDir.resolve(name), List.of(lines));
    }

    // ---- load: first run / missing file ----

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = storageAt("data", "tasks.txt"); // never created

        assertEquals(0, storage.load().size());
    }

    // ---- load: good lines ----

    @Test
    public void load_wellFormedLines_rebuildsEveryTask() throws IOException {
        writeSaveFile("tasks.txt",
                "T | 1 | read book",
                "D | 0 | return book | 2019-12-02 1800",
                "E | 0 | project | 2019-12-02 1400 | 2019-12-02 1600");
        Storage storage = storageAt("tasks.txt");

        List<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertInstanceOfTodoDone(tasks.get(0));
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 pm)", tasks.get(1).toString());
        assertEquals("[E][ ] project (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)",
                tasks.get(2).toString());
    }

    private static void assertInstanceOfTodoDone(Task task) {
        assertEquals("[T][X] read book", task.toString());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    public void load_blankLines_skippedWithoutAffectingCount() throws IOException {
        writeSaveFile("tasks.txt",
                "T | 0 | a",
                "",
                "   ",
                "T | 0 | b");
        Storage storage = storageAt("tasks.txt");

        assertEquals(2, storage.load().size());
    }

    // ---- load: malformed lines are skipped, good ones survive ----

    @Test
    public void load_corruptLines_skippedButGoodLinesKept() throws IOException {
        writeSaveFile("tasks.txt",
                "T | 0 | keep me",
                "X | 0 | unknown type tag",
                "T | 2 | bad done flag",
                "T | 0", // too few fields
                "D | 0 | deadline with no date", // D needs 4 fields
                "D | 0 | deadline bad date | someday", // date no longer parses
                "E | 0 | event with no end | 2019-12-02 1400", // E needs 5 fields
                "T | 1 | keep me too");
        Storage storage = storageAt("tasks.txt");

        List<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] keep me", tasks.get(0).toString());
        assertEquals("[T][X] keep me too", tasks.get(1).toString());
    }

    // ---- load: the optional trailing priority field ----

    @Test
    public void load_lineWithoutPriorityField_loadsAsNone() throws IOException {
        writeSaveFile("tasks.txt", "T | 0 | read book");
        Storage storage = storageAt("tasks.txt");

        Task task = storage.load().get(0);

        assertEquals(TaskPriority.NONE, task.getPriority());
    }

    @Test
    public void load_trailingPriorityField_appliedForEveryTaskType() throws IOException {
        writeSaveFile("tasks.txt",
                "T | 0 | read book | HIGH",
                "D | 0 | return book | 2019-12-02 1800 | LOW",
                "E | 0 | project | 2019-12-02 1400 | 2019-12-02 1600 | MEDIUM");
        Storage storage = storageAt("tasks.txt");

        List<Task> tasks = storage.load();

        assertEquals(TaskPriority.HIGH, tasks.get(0).getPriority());
        assertEquals(TaskPriority.LOW, tasks.get(1).getPriority());
        assertEquals(TaskPriority.MEDIUM, tasks.get(2).getPriority());
    }

    @Test
    public void load_unrecognisedPriorityToken_taskKeptAsNoneWithoutWarning() throws IOException {
        writeSaveFile("tasks.txt", "T | 0 | read book | URGENT");
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(SEED));
        Storage storage = new Storage(ui, tempDir.resolve("tasks.txt").toString());

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(TaskPriority.NONE, tasks.get(0).getPriority());
        assertEquals(0, ui.drainWarnings().size());
    }

    // ---- save, then load: the two must round-trip ----

    @Test
    public void saveThenLoad_roundTripsEveryTaskAndStatus() throws Exception {
        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("borrow book");
        todo.setStatus(TaskStatus.DONE);
        original.add(todo);
        original.add(new Deadline("return book", TaskDateTime.parse("2/12/2019 1800")));
        original.add(new Event("project", TaskDateTime.parse("2/12/2019 1400"),
                TaskDateTime.parse("2/12/2019 1600")));

        Storage storage = storageAt("tasks.txt");
        storage.save(original);
        List<Task> reloaded = storage.load();

        assertEquals(original.size(), reloaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toFileString(), reloaded.get(i).toFileString());
            assertEquals(original.get(i).toString(), reloaded.get(i).toString());
        }
    }

    @Test
    public void saveThenLoad_priorityRoundTrips() throws Exception {
        Todo highPriority = new Todo("borrow book");
        highPriority.setPriority(TaskPriority.HIGH);

        Storage storage = storageAt("tasks.txt");
        storage.save(List.of(highPriority));
        Task reloaded = storage.load().get(0);

        assertEquals(TaskPriority.HIGH, reloaded.getPriority());
        assertEquals(highPriority.toFileString(), reloaded.toFileString());
    }

    @Test
    public void save_taskWithNoPriority_writesLineIdenticalToBeforePrioritiesExisted() throws Exception {
        Storage storage = storageAt("tasks.txt");

        storage.save(List.of(new Todo("borrow book")));

        assertEquals(List.of("T | 0 | borrow book"), Files.readAllLines(tempDir.resolve("tasks.txt")));
    }

    @Test
    public void save_createsMissingParentFolder() {
        Storage storage = storageAt("data", "tasks.txt"); // ./data does not exist yet

        storage.save(List.of(new Todo("a")));

        assertTrue(Files.exists(tempDir.resolve("data").resolve("tasks.txt")));
    }

    @Test
    public void save_overwritesPreviousContents() throws Exception {
        Storage storage = storageAt("tasks.txt");

        storage.save(List.of(new Todo("first save"), new Todo("also first save")));
        storage.save(List.of(new Todo("second save")));

        List<Task> reloaded = storage.load();
        assertEquals(1, reloaded.size());
        assertEquals("[T][ ] second save", reloaded.get(0).toString());
    }

    // ---- load: warnings reach the Ui, not just stdout ----

    @Test
    public void load_corruptLine_warningIsBuffered() throws IOException {
        writeSaveFile("tasks.txt", "X | 0 | unknown type tag");
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(SEED));
        Storage storage = new Storage(ui, tempDir.resolve("tasks.txt").toString());

        storage.load();

        assertEquals(1, ui.drainWarnings().size());
    }
}
