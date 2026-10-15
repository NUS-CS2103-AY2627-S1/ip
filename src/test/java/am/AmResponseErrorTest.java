package am;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import am.storage.Storage;
import am.task.TaskList;

/** Verifies error classification without depending on the wording of task descriptions. */
public class AmResponseErrorTest {
    @TempDir
    private Path directory;

    @Test
    void flagsInvalidCommandsAndMissingTasksThenResetsForSuccess() {
        Am am = new Am(new Storage(directory.resolve("tasks.txt").toString()));
        assertFalse(am.isResponseError());
        String[] commands = {"unknown", "deadline", "mark 1", "unmark 1", "delete 1", "edit 1 /name x"};
        for (String command : commands) {
            am.getResponse(command);
            assertTrue(am.isResponseError(), command);
            am.getResponse("list");
            assertFalse(am.isResponseError(), "Success after: " + command);
        }
    }

    @Test
    void clearsUnknownCommandInputAndRetainsOtherErrorsAcrossResponses() {
        Am am = new Am(new Storage(directory.resolve("tasks.txt").toString()));
        assertFalse(am.shouldPreserveInput());
        for (String command : new String[]{"unknown", "LIST"}) {
            assertEquals("AAAAHHHHHHHHHHHHHHH\nYou can't tell me to '" + command + "'", am.getResponse(command));
            assertTrue(am.isResponseError());
            assertFalse(am.shouldPreserveInput());

            am.getResponse("deadline");
            assertTrue(am.isResponseError());
            assertTrue(am.shouldPreserveInput());

            assertEquals("", am.getResponse(" "));
            assertFalse(am.isResponseError());
            assertFalse(am.shouldPreserveInput());
        }
        am.getResponse("todo You can't tell me to 'unknown'");
        assertFalse(am.isResponseError());
        assertFalse(am.shouldPreserveInput());
        am.getResponse("edit 1 /until 1800");
        assertTrue(am.isResponseError());
        assertTrue(am.shouldPreserveInput());
        am.getResponse("list");
        assertFalse(am.shouldPreserveInput());
    }

    @Test
    void distinguishesInvalidEditsFromUnchangedTasksAndErrorLikeNames() {
        Am am = new Am(new Storage(directory.resolve("tasks.txt").toString()));
        am.getResponse("todo You messed up the command.");
        assertFalse(am.isResponseError());
        am.getResponse("edit 1 /by 2026-09-12");
        assertTrue(am.isResponseError());
        assertTrue(am.shouldPreserveInput());
        assertEquals("No changes.", am.getResponse("edit 1 /name You messed up the command."));
        assertFalse(am.isResponseError());
        am.getResponse("list");
        assertFalse(am.isResponseError());
    }

    @Test
    void flagsCorruptedDataAndRecoversAfterFileIsFixed() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "invalid record");
        Am am = new Am(new Storage(file.toString()));
        assertEquals("What did you do to my memory?", am.getResponse("list"));
        assertTrue(am.isResponseError());
        assertTrue(am.shouldPreserveInput());
        Files.writeString(file, "");
        am.getResponse("list");
        assertFalse(am.isResponseError());
    }

    @Test
    void flagsStorageReadFailures() {
        Storage storage = new Storage(directory.resolve("tasks.txt").toString()) {
            @Override
            public TaskList load() throws IOException {
                throw new IOException("Simulated read failure");
            }
        };
        Am am = new Am(storage);
        assertEquals("I couldn't access my memory.", am.getResponse("list"));
        assertTrue(am.isResponseError());
        assertTrue(am.shouldPreserveInput());
    }

    @Test
    void flagsSaveFailuresIncludingEditsAndResetsForReadOnlyCommands() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | read book\n");
        Storage storage = new Storage(file.toString()) {
            @Override
            public void save(TaskList tasks) throws IOException {
                throw new IOException("Simulated save failure");
            }
        };
        Am am = new Am(storage);
        String[] commands = {"edit 1 /name read notes", "mark 1", "unmark 1", "todo another", "delete 1"};
        for (String command : commands) {
            assertEquals("I couldn't access my memory.", am.getResponse(command));
            assertTrue(am.isResponseError(), command);
            am.getResponse("list");
            assertFalse(am.isResponseError(), "Success after: " + command);
        }
    }
}
