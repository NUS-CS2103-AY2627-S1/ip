package chai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chai.storage.Storage;

/** Tests Chai's command workflow independently of the console and GUI. */
public class ChaiTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_taskLifecycle_updatesAndDisplaysTasks() {
        Chai chai = createChai("lifecycle");

        assertTrue(chai.getResponse("todo borrow book").contains("[T][ ] borrow book"));
        assertTrue(chai.getResponse("deadline submit report /by 2026-10-01").
                contains("[D][ ] submit report"));
        assertTrue(chai.getResponse("event conference /from 2026-10-02 /to 2026-10-03").
                contains("[E][ ] conference"));
        assertTrue(chai.getResponse("mark 2").contains("[D][X] submit report"));
        assertTrue(chai.getResponse("unmark 2").contains("[D][ ] submit report"));
        assertTrue(chai.getResponse("find REPORT").contains("submit report"));

        String deleteResponse = chai.getResponse("delete 1");
        String listResponse = chai.getResponse("list");

        assertTrue(deleteResponse.contains("Now you have 2 tasks"));
        assertFalse(listResponse.contains("borrow book"));
        assertTrue(listResponse.contains("submit report"));
        assertTrue(listResponse.contains("conference"));
    }

    @Test
    public void getResponse_sort_reordersAndPersistsTasks() {
        Storage storage = createStorage("sorting");
        Chai chai = new Chai(storage);
        chai.getResponse("todo borrow book");
        chai.getResponse("deadline submit report /by 2026-10-03");
        chai.getResponse("event conference /from 2026-10-01 /to 2026-10-02");

        String sortResponse = chai.getResponse("sort");
        String reloadedList = new Chai(storage).getResponse("list");

        assertTaskOrder(sortResponse, "conference", "submit report", "borrow book");
        assertTaskOrder(reloadedList, "conference", "submit report", "borrow book");
    }

    @Test
    public void getResponse_invalidCommands_returnsSpecificErrors() {
        Chai chai = createChai("errors");

        assertEquals("Oops — Use: list", chai.getResponse("list now"));
        assertEquals("Oops — Use: bye", chai.getResponse("bye now"));
        assertTrue(chai.getResponse("mark 1").contains("does not exist"));
        assertTrue(chai.getResponse("deadline report /by tomorrow").contains("yyyy-MM-dd"));
        assertTrue(chai.getResponse("nonsense").contains("isn't on my menu"));
    }

    @Test
    public void constructor_corruptedData_reportsErrorAndStartsEmpty() throws IOException {
        Path dataFile = temporaryDirectory.resolve("corrupt").resolve("chai.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "T | invalid | read book");

        Chai chai = new Chai(new Storage(dataFile));

        assertTrue(chai.getWelcomeMessage().contains("Oops — Saved data is invalid on line 1"));
        assertTrue(chai.getResponse("list").contains("(none)"));
    }

    @Test
    public void getResponse_saveFails_rollsBackAddedTask() throws IOException {
        Path parentFile = temporaryDirectory.resolve("blocked");
        Files.writeString(parentFile, "not a directory");
        Chai chai = new Chai(new Storage(parentFile.resolve("chai.txt")));

        String addResponse = chai.getResponse("todo borrow book");

        assertTrue(addResponse.startsWith("Oops — I could not save your tasks"));
        assertTrue(chai.getResponse("list").contains("(none)"));
    }

    /** Creates a Chai instance with an isolated data file. */
    private Chai createChai(String directoryName) {
        return new Chai(createStorage(directoryName));
    }

    /** Creates storage under the temporary test directory. */
    private Storage createStorage(String directoryName) {
        Path dataFile = temporaryDirectory.resolve(directoryName).resolve("chai.txt");
        return new Storage(dataFile);
    }

    /** Checks that the named descriptions occur in ascending output order. */
    private void assertTaskOrder(String output, String... descriptions) {
        int previousIndex = -1;
        for (String description : descriptions) {
            int currentIndex = output.indexOf(description);
            assertTrue(currentIndex > previousIndex);
            previousIndex = currentIndex;
        }
    }
}
