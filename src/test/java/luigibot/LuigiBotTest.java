package luigibot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LuigiBotTest {
    private static final String LINE = "____________________________________________________________";

    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_commandsInSequence_taskStatePreserved() {
        Path savePath = this.temporaryDirectory.resolve("tasks.txt");
        LuigiBot luigiBot = new LuigiBot(savePath.toString());

        String addResponse = luigiBot.getResponse("todo read book");
        String listResponse = luigiBot.getResponse("list");

        assertEquals(response(
                LINE,
                "Okie-dokie! Luigi added this task:",
                "  [T][ ] read book",
                "You've-a got 1 tasks now!",
                LINE), addResponse);
        assertEquals(response(
                LINE,
                "Let's-a see what Luigi has on the list:",
                "1.[T][ ] read book",
                LINE), listResponse);
    }

    @Test
    void getResponse_invalidCommand_errorResponseReturned() {
        Path savePath = this.temporaryDirectory.resolve("tasks.txt");
        LuigiBot luigiBot = new LuigiBot(savePath.toString());

        String response = luigiBot.getResponse("jump");

        assertEquals(response(
                LINE,
                "Oh no! Luigi doesn't-a recognize that command.",
                LINE), response);
    }

    @Test
    void getResponse_existingSaveFile_savedTasksLoaded() throws IOException {
        Path savePath = this.temporaryDirectory.resolve("tasks.txt");
        Files.writeString(savePath, "T | 1 | saved task");
        LuigiBot luigiBot = new LuigiBot(savePath.toString());

        String response = luigiBot.getResponse("list");

        assertEquals(response(
                LINE,
                "Let's-a see what Luigi has on the list:",
                "1.[T][X] saved task",
                LINE), response);
    }

    @Test
    void getResponse_byeCommand_exitRequested() {
        Path savePath = this.temporaryDirectory.resolve("tasks.txt");
        LuigiBot luigiBot = new LuigiBot(savePath.toString());

        luigiBot.getResponse("bye");

        assertTrue(luigiBot.isExitRequested());
    }

    @Test
    void getResponse_nonExitCommand_exitNotRequested() {
        Path savePath = this.temporaryDirectory.resolve("tasks.txt");
        LuigiBot luigiBot = new LuigiBot(savePath.toString());

        luigiBot.getResponse("list");

        assertFalse(luigiBot.isExitRequested());
    }

    @Test
    void getResponse_clashingEvent_errorAndExistingTaskPreserved() {
        Path savePath = this.temporaryDirectory.resolve("tasks.txt");
        LuigiBot luigiBot = new LuigiBot(savePath.toString());
        luigiBot.getResponse("event team meeting /from 2026-08-27 1400 /to 2026-08-27 1600");

        String response = luigiBot.getResponse(
                "event project meeting /from 2026-08-27 1500 /to 2026-08-27 1700");

        assertEquals(response(
                LINE,
                "Mamma mia! Luigi can't-a add that Event because it clashes with:",
                "1.[E][ ] team meeting (from: Aug 27 2026, 2:00 PM to: Aug 27 2026, 4:00 PM)",
                LINE), response);
        assertEquals(response(
                LINE,
                "Let's-a see what Luigi has on the list:",
                "1.[E][ ] team meeting (from: Aug 27 2026, 2:00 PM to: Aug 27 2026, 4:00 PM)",
                LINE), luigiBot.getResponse("list"));
    }

    @Test
    void getResponse_adjacentEvent_onlyConfirmationReturned() {
        Path savePath = this.temporaryDirectory.resolve("tasks.txt");
        LuigiBot luigiBot = new LuigiBot(savePath.toString());
        luigiBot.getResponse("event team meeting /from 2026-08-27 1400 /to 2026-08-27 1600");

        String response = luigiBot.getResponse(
                "event project meeting /from 2026-08-27 1600 /to 2026-08-27 1700");

        assertEquals(response(
                LINE,
                "Okie-dokie! Luigi added this task:",
                "  [E][ ] project meeting (from: Aug 27 2026, 4:00 PM to: Aug 27 2026, 5:00 PM)",
                "You've-a got 2 tasks now!",
                LINE), response);
    }

    private static String response(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }
}
