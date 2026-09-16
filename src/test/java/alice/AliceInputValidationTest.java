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

class AliceInputValidationTest {
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
    void eventWithSameStartAndEndDate_isAccepted() {
        alice.processCommand("event workshop /from 2026-10-10 /to 2026-10-10");

        assertTrue(outputAsString().contains("[E][ ] workshop"));
    }

    @Test
    void eventEndingBeforeStart_isRejected() {
        alice.processCommand("event workshop /from 2026-10-10 /to 2026-10-09");

        assertTrue(outputAsString().contains("event cannot end before it starts"));
    }

    @Test
    void descriptionOverLimit_isRejected() {
        alice.processCommand("todo " + "a".repeat(101));

        assertTrue(outputAsString().contains("at most 100 characters"));
    }

    @Test
    void descriptionContainingStorageSeparator_isRejected() {
        alice.processCommand("todo buy milk | eggs");

        assertTrue(outputAsString().contains("cannot contain the | character"));
    }

    @Test
    void findWithMultipleWords_searchesFullPhrase() {
        alice.processCommand("todo read book");
        alice.processCommand("todo read notes");
        output.reset();

        alice.processCommand("find   read book");

        assertTrue(outputAsString().contains("read book"));
        assertFalse(outputAsString().contains("read notes"));
    }

    @Test
    void indexedCommandWithExtraSpaces_isAccepted() {
        alice.processCommand("todo read book");
        output.reset();

        alice.processCommand("mark     1");

        assertTrue(outputAsString().contains("completed this task"));
    }

    @Test
    void indexedCommandWithExtraArgument_isRejected() {
        alice.processCommand("todo read book");
        output.reset();

        alice.processCommand("mark 1 extra");

        assertTrue(outputAsString().contains("choose a valid task number"));
    }

    @Test
    void viewWithExtraArgument_isRejected() {
        alice.processCommand("view 2026-10-10 extra");

        assertTrue(outputAsString().contains("which date to check"));
    }

    private String outputAsString() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
