package bo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests Bo's interface-independent command execution API. */
class BoTest {
    /** The storage file used by the application when tests run from the project root. */
    private final Path taskFile = Path.of("data", "duke.txt").toAbsolutePath().normalize();

    /** The original storage file contents, restored after each test. */
    private byte[] originalContents;

    /** Whether the storage file existed before the test started. */
    private boolean fileExisted;

    /** Preserves the user's task data and starts each test without a storage file. */
    @BeforeEach
    void preserveStorageFile() throws IOException {
        fileExisted = Files.exists(taskFile);
        originalContents = fileExisted ? Files.readAllBytes(taskFile) : null;
        Files.deleteIfExists(taskFile);
    }

    /** Restores the user's task data after the test has completed. */
    @AfterEach
    void restoreStorageFile() throws IOException {
        if (fileExisted) {
            Files.write(taskFile, originalContents);
        } else {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies the shared API across creation, sorting, listing, and deletion. */
    @Test
    void executeCommand_taskLifecycleAndSort_updatesTaskResponses() throws BoException {
        Bo bo = new Bo();

        String addTodoResponse = bo.executeCommand("todo read book");
        bo.executeCommand("deadline submit form /by 2019-10-15");
        String sortResponse = bo.executeCommand("sort");
        String listResponse = bo.executeCommand("list");
        String deleteResponse = bo.executeCommand("delete 1");

        assertAll(
                () -> assertTrue(addTodoResponse.contains("I've added this task")),
                () -> assertTrue(sortResponse.contains("sorted by date")),
                () -> assertTrue(listResponse.contains("[D][ ] submit form")),
                () -> assertTrue(deleteResponse.contains("I've removed this task")),
                () -> assertTrue(bo.executeCommand("list").contains("read book")));
    }

    /** Verifies that invalid input remains a user-facing command error. */
    @Test
    void executeCommand_invalidInput_throwsBoExceptionInsteadOfCrashing() {
        Bo bo = new Bo();

        BoException exception = assertThrows(BoException.class, () -> bo.executeCommand("not a command"));

        assertTrue(exception.getMessage().contains("don't know what that means"));
    }
}
