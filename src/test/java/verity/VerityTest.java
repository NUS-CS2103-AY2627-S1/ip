package verity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Verity's end-to-end handling of commands and recoverable errors. */
class VerityTest {

    @TempDir
    Path tempDir;

    @Test
    void getResponse_addThenUndo_taskIsRemoved() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());

        verity.getResponse("todo read book");
        String response = verity.getResponse("undo");

        assertTrue(response.contains("Whatever. Un-did it"));
        assertTrue(verity.getResponse("list").contains("zero tasks"));
    }

    @Test
    void getResponse_repeatedUndo_reversesSeveralActionsInLastInFirstOutOrder() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());
        verity.getResponse("todo read book");
        verity.getResponse("todo write essay");
        verity.getResponse("mark 1");

        String undoMark = verity.getResponse("undo");
        String undoSecondTodo = verity.getResponse("undo");
        String undoFirstTodo = verity.getResponse("undo");
        String noMoreActions = verity.getResponse("undo");

        assertTrue(undoMark.contains("Whatever. Un-did it"));
        assertTrue(undoSecondTodo.contains("write essay"));
        assertTrue(undoFirstTodo.contains("read book"));
        assertTrue(verity.getResponse("list").contains("zero tasks"));
        assertTrue(noMoreActions.contains("nothing to undo"));
    }

    @Test
    void getResponse_nonMutatingCommand_doesNotClearUndoHistory() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());
        verity.getResponse("todo read book");
        verity.getResponse("list");

        String response = verity.getResponse("undo");

        assertTrue(response.contains("Whatever. Un-did it"));
        assertTrue(verity.getResponse("list").contains("zero tasks"));
    }

    @Test
    void getResponse_invalidInput_errorResponseReturnedWithoutEndingSession() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());

        String response = verity.getResponse("xyzzy plugh");

        assertTrue(Verity.isErrorResponse(response));
        assertFalse(verity.isExit());
    }

    @Test
    void getResponse_byeCommand_setsIsExitTrue() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());

        verity.getResponse("bye");

        assertTrue(verity.isExit());
    }

    @Test
    void getResponse_firstMistake_noEscalationReaction() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());

        String response = verity.getResponse("xyzzy plugh");

        assertFalse(response.contains("Again?"));
        assertFalse(response.contains("I am NOT explaining"));
    }

    @Test
    void getResponse_secondConsecutiveMistake_appendsMildAnnoyance() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());
        verity.getResponse("xyzzy plugh");

        String response = verity.getResponse("xyzzy plugh");

        assertTrue(response.contains("Again? Read what I just told you."));
    }

    @Test
    void getResponse_thirdConsecutiveMistake_appendsMaximumAnnoyance() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());
        verity.getResponse("xyzzy plugh");
        verity.getResponse("xyzzy plugh");

        String response = verity.getResponse("xyzzy plugh");

        assertTrue(response.contains("I am NOT explaining this again."));
    }

    @Test
    void getResponse_successAfterMistakes_annoyanceResetsToFirstTier() {
        Verity verity = new Verity(tempDir.resolve("verity.txt").toString());
        verity.getResponse("xyzzy plugh");
        verity.getResponse("xyzzy plugh");
        verity.getResponse("todo read book");

        String response = verity.getResponse("xyzzy plugh");

        assertFalse(response.contains("Again?"));
        assertFalse(response.contains("I am NOT explaining"));
    }

    @Test
    void constructor_loadFails_fallsBackToEmptyTaskList() {
        // Files.exists() on a directory is true, but Files.readAllLines() on one throws
        // IOException - this exercises Verity's catch-and-recover path without needing to
        // simulate a permissions failure, which isn't reliably portable across OSes.
        Verity verity = new Verity(tempDir.toString());

        assertTrue(verity.getResponse("list").contains("zero tasks"));
    }

    @Test
    void run_readsCommandsFromStdinUntilBye_printsWelcomeAndProcessesEachOne() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            String simulatedInput = "todo read book\nbye\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
            // Ui's Scanner is constructed from System.in inside Verity's own constructor, so
            // System.in must already be redirected before Verity is created.
            Verity verity = new Verity(tempDir.resolve("verity.txt").toString());

            ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
            verity.run();

            String output = capturedOut.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Hi I'm Verity"));
            assertTrue(output.contains("Obviously. Added"));
            assertTrue(output.contains("Finally."));
            assertTrue(verity.isExit());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
