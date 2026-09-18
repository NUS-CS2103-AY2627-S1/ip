package myriad.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import myriad.MyriadException;
import myriad.task.Deadline;
import myriad.task.Event;
import myriad.task.Task;
import myriad.task.TaskDateTime;
import myriad.task.TaskList;
import myriad.task.ToDo;

/**
 * Tests for {@link Storage}, which owns the on-disk save format. Two
 * things matter here: that a task written out comes back identical on the
 * next launch, and that a damaged line costs the user one task rather
 * than the whole file.
 * <p>
 * Every test writes inside a JUnit-managed temporary directory, so the
 * real data file at {@code data/myriad.txt} is never touched.
 */
public class StorageTest {

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Builds a TaskDateTime, unwrapping the checked exception for brevity. */
    private static TaskDateTime at(String raw) {
        try {
            return TaskDateTime.parse(raw);
        } catch (MyriadException e) {
            throw new AssertionError("test fixture should parse: " + raw, e);
        }
    }

    /** Path to a data file inside the temp directory, not yet created. */
    private Path dataFile() {
        return tempDir.resolve("myriad.txt");
    }

    /** Writes the given lines to the data file, newline-separated. */
    private void writeDataFile(String... lines) throws IOException {
        Files.writeString(dataFile(), String.join("\n", lines) + "\n");
    }

    /** A Storage pointed at the temp data file. */
    private Storage storageAtTempFile() {
        return new Storage(dataFile().toString());
    }

    /** Returns the names of everything in the temp directory, sorted. */
    private List<String> listFileNames() throws IOException {
        try (Stream<Path> entries = Files.list(tempDir)) {
            return entries.map(entry -> entry.getFileName().toString()).sorted().toList();
        }
    }

    // ---------------------------------------------------------------
    // load: the file may legitimately not be there
    // ---------------------------------------------------------------

    @Test
    public void load_fileDoesNotExist_emptyResultReturned() throws MyriadException {
        // A first run has nothing saved yet. That is not an error, so it must
        // not produce a warning either.
        LoadResult result = storageAtTempFile().load();
        assertEquals(0, result.tasks().size());
        assertEquals(0, result.skippedLines().size());
    }

    @Test
    public void load_emptyFile_emptyResultReturned() throws IOException, MyriadException {
        Files.writeString(dataFile(), "");
        LoadResult result = storageAtTempFile().load();
        assertEquals(0, result.tasks().size());
        assertEquals(0, result.skippedLines().size());
    }

    @Test
    public void load_pathIsADirectory_exceptionThrown() {
        // The path exists but cannot be opened as a file -- a real failure,
        // unlike the file simply not being there yet.
        Path directory = tempDir.resolve("subdir");
        assertThrows(MyriadException.class, () -> {
            Files.createDirectory(directory);
            new Storage(directory.toString()).load();
        });
    }

    // ---------------------------------------------------------------
    // save then load: the round trip the whole app depends on
    // ---------------------------------------------------------------

    @Test
    public void saveThenLoad_oneOfEachTaskType_allRestored() throws Exception {
        TaskList original = new TaskList();
        original.add(new ToDo("read book"));
        original.add(new Deadline("homework", at("2019-12-02 1800")));
        original.add(new Event("party", at("2019-12-02 1400"), at("2019-12-02 1600")));

        storageAtTempFile().save(original);
        List<Task> reloaded = storageAtTempFile().load().tasks();

        assertEquals(3, reloaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), reloaded.get(i).toString(),
                    "task " + i + " changed across the save/load round trip");
        }
    }

    @Test
    public void saveThenLoad_doneStatePreserved() throws Exception {
        TaskList original = new TaskList();
        original.add(new ToDo("done task"));
        original.add(new ToDo("not done task"));
        original.markDone(0);

        storageAtTempFile().save(original);
        List<Task> reloaded = storageAtTempFile().load().tasks();

        assertTrue(reloaded.get(0).toString().startsWith("[T][X]"));
        assertTrue(reloaded.get(1).toString().startsWith("[T][ ]"));
    }

    @Test
    public void saveThenLoad_dateOnlyAndTimedDatesStayDistinct() throws Exception {
        // The date-only vs midnight distinction must survive a restart, or a
        // whole-day deadline would come back as a deadline at 00:00.
        TaskList original = new TaskList();
        original.add(new Deadline("whole day", at("2019-12-02")));
        original.add(new Deadline("at midnight", at("2019-12-02 0000")));

        storageAtTempFile().save(original);
        List<Task> reloaded = storageAtTempFile().load().tasks();

        assertEquals("[D][ ] whole day (by: Dec 02 2019)", reloaded.get(0).toString());
        assertEquals("[D][ ] at midnight (by: Dec 02 2019 0000)", reloaded.get(1).toString());
    }

    @Test
    public void saveThenLoad_emptyList_nothingRestored() throws Exception {
        storageAtTempFile().save(new TaskList());
        assertEquals(0, storageAtTempFile().load().tasks().size());
    }

    // ---------------------------------------------------------------
    // save: what actually lands on disk
    // ---------------------------------------------------------------

    @Test
    public void save_tasks_writesOneLinePerTask() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));

        storageAtTempFile().save(tasks);

        // The separator is a hardcoded "\n", not the platform line separator,
        // so this assertion holds on Windows too.
        assertEquals("T | 0 | first\nT | 0 | second\n", Files.readString(dataFile()));
    }

    @Test
    public void save_calledTwice_fileOverwrittenNotAppended() throws Exception {
        Storage storage = storageAtTempFile();
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));

        storage.save(tasks);
        storage.save(tasks);

        assertEquals("T | 0 | first\n", Files.readString(dataFile()));
    }

    @Test
    public void save_emptyList_fileTruncated() throws Exception {
        Storage storage = storageAtTempFile();
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("will be deleted"));
        storage.save(tasks);

        // Deleting the last task must clear the file, not leave the old
        // contents behind to be reloaded on the next launch.
        storage.save(new TaskList());

        assertEquals("", Files.readString(dataFile()));
    }

    @Test
    public void save_parentDirectoryMissing_directoryCreated() throws Exception {
        Path nested = tempDir.resolve("data").resolve("myriad.txt");
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        new Storage(nested.toString()).save(tasks);

        assertTrue(Files.exists(nested), "save should create the missing parent directory");
    }

    @Test
    public void saveThenLoad_bareFileNameWithoutFolder_roundTrips() throws Exception {
        // A path with no folder has no parent directory to create, which save
        // must skip rather than fail on. A bare name is relative to the working
        // directory, which build.gradle points at a scratch folder in build/.
        // The name is made unique and deleted afterwards, so reruns never clash.
        Path bareName = Path.of("storage-test-" + System.nanoTime() + ".txt");
        assertEquals(null, bareName.getParent());
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        try {
            new Storage(bareName.toString()).save(tasks);
            List<Task> reloaded = new Storage(bareName.toString()).load().tasks();

            assertEquals(1, reloaded.size());
            assertEquals("[T][ ] read book", reloaded.get(0).toString());
        } finally {
            Files.deleteIfExists(bareName);
        }
    }

    @Test
    public void save_nonAsciiDescription_writtenAsUtf8() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("caf" + (char) 0xE9));

        storageAtTempFile().save(tasks);

        // "é" is the two bytes C3 A9 in UTF-8, whatever the platform default.
        byte[] expected = {'T', ' ', '|', ' ', '0', ' ', '|', ' ', 'c', 'a', 'f', (byte) 0xC3, (byte) 0xA9, '\n'};
        assertArrayEquals(expected, Files.readAllBytes(dataFile()));
    }

    @Test
    public void save_parentPathIsAFile_ioExceptionThrown() throws Exception {
        // The data file's directory can't be created where a file already
        // sits, and that has to surface as an IOException the caller reports.
        Files.writeString(dataFile(), "");
        Storage storage = new Storage(dataFile().resolve("myriad.txt").toString());
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
    }

    @Test
    public void save_pathIsADirectory_ioExceptionThrown() throws Exception {
        Path directory = tempDir.resolve("subdir");
        Files.createDirectory(directory);
        assertThrows(IOException.class, () -> new Storage(directory.toString()).save(new TaskList()));
        // The empty directory must not have been replaced by a file.
        assertTrue(Files.isDirectory(directory));
    }

    // ---------------------------------------------------------------
    // save: the data file is replaced in one step, never half-written
    // ---------------------------------------------------------------

    @Test
    public void save_successful_noTemporaryFileLeftBehind() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        storageAtTempFile().save(tasks);
        storageAtTempFile().save(tasks);

        assertEquals(List.of("myriad.txt"), listFileNames());
    }

    @Test
    public void save_temporaryFileLeftByEarlierCrash_replacedAndRemoved() throws Exception {
        // A crash between writing the temporary file and moving it into place
        // leaves it behind; the next save must reuse it, not be blocked by it.
        Files.writeString(tempDir.resolve("myriad.txt.tmp"), "half-written junk");
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        storageAtTempFile().save(tasks);

        assertEquals("T | 0 | read book\n", Files.readString(dataFile()));
        assertEquals(List.of("myriad.txt"), listFileNames());
    }

    @Test
    public void save_temporaryFileCannotBeWritten_dataFileUnchanged() throws Exception {
        // A directory where the temporary file should go makes the write
        // fail, standing in for a full disk part-way through a save. The
        // previous save's contents must survive intact.
        writeDataFile("T | 1 | saved earlier");
        Path blockingDirectory = Files.createDirectory(tempDir.resolve("myriad.txt.tmp"));
        Files.writeString(blockingDirectory.resolve("keeps it non-empty"), "");
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("never saved"));

        assertThrows(IOException.class, () -> storageAtTempFile().save(tasks));

        assertEquals("T | 1 | saved earlier\n", Files.readString(dataFile()));
    }

    // ---------------------------------------------------------------
    // load: a damaged line costs one task, not the file
    // ---------------------------------------------------------------

    @Test
    public void load_someLinesMalformed_validLinesStillLoaded() throws Exception {
        writeDataFile(
                "T | 0 | first",
                "this line is nonsense",
                "T | 0 | second");

        LoadResult result = storageAtTempFile().load();

        assertEquals(2, result.tasks().size());
        assertEquals(1, result.skippedLines().size());
    }

    @Test
    public void load_malformedLine_reportedWithOneBasedLineNumber() throws Exception {
        writeDataFile(
                "T | 0 | first",
                "T | 0 | second",
                "broken");

        List<String> skipped = storageAtTempFile().load().skippedLines();

        assertEquals(1, skipped.size());
        assertTrue(skipped.get(0).startsWith("line 3:"),
                "line numbers should be 1-based and match the file: " + skipped.get(0));
    }

    @Test
    public void load_severalMalformedLines_allReported() throws Exception {
        writeDataFile("broken one", "T | 0 | fine", "broken two");
        assertEquals(2, storageAtTempFile().load().skippedLines().size());
    }

    @Test
    public void load_windowsAndOldMacLineEndings_everyLineLoaded() throws Exception {
        // A data file edited on Windows may end its lines in "\r\n", and a
        // very old editor in a bare "\r"; neither may leave a stray "\r" in a
        // task or merge two lines.
        Files.writeString(dataFile(), "T | 0 | first\r\nT | 1 | second\rT | 0 | third\n");

        LoadResult result = storageAtTempFile().load();

        assertEquals(3, result.tasks().size());
        assertEquals("[T][X] second", result.tasks().get(1).toString());
        assertEquals(0, result.skippedLines().size());
    }

    @Test
    public void load_bytesNotValidUtf8_everyLineStillLoaded() throws Exception {
        // A file re-saved in another encoding, such as Windows-1252 from an
        // old Notepad, holds bytes that aren't valid UTF-8. Each one becomes a
        // replacement character, and every line, including the ones around
        // it, still loads, instead of the whole file quietly coming back empty.
        byte[] content = ("T | 0 | first\nT | 0 | caf" + (char) 0xE9 + "\nT | 0 | third\n")
                .getBytes(StandardCharsets.ISO_8859_1);
        Files.write(dataFile(), content);

        LoadResult result = storageAtTempFile().load();

        assertEquals(3, result.tasks().size());
        assertEquals("[T][ ] caf" + (char) 0xFFFD, result.tasks().get(1).toString());
        assertEquals("[T][ ] third", result.tasks().get(2).toString());
    }

    @Test
    public void saveThenLoad_descriptionWithUnicodeLineSeparators_restoredIntact() throws Exception {
        // U+2028 and U+0085 are line separators to some readers, but save()
        // never ends a line with them, so load() must not split on them.
        String description = "a" + (char) 0x2028 + "b" + (char) 0x0085 + "c";
        TaskList original = new TaskList();
        original.add(new ToDo(description));
        original.add(new ToDo("next"));

        storageAtTempFile().save(original);
        LoadResult result = storageAtTempFile().load();

        assertEquals(2, result.tasks().size());
        assertEquals("[T][ ] " + description, result.tasks().get(0).toString());
        assertEquals(0, result.skippedLines().size());
    }

    @Test
    public void load_blankLine_skippedWithFieldCountMessage() throws Exception {
        writeDataFile("T | 0 | first", "", "T | 0 | second");

        LoadResult result = storageAtTempFile().load();

        assertEquals(2, result.tasks().size());
        assertTrue(result.skippedLines().get(0).contains("found 1"),
                "a blank line splits into a single empty field: " + result.skippedLines().get(0));
    }

    @Test
    public void load_lineWithEmptyDescription_skipped() throws Exception {
        // String.split discards trailing empty fields, so "T | 0 | " is two
        // fields, not three -- an empty description is rejected as a short
        // line rather than loading a nameless task.
        writeDataFile("T | 0 | ");

        LoadResult result = storageAtTempFile().load();

        assertEquals(0, result.tasks().size());
        assertTrue(result.skippedLines().get(0).contains("found 2"));
    }

    @Test
    public void load_unknownTypeLetter_skipped() throws Exception {
        writeDataFile("X | 0 | mystery");

        LoadResult result = storageAtTempFile().load();

        assertEquals(0, result.tasks().size());
        assertTrue(result.skippedLines().get(0).contains("unknown task type"));
    }

    @Test
    public void load_lowercaseTypeLetter_skipped() throws Exception {
        // Type letters are matched exactly; unlike command keywords, the save
        // format is not case-insensitive.
        writeDataFile("t | 0 | read book");
        assertTrue(storageAtTempFile().load().skippedLines().get(0).contains("unknown task type"));
    }

    @Test
    public void load_leadingWhitespaceBeforeType_skipped() throws Exception {
        // The split trims around the "|" separators but not before the first
        // field, so an indented line has a type of " T" rather than "T".
        writeDataFile(" T | 0 | read book");
        assertTrue(storageAtTempFile().load().skippedLines().get(0).contains("unknown task type"));
    }

    @Test
    public void load_deadlineMissingDateField_skipped() throws Exception {
        writeDataFile("D | 0 | homework");

        LoadResult result = storageAtTempFile().load();

        assertEquals(0, result.tasks().size());
        assertTrue(result.skippedLines().get(0).contains("needs a 4th field"));
    }

    @Test
    public void load_eventMissingEndField_skipped() throws Exception {
        writeDataFile("E | 0 | party | 2019-12-02T14:00");

        LoadResult result = storageAtTempFile().load();

        assertEquals(0, result.tasks().size());
        assertTrue(result.skippedLines().get(0).contains("needs 5 fields"));
    }

    @Test
    public void load_deadlineWithUnparseableDate_skipped() throws Exception {
        writeDataFile("D | 0 | homework | not-a-date");

        LoadResult result = storageAtTempFile().load();

        assertEquals(0, result.tasks().size());
        assertTrue(result.skippedLines().get(0).contains("doesn't look like a date/time I understand"));
    }

    @Test
    public void load_eventWithUnparseableEndDate_skipped() throws Exception {
        writeDataFile("E | 0 | party | 2019-12-02T14:00 | not-a-date");
        assertEquals(0, storageAtTempFile().load().tasks().size());
    }

    @Test
    public void load_eventEndingBeforeItStarts_skipped() throws Exception {
        // A hand-edited file must not bring back an event the Parser would reject.
        writeDataFile("E | 0 | party | 2019-12-05 | 2019-12-01", "T | 0 | read book");

        LoadResult result = storageAtTempFile().load();

        assertEquals(1, result.tasks().size());
        assertTrue(result.skippedLines().get(0).contains("An event must end after it starts"));
    }

    // ---------------------------------------------------------------
    // load: the done flag and extra fields
    // ---------------------------------------------------------------

    @Test
    public void load_doneFlagIsOne_taskLoadedAsDone() throws Exception {
        writeDataFile("T | 1 | read book");
        assertTrue(storageAtTempFile().load().tasks().get(0).toString().startsWith("[T][X]"));
    }

    @Test
    public void load_doneFlagIsZero_taskLoadedAsNotDone() throws Exception {
        writeDataFile("T | 0 | read book");
        assertTrue(storageAtTempFile().load().tasks().get(0).toString().startsWith("[T][ ]"));
    }

    @Test
    public void load_doneFlagIsUnexpectedValue_treatedAsNotDone() throws Exception {
        // The check is an exact match against "1", so anything else means not
        // done. A corrupted flag loses the done state silently rather than
        // costing the user the whole task -- the lesser of the two evils, but
        // worth stating.
        writeDataFile("T | true | read book", "T | 2 | write essay");

        LoadResult result = storageAtTempFile().load();

        assertEquals(2, result.tasks().size());
        assertEquals(0, result.skippedLines().size());
        assertTrue(result.tasks().get(0).toString().startsWith("[T][ ]"));
        assertTrue(result.tasks().get(1).toString().startsWith("[T][ ]"));
    }

    @Test
    public void load_extraTrailingFields_ignored() throws Exception {
        writeDataFile("T | 0 | read book | unexpected extra");

        LoadResult result = storageAtTempFile().load();

        assertEquals(1, result.tasks().size());
        assertEquals(0, result.skippedLines().size());
        assertEquals("[T][ ] read book", result.tasks().get(0).toString());
    }

    @Test
    public void load_separatorWithoutSurroundingSpaces_accepted() throws Exception {
        // The split tolerates any spacing around "|", so a hand-edited file
        // still loads.
        writeDataFile("T|0|read book");
        assertEquals("[T][ ] read book", storageAtTempFile().load().tasks().get(0).toString());
    }

    // ---------------------------------------------------------------
    // Known limitation: the save format has no escaping. The Parser
    // rejects "|" in descriptions so a user cannot reach this, but Storage
    // itself does not guard against it, as these tests record.
    // ---------------------------------------------------------------

    @Test
    public void saveThenLoad_descriptionContainingSeparator_descriptionTruncated() throws Exception {
        // Known limitation: "|" separates fields and nothing escapes it, so a
        // description containing one is split into several fields on the way
        // back in. Here "a | b" is saved as "T | 0 | a | b", which reloads as
        // a ToDo called "a" with "b" silently discarded as an extra field.
        // A task built directly, bypassing the Parser, still reaches this.
        TaskList original = new TaskList();
        original.add(new ToDo("a | b"));

        storageAtTempFile().save(original);
        List<Task> reloaded = storageAtTempFile().load().tasks();

        assertEquals(1, reloaded.size());
        assertEquals("[T][ ] a", reloaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_deadlineDescriptionContainingSeparator_dateLost() throws Exception {
        // Known limitation: the same cause, but worse for a Deadline -- the
        // stray "|" shifts every later field along, so the date field now
        // holds the rest of the description and fails to parse. The task is
        // skipped entirely rather than merely truncated.
        TaskList original = new TaskList();
        original.add(new Deadline("submit a | b", at("2019-12-02")));

        storageAtTempFile().save(original);
        LoadResult result = storageAtTempFile().load();

        assertEquals(0, result.tasks().size());
        assertEquals(1, result.skippedLines().size());
    }
}
