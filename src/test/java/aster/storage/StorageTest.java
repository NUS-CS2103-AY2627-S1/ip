package aster.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aster.exception.AsterException;
import aster.task.Deadline;
import aster.task.Event;
import aster.task.Task;
import aster.task.Todo;

/**
 * Tests {@link Storage#save(List)} and {@link Storage#load()}, the pair that implements
 * the on-disk task format.
 *
 * <p>The two methods are checked against each other through save and load round trips,
 * rather than against a restated copy of the format, so a change that breaks only one
 * half of the escaping is caught. Records that no save would ever produce are written by
 * hand instead, because the only way to reach the reader's rejection paths is to hand it
 * a file it did not write.
 *
 * <p>Every test works inside its own temporary directory, so no test can see another's
 * files and the real {@code data/aster.txt} is never read or written.
 */
class StorageTest {
    private static final String DATA_FILE_NAME = "aster.txt";

    @TempDir
    Path tempDir;

    // ---------- load: nothing saved yet ----------

    @Test
    void load_fileMissing_returnsEmptyListAndCreatesNoFile() throws AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);

        List<Task> loaded = new Storage(file).load();

        assertTrue(loaded.isEmpty());
        assertFalse(Files.exists(file), "load must not create the data file");
    }

    @Test
    void load_missingParentFolder_returnsEmptyList() throws AsterException {
        Path folder = tempDir.resolve("data");
        Path file = folder.resolve(DATA_FILE_NAME);

        List<Task> loaded = new Storage(file).load();

        assertTrue(loaded.isEmpty());
        assertFalse(Files.exists(folder), "load must not create the data folder");
    }

    @Test
    void load_emptyFile_returnsEmptyList() throws IOException, AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        writeLines(file);

        List<Task> loaded = new Storage(file).load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_blankLinesOnly_returnsEmptyList() throws IOException, AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        writeLines(file, "", "   ", "\t");

        List<Task> loaded = new Storage(file).load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_blankLineBetweenRecords_readsBothRecords() throws IOException, AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        writeLines(file, "T | 0 | a", "", "T | 1 | b");

        List<Task> loaded = new Storage(file).load();

        assertEquals(2, loaded.size());
        assertEquals("a", loaded.get(0).getDescription());
        assertFalse(loaded.get(0).isDone());
        assertEquals("b", loaded.get(1).getDescription());
        assertTrue(loaded.get(1).isDone());
    }

    @Test
    void load_eventEndingBeforeItStarts_readsItUnchanged() throws IOException, AsterException {
        // Refusing such an event is a check on what the user types, not on what was saved
        // earlier, so a file holding one must still open rather than lock the user out.
        Path file = tempDir.resolve(DATA_FILE_NAME);
        writeLines(file, "E | 0 | project meeting | 2019-08-08 | 2019-08-06");

        List<Task> loaded = new Storage(file).load();

        assertEquals(1, loaded.size());
        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(0));
        assertEquals(LocalDate.of(2019, 8, 8), loadedEvent.getFrom());
        assertEquals(LocalDate.of(2019, 8, 6), loadedEvent.getTo());
    }

    // ---------- save then load: what a task must survive ----------

    @Test
    void saveThenLoad_mixedTasks_preservesOrderTypeDoneAndDates() throws AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        Todo doneTodo = new Todo("wash up");
        doneTodo.markAsDone();
        Deadline doneDeadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        doneDeadline.markAsDone();
        List<Task> saved = List.of(
                new Todo("read book"),
                doneTodo,
                doneDeadline,
                new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 8)));

        new Storage(file).save(saved);
        // A second Storage, so the tasks can only have come back off the disk.
        List<Task> loaded = new Storage(file).load();

        assertEquals(4, loaded.size());

        Todo firstTodo = assertInstanceOf(Todo.class, loaded.get(0));
        assertEquals("read book", firstTodo.getDescription());
        assertFalse(firstTodo.isDone());

        Todo secondTodo = assertInstanceOf(Todo.class, loaded.get(1));
        assertEquals("wash up", secondTodo.getDescription());
        assertTrue(secondTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loaded.get(2));
        assertEquals("return book", loadedDeadline.getDescription());
        assertTrue(loadedDeadline.isDone());
        assertEquals(LocalDate.of(2019, 6, 6), loadedDeadline.getBy());

        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(3));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertFalse(loadedEvent.isDone());
        assertEquals(LocalDate.of(2019, 8, 6), loadedEvent.getFrom());
        assertEquals(LocalDate.of(2019, 8, 8), loadedEvent.getTo());
    }

    @Test
    void saveThenLoad_descriptionContainingSeparator_isPreserved() throws AsterException {
        assertDescriptionSurvivesRoundTrip("a | b");
    }

    @Test
    void saveThenLoad_descriptionWithBackslashAndSeparator_isPreserved() throws AsterException {
        // Holds a lone backslash, a separator, and a backslash already followed by a
        // separator, so an escaping change that only handles one of them is caught.
        assertDescriptionSurvivesRoundTrip("c:\\path | d\\|e");
    }

    @Test
    void saveThenLoad_descriptionEndingInBackslash_isPreserved() throws AsterException {
        // The one position where the reader's "escapes nothing" guard fires, so a writer
        // that stopped escaping would make load reject a file save had just written.
        assertDescriptionSurvivesRoundTrip("trailing\\");
    }

    // ---------- save: the file system side ----------

    @Test
    void save_missingParentFolder_createsFolderAndFile() throws AsterException {
        Path folder = tempDir.resolve("data");
        Path file = folder.resolve(DATA_FILE_NAME);

        new Storage(file).save(List.of(new Todo("read book")));

        assertTrue(Files.isDirectory(folder), "the first save must create the data folder");
        assertTrue(Files.isRegularFile(file));
        List<Task> loaded = new Storage(file).load();
        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
    }

    @Test
    void save_calledTwice_replacesContentAndLeavesNoTemporaryFile()
            throws IOException, AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        Storage storage = new Storage(file);
        storage.save(List.of(new Todo("one"), new Todo("two"), new Todo("three")));

        storage.save(List.of(new Todo("only")));

        List<Task> loaded = new Storage(file).load();
        assertEquals(1, loaded.size(), "a save replaces the file rather than adding to it");
        assertEquals("only", loaded.get(0).getDescription());
        assertEquals(List.of(DATA_FILE_NAME), fileNamesIn(tempDir),
                "no temporary file may be left behind");
    }

    @Test
    void save_unsupportedTaskType_throwsAndLeavesExistingFileUnchanged()
            throws IOException, AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        Storage storage = new Storage(file);
        storage.save(List.of(new Todo("keep me")));
        byte[] before = Files.readAllBytes(file);

        // Task is concrete, so a task of a type the format does not cover can be built
        // here without changing anything in the production code.
        assertThrows(AsterException.class, () -> storage.save(List.of(new Task("plain"))));

        assertArrayEquals(before, Files.readAllBytes(file),
                "a failed save must leave the previously saved file exactly as it was");
        assertEquals(List.of(DATA_FILE_NAME), fileNamesIn(tempDir),
                "no temporary file may be left behind");
    }

    // ---------- load: records no save would produce ----------

    @Test
    void load_lineWithTooFewFields_throws() throws IOException {
        assertLineIsRejected("T | 0");
    }

    @Test
    void load_deadlineMissingDateField_throws() throws IOException {
        assertLineIsRejected("D | 0 | return book");
    }

    @Test
    void load_todoWithExtraField_throws() throws IOException {
        // Paired with the deadline case above: together they require the field count to
        // match its type exactly, rather than merely reaching it.
        assertLineIsRejected("T | 0 | read book | 2019-06-06");
    }

    @Test
    void load_unknownTypeTag_throws() throws IOException {
        assertLineIsRejected("X | 0 | read book");
    }

    @Test
    void load_unknownDoneFlag_throws() throws IOException {
        assertLineIsRejected("T | 2 | read book");
    }

    @Test
    void load_emptyDescription_throws() throws IOException {
        assertLineIsRejected("T | 0 | ");
    }

    @Test
    void load_impossibleStoredDate_throws() throws IOException {
        // 2019 is not a leap year, so a lenient reader would quietly load this as 28 Feb.
        assertLineIsRejected("D | 0 | return book | 2019-02-29");
    }

    @Test
    void load_lineEndingInLoneBackslash_throws() throws IOException {
        assertLineIsRejected("T | 0 | read book\\");
    }

    // ---------- helpers ----------

    /**
     * Checks that a description is the same after being saved and read back.
     *
     * @param description the description to put through a save and a load.
     * @throws AsterException if the round trip fails, which fails the calling test.
     */
    private void assertDescriptionSurvivesRoundTrip(String description) throws AsterException {
        Path file = tempDir.resolve(DATA_FILE_NAME);

        new Storage(file).save(List.of(new Todo(description)));
        List<Task> loaded = new Storage(file).load();

        assertEquals(1, loaded.size());
        assertEquals(description, loaded.get(0).getDescription());
    }

    /**
     * Checks that a file holding the given single line is refused rather than read.
     *
     * @param line the stored line to write and try to read back.
     * @throws IOException if the line cannot be written, which fails the calling test.
     */
    private void assertLineIsRejected(String line) throws IOException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        writeLines(file, line);

        assertThrows(AsterException.class, () -> new Storage(file).load());
    }

    /**
     * Writes the given lines to a file, each ended by a newline.
     *
     * <p>The line ending is written directly rather than taken from the system, so these
     * tests read the same file whichever operating system they run on.
     *
     * @param file the file to write.
     * @param lines the lines to write; none at all writes an empty file.
     * @throws IOException if the file cannot be written.
     */
    private static void writeLines(Path file, String... lines) throws IOException {
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append('\n');
        }
        Files.writeString(file, content.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Returns the names of everything directly inside a folder, in alphabetical order.
     *
     * @param folder the folder to look in.
     * @return the names of its entries, sorted so the result can be compared directly.
     * @throws IOException if the folder cannot be listed.
     */
    private static List<String> fileNamesIn(Path folder) throws IOException {
        List<String> names = new ArrayList<>();
        try (Stream<Path> entries = Files.list(folder)) {
            entries.forEach(entry -> names.add(entry.getFileName().toString()));
        }
        Collections.sort(names);
        return names;
    }
}
