package thomas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ThomasTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void run_byeCommand_showsWelcomeAndGoodbye() {
        java.io.InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(captured));
        try {
            new Thomas(temporaryDirectory.toString(), "tasks.txt").run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = captured.toString();
        assertTrue(output.contains("Hello! I'm Thomas."));
        assertTrue(output.contains("Bye. See yaa!"));
    }

    @Test
    void getResponse_placeDeletion_requiresMatchingConfirmation() {
        Thomas thomas = new Thomas(temporaryDirectory.toString(), "tasks.txt");
        thomas.getResponse("place Sushi /type Restaurant /at Town /rating 4 /price 12");

        String unconfirmed = thomas.getResponse("confirmdeleteplace 1");
        assertTrue(unconfirmed.contains("No matching place deletion"));
        assertEquals(1, thomas.getPlaces().size());

        thomas.getResponse("deleteplace 1");
        thomas.getResponse("confirmdeleteplace 1");
        assertEquals(0, thomas.getPlaces().size());
    }

    @Test
    void getResponse_invalidCommandAfterDeletionRequest_cancelsConfirmation() {
        Thomas thomas = new Thomas(temporaryDirectory.toString(), "tasks.txt");
        thomas.getResponse("place Sushi /type Restaurant /at Town /rating 4 /price 12");
        thomas.getResponse("deleteplace 1");

        thomas.getResponse("listplace extra");
        String confirmation = thomas.getResponse("confirmdeleteplace 1");

        assertTrue(confirmation.contains("No matching place deletion"));
        assertEquals(1, thomas.getPlaces().size());
    }

    @Test
    void getResponse_helpCommand_listsTaskAndPlaceCommands() {
        Thomas thomas = new Thomas(temporaryDirectory.toString(), "tasks.txt");

        String response = thomas.getResponse("/help");

        assertTrue(response.contains("Tasks:"));
        assertTrue(response.contains("Places:"));
        assertTrue(response.contains("place NAME /type TYPE /at ADDRESS /rating 1-5 /price AMOUNT"));
        assertTrue(response.contains("[/visited YYYY-MM-DD] [/note NOTE]"));
    }

    @Test
    void getResponse_taskLifecycle_updatesAndPersistsTasks() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Thomas thomas = new Thomas(temporaryDirectory.toString(), "tasks.txt");

        thomas.getResponse("todo read book");
        thomas.getResponse("mark 1");

        Thomas reloaded = new Thomas(temporaryDirectory.toString(), "tasks.txt");
        assertEquals(1, reloaded.getTasks().size());
        assertTrue(reloaded.getTasks().get(0).isDone());
        assertTrue(taskFile.toFile().exists());

        reloaded.getResponse("unmark 1");
        reloaded.getResponse("delete 1");
        assertEquals(0, reloaded.getTasks().size());
    }

    @Test
    void getResponse_errors_returnFriendlyMessagesAndDoNotExit() {
        Thomas thomas = new Thomas(temporaryDirectory.toString(), "tasks.txt");

        String empty = thomas.getResponse("   ");
        String malformedNumber = thomas.getResponse("mark not-a-number");
        String invalidDate = thomas.getResponse("deadline submit /by 2026-02-30");

        assertTrue(empty.contains("Please enter a command"));
        assertTrue(malformedNumber.contains("valid task number"));
        assertTrue(invalidDate.contains("wrong date format"));
        assertFalse(thomas.isLastCommandExit());
    }

    @Test
    void getResponse_bye_setsExitFlagUntilNextCommand() {
        Thomas thomas = new Thomas(temporaryDirectory.toString(), "tasks.txt");

        thomas.getResponse("bye");
        assertTrue(thomas.isLastCommandExit());

        thomas.getResponse("list");
        assertFalse(thomas.isLastCommandExit());
    }
}
