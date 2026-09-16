package discitrack;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DisciTrackTest {
    @TempDir
    public Path tempDir;

    @Test
    public void getResponse_validTodo_returnsSuccessMessage() {
        String filePath = tempDir.resolve("discitrack.txt").toString();
        DisciTrack disciTrack = new DisciTrack(filePath);

        String response = disciTrack.getResponse("todo revise JavaFX");

        assertTrue(response.contains("Your next challenge:"));
        assertTrue(response.contains("revise JavaFX"));
        assertTrue(response.contains("doing it!"));

        String completionResponse = disciTrack.getResponse("mark 1");
        assertTrue(completionResponse.contains("Task completed! You finished"));
        assertTrue(completionResponse.contains("keep that momentum going!"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        String filePath = tempDir.resolve("discitrack.txt").toString();
        DisciTrack disciTrack = new DisciTrack(filePath);

        String response = disciTrack.getResponse("unknown command");

        assertTrue(response.contains("UHOH"));
    }

    @Test
    public void shouldExit_afterByeCommand_returnsTrue() {
        String filePath = tempDir.resolve("discitrack.txt").toString();
        DisciTrack disciTrack = new DisciTrack(filePath);

        assertFalse(disciTrack.shouldExit());
        disciTrack.getResponse("bye");

        assertTrue(disciTrack.shouldExit());
    }

    @Test
    public void getGreeting_returnsShortCoachWelcome() {
        String filePath = tempDir.resolve("discitrack.txt").toString();
        DisciTrack disciTrack = new DisciTrack(filePath);

        String greeting = disciTrack.getGreeting();

        assertTrue(greeting.contains("your discipline coach"));
        assertTrue(greeting.contains("Are you ready to crush today's tasks?"));
    }

    @Test
    public void getResponse_help_returnsCommandsAndExamples() {
        String filePath = tempDir.resolve("discitrack.txt").toString();
        DisciTrack disciTrack = new DisciTrack(filePath);

        String response = disciTrack.getResponse("help");

        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by yyyy-MM-dd"));
        assertTrue(response.contains("Example: mark 1"));
    }

    @Test
    public void getResponse_allCommands_processSuccessfully() {
        String filePath = tempDir.resolve("discitrack.txt").toString();
        DisciTrack disciTrack = new DisciTrack(filePath);

        disciTrack.getResponse("todo exercise");
        disciTrack.getResponse("deadline pay bills /by 2026-09-10");
        disciTrack.getResponse("event holiday /from 2026-09-15 /to 2026-09-18");

        String listResponse = disciTrack.getResponse("list");
        assertTrue(listResponse.contains("exercise"));
        assertTrue(listResponse.contains("pay bills"));
        assertTrue(listResponse.contains("holiday"));

        String findResponse = disciTrack.getResponse("find bills");
        assertTrue(findResponse.contains("pay bills"));

        String dateResponse = disciTrack.getResponse("checkdate 2026-09-10");
        assertTrue(dateResponse.contains("pay bills"));

        String markResponse = disciTrack.getResponse("mark 1");
        assertTrue(markResponse.contains("[X] exercise"));

        String unmarkResponse = disciTrack.getResponse("unmark 1");
        assertTrue(unmarkResponse.contains("[ ] exercise"));

        String deleteResponse = disciTrack.getResponse("delete 1");
        assertTrue(deleteResponse.contains("Off the board!"));

        String byeResponse = disciTrack.getResponse("bye");
        assertTrue(byeResponse.contains("Coach out!"));
    }

    @Test
    public void getResponse_savedTask_persistsAcrossInstances() {
        String filePath = tempDir.resolve("discitrack.txt").toString();
        DisciTrack firstInstance = new DisciTrack(filePath);
        firstInstance.getResponse("todo build discipline");

        DisciTrack secondInstance = new DisciTrack(filePath);
        String response = secondInstance.getResponse("list");

        assertTrue(response.contains("build discipline"));
    }
}
