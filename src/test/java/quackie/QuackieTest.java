package quackie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import quackie.storage.Storage;

/**
 * Tests the command-response interface used by the JavaFX GUI.
 */
class QuackieTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that GUI commands share task state and return readable responses. */
    @Test
    void getResponseExecutesCommandsAndPreservesState() {
        Path dataFile = temporaryDirectory.resolve("quackie.txt");
        Quackie quackie = new Quackie(new Storage(dataFile));

        String addResponse = quackie.getResponse("todo read book");
        String markResponse = quackie.getResponse("mark 1");
        String updateResponse = quackie.getResponse("update 1 deadline read novel /by Friday");
        String listResponse = quackie.getResponse("list");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(markResponse.contains("marked this task as done"));
        assertTrue(updateResponse.contains("Updated this task"));
        assertTrue(listResponse.contains("1.[D][X] read novel (by: Friday)"));
    }

    /** Verifies GUI-specific formatting and exit-command recognition. */
    @Test
    void getResponseFormatsErrorsAndExitMessage() {
        Quackie quackie = new Quackie(new Storage(temporaryDirectory.resolve("commands.txt")));

        assertEquals("OOPS!!! Please enter a command.", quackie.getResponse(""));
        assertEquals("OOPS!!! Please enter a command.", quackie.getResponse(null));
        assertEquals("Bye. Hope to see you again soon!", quackie.getResponse("bye"));
        assertTrue(quackie.isExitCommand("bye"));
        assertTrue(quackie.isExitCommand("  bye  "));
        assertFalse(quackie.isExitCommand("list"));
    }

    /** Verifies corrupted startup data does not prevent the chatbot from serving commands. */
    @Test
    void startsWithEmptyListWhenStoredDataIsCorrupted() throws IOException {
        Path dataFile = temporaryDirectory.resolve("corrupted.txt");
        Files.writeString(dataFile, "not-a-task-record");

        Quackie quackie = new Quackie(new Storage(dataFile));

        assertEquals("Here are the tasks in your list:", quackie.getResponse("list"));
    }
}
