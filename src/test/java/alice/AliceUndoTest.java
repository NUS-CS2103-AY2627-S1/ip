package alice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AliceUndoTest {

    private ByteArrayOutputStream output;
    private Alice alice;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);
        alice = new Alice(tempDir.resolve("alice.txt").toString(), new Ui(printStream));
        output.reset();
    }

    @Test
    void undoRevertsTaskAddition() {
        alice.processCommand("todo read book");
        alice.processCommand("undo");
        output.reset();
        alice.processCommand("list");

        assertTaskListIsEmpty();
    }

    @Test
    void undoRestoresDeletedTask() {
        alice.processCommand("todo read book");
        alice.processCommand("delete 1");
        alice.processCommand("undo");
        output.reset();
        alice.processCommand("list");

        assertTrue(outputAsString().contains("[T][ ] read book"));
    }

    @Test
    void undoRestoresPreviousTaskStatus() {
        alice.processCommand("todo read book");
        alice.processCommand("mark 1");
        alice.processCommand("undo");
        output.reset();
        alice.processCommand("list");

        assertTrue(outputAsString().contains("[T][ ] read book"));
    }

    @Test
    void undoCanRevertMultipleCommands() {
        alice.processCommand("todo first task");
        alice.processCommand("todo second task");
        alice.processCommand("undo");
        output.reset();
        alice.processCommand("list");

        assertTrue(outputAsString().contains("first task"));
        assertFalse(outputAsString().contains("second task"));

        alice.processCommand("undo");
        output.reset();
        alice.processCommand("list");
        assertTaskListIsEmpty();
    }

    @Test
    void failedCommandDoesNotConsumeUndoHistory() {
        alice.processCommand("todo read book");
        alice.processCommand("todo");
        alice.processCommand("undo");
        output.reset();
        alice.processCommand("list");

        assertTaskListIsEmpty();
    }

    @Test
    void undoWithoutHistoryShowsError() {
        alice.processCommand("undo");

        assertTrue(outputAsString().contains("There is no earlier change to undo."));
    }

    private void assertTaskListIsEmpty() {
        assertTrue(outputAsString().contains("Your list is clear."));
    }

    private String outputAsString() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
