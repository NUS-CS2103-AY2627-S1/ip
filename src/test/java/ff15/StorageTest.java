package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ff15.contact.Contact;
import ff15.contact.ContactList;
import ff15.task.Deadline;
import ff15.task.Event;
import ff15.task.Task;
import ff15.task.TaskList;
import ff15.task.TaskTime;
import ff15.task.Todo;

/**
 * Tests that {@link Storage} writes the task list somewhere it can read it back
 * from, and that a save file which has been damaged is reported rather than
 * silently misread.
 *
 * <p>Every test writes inside a temporary folder supplied by JUnit, so none of
 * them can touch the real {@code data/ff15.txt}.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    /** Returns a Storage keeping tasks at {@code pathParts} and contacts alongside them. */
    private Storage storageAt(String... pathParts) {
        return new Storage(fileAt(pathParts).toString(), fileAt("contacts.txt").toString());
    }

    /** Returns a Storage keeping contacts at {@code contactPathParts}. */
    private Storage contactStorageAt(String... contactPathParts) {
        return new Storage(fileAt("ff15.txt").toString(), fileAt(contactPathParts).toString());
    }

    private Path fileAt(String... pathParts) {
        Path path = tempDir;
        for (String part : pathParts) {
            path = path.resolve(part);
        }
        return path;
    }

    /** Writes {@code lines} straight into a save file, standing in for an earlier run. */
    private Storage givenSaveFile(String... lines) throws IOException {
        Files.write(fileAt("ff15.txt"), List.of(lines));
        return storageAt("ff15.txt");
    }

    /** Writes {@code lines} straight into a contacts file, standing in for an earlier run. */
    private Storage givenContactFile(String... lines) throws IOException {
        Files.write(fileAt("contacts.txt"), List.of(lines));
        return contactStorageAt("contacts.txt");
    }

    // --- writing and reading back --------------------------------------------

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws Exception {
        assertTrue(storageAt("never-written.txt").load().items().isEmpty());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws Exception {
        assertTrue(givenSaveFile().load().items().isEmpty());
    }

    @Test
    public void save_parentFolderMissing_createsIt() throws Exception {
        Storage storage = storageAt("data", "ff15.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertTrue(Files.exists(fileAt("data", "ff15.txt")));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(fileAt("data", "ff15.txt")));
    }

    @Test
    public void saveThenLoad_everyTaskType_roundTripsUnchanged() throws Exception {
        Storage storage = storageAt("ff15.txt");
        TaskList original = new TaskList();
        original.add(new Todo("read book"));
        original.add(new Deadline("return book", TaskTime.parse("2019-12-02")));
        original.add(new Deadline("submit report", TaskTime.parse("2019-12-02 1800")));
        original.add(new Event("holiday", TaskTime.parse("2019-12-20"), TaskTime.parse("2019-12-26")));
        original.add(new Event("meeting",
                TaskTime.parse("2019-12-05 1400"), TaskTime.parse("2019-12-05 1600")));

        storage.save(original);
        TaskList reloaded = new TaskList(storage.load().items());

        assertEquals(original.size(), reloaded.size());
        for (int number = 1; number <= original.size(); number++) {
            assertEquals(original.get(number).toString(), reloaded.get(number).toString());
        }
    }

    @Test
    public void saveThenLoad_dateOnlyTask_doesNotGainAMidnightTime() throws Exception {
        Storage storage = storageAt("ff15.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", TaskTime.parse("2019-12-02")));

        storage.save(tasks);

        assertEquals("[D][ ] return book (by: Dec 02 2019)", storage.load().items().get(0).toString());
    }

    @Test
    public void saveThenLoad_doneTasks_keepTheirStatus() throws Exception {
        Storage storage = storageAt("ff15.txt");
        TaskList tasks = new TaskList();
        Task done = new Todo("read book");
        done.markAsDone();
        tasks.add(done);
        tasks.add(new Todo("not done yet"));

        storage.save(tasks);
        ArrayList<Task> reloaded = storage.load().items();

        assertEquals("[T][X] read book", reloaded.get(0).toString());
        assertEquals("[T][ ] not done yet", reloaded.get(1).toString());
    }

    @Test
    public void save_emptyList_leavesAnEmptyFile() throws Exception {
        Storage storage = storageAt("ff15.txt");
        storage.save(new TaskList());
        assertTrue(Files.exists(fileAt("ff15.txt")));
        assertTrue(Files.readAllLines(fileAt("ff15.txt")).isEmpty());
    }

    @Test
    public void save_calledAgain_replacesTheEarlierContents() throws Exception {
        Storage storage = storageAt("ff15.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        storage.save(tasks);

        tasks.delete(1);
        tasks.add(new Todo("second"));
        storage.save(tasks);

        assertEquals(List.of("T | 0 | second"), Files.readAllLines(fileAt("ff15.txt")));
    }

    @Test
    public void save_descriptionContainingTheSeparator_losesTheTextAfterIt() throws Exception {
        // Documents Storage's own limit: a description holding " | " gains a field
        // when read back, so the text after it is lost. The parser refuses "|" in
        // anything the user types, so this cannot be reached from a command; it is
        // kept so that the limit is stated rather than merely avoided.
        Storage storage = storageAt("ff15.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book | and take notes"));

        storage.save(tasks);

        assertEquals("read book", storage.load().items().get(0).toString().substring("[T][ ] ".length()));
    }

    // --- the environment getting in the way ----------------------------------

    @Test
    public void save_parentIsAFileNotAFolder_throwsAndSaysSo() throws Exception {
        Files.writeString(fileAt("data"), "not a folder");
        Storage storage = storageAt("data", "ff15.txt");

        IOException thrown = assertThrows(IOException.class, () -> storage.save(new TaskList()));

        assertTrue(thrown.getMessage().contains("folder"), thrown.getMessage());
    }

    @Test
    public void save_targetIsAFolder_throwsAndSaysSo() throws Exception {
        Files.createDirectories(fileAt("ff15.txt"));
        Storage storage = storageAt("ff15.txt");

        IOException thrown = assertThrows(IOException.class, () -> storage.save(new TaskList()));

        assertTrue(thrown.getMessage().contains("folder"), thrown.getMessage());
    }

    @Test
    public void load_targetIsAFolder_throwsAndSaysSo() throws Exception {
        Files.createDirectories(fileAt("ff15.txt"));
        Storage storage = storageAt("ff15.txt");

        IOException thrown = assertThrows(IOException.class, storage::load);

        assertTrue(thrown.getMessage().contains("folder"), thrown.getMessage());
    }

    // --- reading a file that is not quite right ------------------------------

    @Test
    public void load_blankLines_areSkipped() throws Exception {
        Storage storage = givenSaveFile("T | 0 | read book", "", "   ", "T | 1 | return book");
        ArrayList<Task> tasks = storage.load().items();
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][X] return book", tasks.get(1).toString());
    }

    @Test
    public void load_unknownTaskType_skipsTheLineAndSaysWhich() throws Exception {
        Storage.Loaded<Task> loaded = givenSaveFile("X | 0 | mystery").load();
        assertTrue(loaded.items().isEmpty());
        assertEquals(1, loaded.skipped().size());
        assertTrue(loaded.skipped().get(0).startsWith("line 1:"), loaded.skipped().get(0));
        assertTrue(loaded.skipped().get(0).contains("X"), loaded.skipped().get(0));
    }

    @Test
    public void load_lineWithTooFewFields_isSkipped() throws Exception {
        assertEquals(1, givenSaveFile("T | 0").load().skipped().size());
        assertEquals(1, givenSaveFile("just some text").load().skipped().size());
    }

    @Test
    public void load_deadlineMissingItsDate_isSkipped() throws Exception {
        assertEquals(1, givenSaveFile("D | 0 | return book").load().skipped().size());
    }

    @Test
    public void load_eventMissingItsEndDate_isSkipped() throws Exception {
        assertEquals(1, givenSaveFile("E | 0 | meeting | 2019-12-05").load().skipped().size());
    }

    @Test
    public void load_unreadableDate_isSkipped() throws Exception {
        assertEquals(1, givenSaveFile("D | 0 | return book | sunday").load().skipped().size());
    }

    @Test
    public void load_doneFlagThatIsNotOne_readsAsNotDone() throws Exception {
        assertEquals("[T][ ] read book", givenSaveFile("T | 0 | read book").load().items().get(0).toString());
        assertEquals("[T][ ] read book", givenSaveFile("T | 2 | read book").load().items().get(0).toString());
    }

    @Test
    public void load_damagedLineAmongGoodOnes_keepsTheGoodOnesAndNumbersTheBad() throws Exception {
        Storage storage = givenSaveFile("T | 0 | read book", "D | 0 | broken", "", "T | 1 | return book");

        Storage.Loaded<Task> loaded = storage.load();

        assertEquals(2, loaded.items().size());
        assertEquals("[T][ ] read book", loaded.items().get(0).toString());
        assertEquals("[T][X] return book", loaded.items().get(1).toString());
        assertEquals(1, loaded.skipped().size());
        assertTrue(loaded.skipped().get(0).startsWith("line 2:"), loaded.skipped().get(0));
    }

    @Test
    public void load_cleanFile_reportsNothingSkipped() throws Exception {
        assertTrue(givenSaveFile("T | 0 | read book").load().skipped().isEmpty());
    }

    // --- contacts -------------------------------------------------------------

    @Test
    public void loadContacts_fileDoesNotExist_returnsEmptyList() throws Exception {
        assertTrue(contactStorageAt("never-written.txt").loadContacts().items().isEmpty());
    }

    @Test
    public void saveContacts_thenLoad_returnsTheSameContacts() throws Exception {
        ContactList original = new ContactList();
        original.add(new Contact("John", "91234567", "john@example.com"),
                new Contact("Mary", "98765432", ""),
                new Contact("Alex", "", ""));
        Storage storage = contactStorageAt("contacts.txt");

        storage.saveContacts(original);
        ContactList reloaded = new ContactList(storage.loadContacts().items());

        assertEquals(3, reloaded.size());
        assertEquals("John (phone: 91234567, email: john@example.com)", reloaded.get(1).toString());
        assertEquals("Mary (phone: 98765432)", reloaded.get(2).toString());
        assertEquals("Alex", reloaded.get(3).toString());
    }

    @Test
    public void loadContacts_nameOnlyLine_keepsTheTrailingEmptyFields() throws Exception {
        ContactList contacts = new ContactList(givenContactFile("Alex |  | ").loadContacts().items());
        assertEquals(1, contacts.size());
        assertEquals("Alex", contacts.get(1).toString());
    }

    @Test
    public void loadContacts_blankLines_areSkipped() throws Exception {
        Storage storage = givenContactFile("John | 91234567 | ", "", "   ", "Mary |  | ");
        assertEquals(2, storage.loadContacts().items().size());
    }

    @Test
    public void loadContacts_lineMissingFields_isSkippedAndTheRestKept() throws Exception {
        Storage storage = givenContactFile("John | 91234567", "Mary | 98765432 | ");

        Storage.Loaded<Contact> loaded = storage.loadContacts();

        assertEquals(1, loaded.items().size());
        assertEquals("Mary", loaded.items().get(0).getName());
        assertEquals(1, loaded.skipped().size());
        assertTrue(loaded.skipped().get(0).startsWith("line 1:"), loaded.skipped().get(0));
    }

    @Test
    public void saveContacts_emptyList_writesAnEmptyFile() throws Exception {
        Storage storage = contactStorageAt("contacts.txt");
        storage.saveContacts(new ContactList());
        assertTrue(Files.exists(fileAt("contacts.txt")));
        assertTrue(Files.readAllLines(fileAt("contacts.txt")).isEmpty());
    }

    @Test
    public void loadContacts_missingContactFile_leavesTaskLoadingUnaffected() throws Exception {
        Storage storage = givenSaveFile("T | 0 | read book");
        assertEquals(1, storage.load().items().size());
        assertTrue(storage.loadContacts().items().isEmpty());
    }
}
