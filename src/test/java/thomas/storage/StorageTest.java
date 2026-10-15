package thomas.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.ThomasException;
import thomas.task.DeadlineTask;
import thomas.task.EventTask;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.TodoTask;

/**
 * Tests Storage.
 * This is the class where a bug costs the user something they cannot get back.
 * Every other mistake is visible on screen and survives until the next command,
 * while a save file written or read wrongly loses tasks silently between one run
 * and the next.
 * Decoding is the part with the decisions in it, and it is deliberately strict:
 * each task type is written with an exact number of fields, so a description
 * holding the field separator is refused rather than loaded back truncated. A
 * damaged line is skipped and complained about rather than abandoning the file,
 * so the cases below check both halves of that.
 * The decoder itself is private, and is reached here through load against a real
 * file in a temporary folder that JUnit creates and removes, so no case can see
 * another's file.
 */
public class StorageTest {

    /** Folder JUnit makes fresh for each test and deletes afterwards */
    @TempDir
    private Path folder;

    /**
     * Returns the save file each case works on, inside the temporary folder.
     *
     * @return Path to this case's save file.
     */
    private Path saveFile() {
        return folder.resolve("tasklist.txt");
    }

    /**
     * Returns a storage over this case's save file.
     *
     * @return New storage.
     */
    private Storage storage() {
        return new Storage(saveFile().toString());
    }

    /**
     * Writes the given lines to the save file as the whole of its contents.
     *
     * @param lines Lines to write.
     * @throws IOException If the file cannot be written.
     */
    private void writeSaveFile(String... lines) throws IOException {
        Files.write(saveFile(), List.of(lines));
    }

    /**
     * Returns the save file's lines, ignoring how they were separated.
     *
     * @return Lines the file holds, in order.
     * @throws IOException If the file cannot be read.
     */
    private List<String> readSaveFile() throws IOException {
        return Files.readAllLines(saveFile());
    }

    // ---- load: the file itself ----

    @Test
    public void load_missingFile_returnsEmptyList() throws IOException {
        // A missing file is a first run, not a failure.
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getSkipComplaints().isEmpty());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws IOException {
        writeSaveFile();
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getSkipComplaints().isEmpty());
    }

    @Test
    public void load_blankLines_skippedWithoutComplaint() throws IOException {
        // Blank lines are passed over quietly: they are not damage worth reporting.
        writeSaveFile("", "T | 0 | read book", "   ");
        Storage storage = storage();

        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertTrue(storage.getSkipComplaints().isEmpty());
    }

    // ---- load: lines that decode ----

    @Test
    public void load_todoLine_returnsTodo() throws IOException {
        writeSaveFile("T | 0 | read book");

        assertEquals("[T][ ] read book", storage().load().get(0).toString());
    }

    @Test
    public void load_deadlineLine_returnsDeadline() throws IOException {
        writeSaveFile("D | 0 | return book | 2019-12-02 1800");

        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)",
                storage().load().get(0).toString());
    }

    @Test
    public void load_eventLine_returnsEvent() throws IOException {
        writeSaveFile("E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600");

        assertEquals("[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                storage().load().get(0).toString());
    }

    @Test
    public void load_doneFlagSet_taskIsDone() throws IOException {
        writeSaveFile("T | 1 | read book");

        assertEquals("[T][X] read book", storage().load().get(0).toString());
    }

    @Test
    public void load_doneFlagClear_taskIsNotDone() throws IOException {
        writeSaveFile("T | 0 | read book");

        assertEquals("[T][ ] read book", storage().load().get(0).toString());
    }

    @Test
    public void load_damagedDoneFlag_taskLoadsAsNotDone() throws IOException {
        // Only "1" means done, and anything else is read as not done rather than as a corrupt line: a damaged flag
        // costs the tick, not the whole task.
        writeSaveFile("T | x | read book");
        Storage storage = storage();

        ArrayList<Task> tasks = storage.load();

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertTrue(storage.getSkipComplaints().isEmpty());
    }

    @Test
    public void load_severalLines_returnsThemInFileOrder() throws IOException {
        writeSaveFile("T | 0 | read book",
                "D | 1 | return book | 2019-12-02 1800",
                "T | 0 | buy book");

        ArrayList<Task> tasks = storage().load();

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[D][X] return book (by: Dec 02 2019, 6:00 PM)", tasks.get(1).toString());
        assertEquals("[T][ ] buy book", tasks.get(2).toString());
    }

    // ---- load: lines that do not decode ----

    @Test
    public void load_unknownTypeLetter_lineSkipped() throws IOException {
        writeSaveFile("X | 0 | read book");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("unknown task type 'X': X | 0 | read book"),
                storage.getSkipComplaints());
    }

    @Test
    public void load_tooFewFields_lineSkipped() throws IOException {
        writeSaveFile("T | 0");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("too few fields: T | 0"), storage.getSkipComplaints());
    }

    @Test
    public void load_descriptionContainingSeparator_lineSkipped() throws IOException {
        // The field count is exact rather than a minimum, which is what catches a description containing the
        // separator: it splits into an extra field, and loading it back would silently truncate the description.
        writeSaveFile("T | 0 | read book | and return it");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("expected 3 fields but found 4: T | 0 | read book | and return it"),
                storage.getSkipComplaints());
    }

    @Test
    public void load_trailingSeparator_lineSkipped() throws IOException {
        // Splitting keeps trailing empty fields, so a line ending in a separator is reported rather than quietly
        // losing its last field.
        writeSaveFile("T | 0 | read book | ");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("expected 3 fields but found 4: T | 0 | read book | "),
                storage.getSkipComplaints());
    }

    @Test
    public void load_deadlineMissingDate_lineSkipped() throws IOException {
        writeSaveFile("D | 0 | return book");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("expected 4 fields but found 3: D | 0 | return book"),
                storage.getSkipComplaints());
    }

    @Test
    public void load_eventMissingEndDate_lineSkipped() throws IOException {
        writeSaveFile("E | 0 | project meeting | 2019-12-02 1400");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("expected 5 fields but found 4: E | 0 | project meeting | 2019-12-02 1400"),
                storage.getSkipComplaints());
    }

    @Test
    public void load_unreadableDate_lineSkipped() throws IOException {
        // A date the parser cannot read is reported in the same words the user sees.
        writeSaveFile("D | 0 | return book | tomorrow");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("I can't read 'tomorrow' as a deadline date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800."),
                storage.getSkipComplaints());
    }

    @Test
    public void load_eventEndingBeforeItStarts_lineSkipped() throws IOException {
        // Loading goes through the event constructor, so a line edited by hand to run backwards is refused here too
        // rather than loading an event no command could have created.
        writeSaveFile("E | 0 | project meeting | 2019-12-02 1600 | 2019-12-02 1400");
        Storage storage = storage();

        assertTrue(storage.load().isEmpty());
        assertEquals(List.of("Bust my buffers! Your event arrives before it sets off! Check its /from and /to."),
                storage.getSkipComplaints());
    }

    @Test
    public void load_blankDescription_lineSkipped() throws IOException {
        // Refused here rather than reaching Task's constructor, whose assertion on a blank description would end
        // start-up for the one damaged line.
        writeSaveFile("T | 0 |   ", "T | 0 | read book");
        Storage storage = storage();

        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(List.of("empty description: T | 0 |   "), storage.getSkipComplaints());
    }

    @Test
    public void load_dayNotOnTheCalendar_lineSkipped() throws IOException {
        // The loader reads dates through the same strict helper as the parser, so an edited-in 30th of February is
        // reported rather than quietly loaded as the 28th.
        writeSaveFile("D | 0 | return book | 2019-02-30 1800");
        Storage storage = storage();

        ArrayList<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
        assertEquals(List.of("There's no such moment as '2019-02-30 1800' for a deadline date! "
                + "Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359."),
                storage.getSkipComplaints());
    }

    @Test
    public void load_lineRepeatingAnEarlierOne_secondCopySkipped() throws IOException {
        // The first copy is kept and the repeat reported: loading both would put on the list what typing the
        // second is refused. The done flag makes no difference to being a repeat.
        writeSaveFile("T | 0 | read book", "T | 1 | read book", "T | 0 | return book");
        Storage storage = storage();

        ArrayList<Task> tasks = storage.load();

        assertEquals(List.of("[T][ ] read book", "[T][ ] return book"),
                tasks.stream().map(Task::toString).toList());
        assertEquals(List.of("repeats an earlier line: T | 1 | read book"), storage.getSkipComplaints());
    }

    @Test
    public void load_sameDescriptionDifferentTypes_bothLoad() throws IOException {
        writeSaveFile("T | 0 | read book", "D | 0 | read book | 2019-12-02 1800");
        Storage storage = storage();

        assertEquals(2, storage.load().size());
        assertTrue(storage.getSkipComplaints().isEmpty());
    }

    @Test
    public void load_someLinesDamaged_othersStillLoad() throws IOException {
        // One damaged line must not cost the user every other task in the file.
        writeSaveFile("T | 0 | read book",
                "X | 0 | nonsense",
                "T | 0 | buy book");
        Storage storage = storage();

        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] buy book", tasks.get(1).toString());
        assertEquals(1, storage.getSkipComplaints().size());
    }

    @Test
    public void load_severalLinesDamaged_complaintsInFileOrder() throws IOException {
        // Complaints come back in the order the lines appeared.
        writeSaveFile("X | 0 | first", "T | 0", "Y | 0 | third");
        Storage storage = storage();

        storage.load();

        assertEquals(List.of("unknown task type 'X': X | 0 | first",
                "too few fields: T | 0",
                "unknown task type 'Y': Y | 0 | third"),
                storage.getSkipComplaints());
    }

    @Test
    public void load_calledTwice_reportsOnlyTheLastLoad() throws IOException {
        // The complaints are cleared at the start of each load, so a second load reports what that load skipped rather
        // than everything ever skipped.
        writeSaveFile("X | 0 | nonsense");
        Storage storage = storage();
        storage.load();

        writeSaveFile("T | 0 | read book");
        storage.load();

        assertTrue(storage.getSkipComplaints().isEmpty());
    }

    // ---- save ----

    @Test
    public void save_severalTasks_writesOneLinePerTask() throws IOException, ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));
        tasks.add(new DeadlineTask("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new EventTask("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0)));

        storage().save(tasks);

        assertEquals(List.of("T | 0 | read book",
                "D | 0 | return book | 2019-12-02 1800",
                "E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600"),
                readSaveFile());
    }

    @Test
    public void save_doneTask_writesTheDoneFlag() throws IOException, ThomasException {
        TaskList tasks = new TaskList();
        Task task = new TodoTask("read book");
        task.markAsDone();
        tasks.add(task);

        storage().save(tasks);

        assertEquals(List.of("T | 1 | read book"), readSaveFile());
    }

    @Test
    public void save_emptyList_writesEmptyFile() throws IOException {
        storage().save(new TaskList());

        assertTrue(readSaveFile().isEmpty());
    }

    @Test
    public void save_overExistingFile_replacesItsContents() throws IOException, ThomasException {
        // Saving replaces the file, so tasks deleted in this run do not come back.
        writeSaveFile("T | 0 | read book", "T | 0 | return book");
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("buy book"));

        storage().save(tasks);

        assertEquals(List.of("T | 0 | buy book"), readSaveFile());
    }

    @Test
    public void save_anyList_leavesTheListUnchanged() throws IOException, ThomasException {
        // Saving runs after every change to the task list, so it must only read the list: emptying it here would
        // delete the tasks it is meant to be saving.
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        storage().save(tasks);

        assertEquals(1, tasks.size());
    }

    @Test
    public void save_missingFolder_createsIt() throws IOException, ThomasException {
        // The folder a save file sits in is created if it is not there yet.
        Path nested = folder.resolve("data").resolve("tasklist.txt");
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        new Storage(nested.toString()).save(tasks);

        assertTrue(new File(nested.toString()).exists());
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(nested));
    }

    @Test
    public void save_severalMissingFolderLevels_createsThemAll() throws IOException, ThomasException {
        // mkdirs rather than mkdir: every missing level is made, not only the last.
        Path nested = folder.resolve("a").resolve("b").resolve("c").resolve("tasklist.txt");
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));

        new Storage(nested.toString()).save(tasks);

        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(nested));
    }

    @Test
    public void save_fileWhereTheFolderShouldBe_exceptionNamesTheFolder() throws IOException {
        // A plain file sitting where ./data should go means the folder cannot be made. Said in words, rather than
        // FileWriter's "No such file or directory" for the save file -- which points away from the fault.
        Path inTheWay = folder.resolve("data");
        Files.writeString(inTheWay, "not a folder");
        Storage storage = new Storage(inTheWay.resolve("tasklist.txt").toString());

        IOException e = assertThrows(IOException.class, () -> storage.save(new TaskList()));

        assertEquals("I couldn't make the folder '" + inTheWay + "'. Is there a file with that name in the way?",
                e.getMessage());
    }

    @Test
    public void save_folderWhereTheFileShouldBe_exceptionNamesTheFile() throws IOException {
        Files.createDirectory(saveFile());

        IOException e = assertThrows(IOException.class, () -> storage().save(new TaskList()));

        assertEquals("'" + saveFile() + "' is a folder, and I need it to be a file. Move the folder out of the way.",
                e.getMessage());
    }

    @Test
    public void load_folderWhereTheFileShouldBe_exceptionNamesTheFile() throws IOException {
        // The same wrong thing on the reading side, so the first run against it is told in words too.
        Files.createDirectory(saveFile());

        IOException e = assertThrows(IOException.class, () -> storage().load());

        assertEquals("'" + saveFile() + "' is a folder, and I need it to be a file. Move the folder out of the way.",
                e.getMessage());
    }

    // ---- the two sides together ----

    @Test
    public void saveThenLoad_everyTaskType_survivesTheRoundTrip() throws IOException, ThomasException {
        // The point of the whole class: whatever is written can be read back as the same tasks. Encoding lives on the
        // tasks and decoding lives in the storage, so only a test that runs both catches the two drifting apart, since
        // each side on its own is self-consistent.
        TaskList original = new TaskList();
        Task todo = new TodoTask("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new DeadlineTask("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        original.add(new EventTask("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 4, 16, 0)));
        Storage storage = storage();

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertTrue(storage.getSkipComplaints().isEmpty());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), loaded.get(i).toString());
        }
    }

    @Test
    public void saveThenLoad_nonAsciiDescription_survives() throws IOException, ThomasException {
        // Chinese characters and an umlaut: two bytes and up each in UTF-8. The
        // JVM's default charset has been UTF-8 on every platform since Java
        // 18, which is what FileWriter and Scanner(File) both follow; this
        // would fail first if either were given a narrower charset.
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("读书"));
        tasks.add(new TodoTask("Bücher lesen"));

        storage().save(tasks);
        ArrayList<Task> loaded = storage().load();

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] 读书", loaded.get(0).toString());
        assertEquals("[T][ ] Bücher lesen", loaded.get(1).toString());
    }

    @Test
    public void saveThenLoad_descriptionWithSpacesAndPunctuation_survives() throws IOException, ThomasException {
        // A description with awkward characters still comes back whole.
        TaskList original = new TaskList();
        original.add(new TodoTask("read  book (chapter 3) -- twice!"));
        Storage storage = storage();

        storage.save(original);

        assertEquals("[T][ ] read  book (chapter 3) -- twice!",
                storage.load().get(0).toString());
    }

    @Test
    public void saveThenLoad_descriptionContainingSeparator_isSkipped() throws IOException, ThomasException {
        // A description containing the field separator cannot survive, and is lost on the way back in rather than
        // coming back truncated. This is the known cost of the format, recorded here so that changing it is a
        // deliberate act rather than an accident.
        TaskList original = new TaskList();
        original.add(new TodoTask("read book | and return it"));
        Storage storage = storage();

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertFalse(storage.getSkipComplaints().isEmpty());
    }

    /**
     * A description holding a newline would write two lines for one task, so
     * the next load reads one corrupt line and one task nobody added. Unlike
     * the field separator above, nothing in the program handles this case: it
     * cannot be typed, because commands are read a line at a time, so the
     * save format's one-line-per-task rule is asserted rather than recovered
     * from. Remove the assertion in Storage.save and this case would instead
     * show the corruption, which is what makes it worth pinning here.
     */
    @Test
    public void save_descriptionContainingNewline_assertionThrown() throws ThomasException {
        TaskList original = new TaskList();
        original.add(new TodoTask("read book\nand return it"));
        Storage storage = storage();

        AssertionError error = assertThrows(AssertionError.class, () -> storage.save(original));

        assertEquals("A task encodes to more than one line: T | 0 | read book\nand return it",
                error.getMessage());
    }

    /** The save path is chosen by the program, so a blank one is a bug in it. */
    @Test
    public void constructor_blankFilePath_assertionThrown() {
        AssertionError error = assertThrows(AssertionError.class, () -> new Storage("  "));

        assertEquals("Save file path must be given", error.getMessage());
    }
}
