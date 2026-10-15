package billy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import billy.BillyException;
import billy.task.Deadline;
import billy.task.Event;
import billy.task.Task;
import billy.task.TaskDate;
import billy.task.Todo;

/**
 * Tests {@link Storage}, which keeps the task list on disk between runs.
 *
 * <p>This is where a bug costs the most. Everything else can be put right by
 * typing the command again, but a list written wrongly is gone, and the user
 * finds out only when Billy next starts. The central case is therefore the round
 * trip: whatever is saved has to load back as the same tasks.
 *
 * <p>The other half is damage. A save file can be edited by hand, so a line that
 * cannot be understood must cost only that line rather than the whole list, and
 * must be counted so the user can be told.
 *
 * <p>Every test writes inside a {@link TempDir} that JUnit creates fresh and
 * deletes afterwards, so no test touches a real save file or sees another test's.
 */
public class StorageTest {

    /** The names the save format uses, kept here so a test reads as a file would. */
    private static final String READ_BOOK = "T | 1 | read book";
    private static final String RETURN_BOOK = "D | 0 | return book | 2019-12-02 1800";
    private static final String CONFERENCE = "E | 0 | conference | 2019-12-02 | 2019-12-04";

    /** Writes the given lines into a save file and returns storage over it. */
    private static Storage storageHolding(Path folder, String... lines) throws IOException {
        Path file = folder.resolve("billy.txt");
        Files.write(file, List.of(lines));
        return new Storage(file);
    }

    // ---------------------------------------------------------------
    // Saving and loading back again
    // ---------------------------------------------------------------

    @Test
    public void saveThenLoad_allThreeKindsOfTask_comeBackUnchanged(@TempDir Path folder)
            throws BillyException, IOException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        List<Task> original = List.of(
                todo,
                new Deadline("return book", TaskDate.parse("2019-12-02 1800")),
                new Event("conference", TaskDate.parse("2019-12-02"),
                        TaskDate.parse("2019-12-04")));

        Storage storage = new Storage(folder.resolve("billy.txt"));
        storage.save(original);
        ArrayList<Task> reloaded = storage.load();

        // The whole promise of saving, in one assertion per task: what comes back
        // must describe itself exactly as what went in did, done flags included.
        assertEquals(3, reloaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), reloaded.get(i).toString());
        }
    }

    @Test
    public void saveThenLoad_descriptionContainingABar_survives(@TempDir Path folder)
            throws BillyException, IOException {
        // The bar also separates fields, so a description holding one is the case
        // most likely to be split in the wrong place when read back.
        Storage storage = new Storage(folder.resolve("billy.txt"));
        storage.save(List.of(new Todo("read chapter 3 | 4")));

        assertEquals("[T][ ] read chapter 3 | 4", storage.load().get(0).toString());
    }

    @Test
    public void save_calledTwice_fileReplacedNotAppended(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = new Storage(folder.resolve("billy.txt"));
        storage.save(List.of(new Todo("first"), new Todo("second")));
        storage.save(List.of(new Todo("only one left")));

        // Writing the whole list each time means a deletion has to shrink the file.
        assertEquals(1, storage.load().size());
    }

    @Test
    public void save_emptyList_loadsBackEmpty(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = new Storage(folder.resolve("billy.txt"));
        storage.save(List.of(new Todo("read book")));
        storage.save(List.of());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_folderDoesNotExistYet_createdOnTheWay(@TempDir Path folder)
            throws BillyException, IOException {
        // On a first run neither the folder nor the file exists, and saving has
        // to work anyway rather than asking the user to make one.
        Storage storage = new Storage(folder.resolve("data").resolve("billy.txt"));
        storage.save(List.of(new Todo("read book")));

        assertEquals(1, storage.load().size());
    }

    // ---------------------------------------------------------------
    // Reading a file that is missing, empty, or damaged
    // ---------------------------------------------------------------

    @Test
    public void load_noFileAtAll_emptyListAndNoComplaint(@TempDir Path folder)
            throws BillyException {
        // A missing file is not damage: it is what a first run looks like.
        Storage storage = new Storage(folder.resolve("billy.txt"));
        assertTrue(storage.load().isEmpty());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_blankLines_ignoredWithoutBeingCounted(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = storageHolding(folder, READ_BOOK, "", "   ", RETURN_BOOK);

        assertEquals(2, storage.load().size());
        // A blank line is not damage, so it must not be reported as such.
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_unknownTypeLetter_lineSkippedAndCounted(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = storageHolding(folder, READ_BOOK, "X | 0 | mystery", CONFERENCE);

        // One damaged line costs that line only; the rest of the list survives.
        assertEquals(2, storage.load().size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_tooFewFields_lineSkipped(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = storageHolding(folder, "D | 0 | return book", READ_BOOK);

        // A deadline with no date is not a deadline that can be rebuilt.
        assertEquals(1, storage.load().size());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_doneFlagIsNeitherZeroNorOne_lineSkipped(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = storageHolding(folder, "T | 2 | read book");

        // Guessing at a flag Billy never writes would invent a status.
        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_emptyField_lineSkipped(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = storageHolding(folder, "T | 0 | ");

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_unreadableDate_lineSkipped(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = storageHolding(folder, "D | 0 | return book | someday");

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_eventEndingBeforeItStarts_lineSkipped(@TempDir Path folder)
            throws BillyException, IOException {
        // The rule the typed command enforces has to hold for files too, or a
        // hand-edited file would be a back door around it.
        Storage storage = storageHolding(folder, "E | 0 | backwards | 2019-12-04 | 2019-12-02");

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getSkippedLineCount());
    }

    @Test
    public void load_severalDamagedLines_allCounted(@TempDir Path folder)
            throws BillyException, IOException {
        Storage storage = storageHolding(folder, "X | 0 | one", READ_BOOK, "T | 9 | two", "junk");

        assertEquals(1, storage.load().size());
        assertEquals(3, storage.getSkippedLineCount());
    }

    @Test
    public void getSkippedLineCount_secondLoad_countStartsAgain(@TempDir Path folder)
            throws BillyException, IOException {
        Path file = folder.resolve("billy.txt");
        Storage storage = new Storage(file);

        Files.write(file, List.of("X | 0 | mystery"));
        storage.load();
        assertEquals(1, storage.getSkippedLineCount());

        // The count describes the last load, not every load ever done, or the
        // user would be warned again about damage they have already fixed.
        Files.write(file, List.of(READ_BOOK));
        storage.load();
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void getPath_anyStorage_returnsTheFileItWasGiven(@TempDir Path folder) {
        // Billy names this file in its messages, so it has to be the real one.
        Path file = folder.resolve("billy.txt");
        assertEquals(file, new Storage(file).getPath());
    }

    // ---------------------------------------------------------------
    // Explaining why a file could not be used
    // ---------------------------------------------------------------

    @Test
    public void describeFailure_reasonGiven_readsAsPartOfASentence() {
        // Java capitalizes its reasons; mid-sentence they should not be.
        FileSystemException failure =
                new FileSystemException("billy.txt", null, "Is a directory");
        assertEquals("is a directory", Storage.describeFailure(failure));
    }

    @Test
    public void describeFailure_permissionDenied_saidInPlainWords() {
        assertEquals("permission was denied",
                Storage.describeFailure(new AccessDeniedException("billy.txt")));
    }

    @Test
    public void describeFailure_fileNotThere_saidInPlainWords() {
        assertEquals("it isn't there",
                Storage.describeFailure(new NoSuchFileException("billy.txt")));
    }

    @Test
    public void describeFailure_unrecognizedFailure_fallsBackToSomethingUseful() {
        // Whatever goes wrong, the user gets a phrase rather than a class name.
        assertEquals("the file couldn't be opened",
                Storage.describeFailure(new IOException("something odd")));
    }

    @Test
    public void describeFailure_anyFailure_doesNotRepeatTheFileName() {
        // Billy has already named the file, so the phrase in brackets must not
        // simply echo the path back at the user.
        assertTrue(!Storage.describeFailure(new NoSuchFileException("billy.txt"))
                .contains("billy.txt"));
    }

    // ---------------------------------------------------------------
    // Explaining why a file could not be used
    // ---------------------------------------------------------------

    @Test
    public void describeFailure_fileMissing_saysSo() {
        // Phrased to read mid-sentence, since it is dropped into a message in
        // brackets after the file name.
        assertEquals("it isn't there", Storage.describeFailure(new NoSuchFileException("x")));
    }

    @Test
    public void describeFailure_somethingInTheWay_saysSo() {
        assertEquals("something that isn't a folder is in the way",
                Storage.describeFailure(new FileAlreadyExistsException("x")));
    }

    @Test
    public void describeFailure_permissionDenied_saysSo() {
        assertEquals("permission was denied", Storage.describeFailure(new AccessDeniedException("x")));
    }

    @Test
    public void describeFailure_aReasonGiven_lowercasedToReadMidSentence() {
        // "Is a directory" is how the operating system phrases it; it has to be
        // bent to lower case to sit inside Billy's sentence.
        assertEquals("is a directory",
                Storage.describeFailure(new FileSystemException("x", null, "Is a directory")));
    }

    @Test
    public void describeFailure_anythingElse_generalWording() {
        assertEquals("the file couldn't be opened", Storage.describeFailure(new IOException("x")));
    }
}
