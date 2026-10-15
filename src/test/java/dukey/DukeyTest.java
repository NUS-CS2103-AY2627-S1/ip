package dukey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dukey.command.Command;

public class DukeyTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-14T10:15:30Z"), ZoneId.of("Asia/Singapore"));

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_listCommand_returnsCommandOutput() throws IOException {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Files.writeString(dataFile, "T | 1 | read book\n");
        Dukey dukey = new Dukey(dataFile.toString(), false);

        String response = dukey.getResponse("list");

        assertEquals("Here are the tasks in your list:\n1.[T][X] read book", response);
        assertFalse(dukey.isExit());
        assertEquals(Command.STYLE_SEARCH, dukey.getResponseStyleClass());
    }

    @Test
    public void getResponse_exitCommand_returnsByeAndMarksExit() {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Dukey dukey = new Dukey(dataFile.toString(), false);

        String response = dukey.getResponse("bye");

        assertEquals("Bye. The shelves are tidy for now.", response);
        assertTrue(dukey.isExit());
        assertEquals(Command.STYLE_EXIT, dukey.getResponseStyleClass());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorStyle() {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Dukey dukey = new Dukey(dataFile.toString(), false);

        String response = dukey.getResponse("unknown");

        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-( Let's keep the shelves neat.",
                response);
        assertEquals(Command.STYLE_ERROR, dukey.getResponseStyleClass());
    }

    @Test
    public void getResponse_statsCommand_returnsTaskStatistics() {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Dukey dukey = new Dukey(dataFile.toString(), false, FIXED_CLOCK);

        dukey.getResponse("todo read book");
        dukey.getResponse("todo return book");
        dukey.getResponse("mark 1");
        String response = dukey.getResponse("stats");

        assertEquals("Here are your task statistics:\n"
                + "Total tasks: 2\n"
                + "Completed tasks: 1\n"
                + "Pending tasks: 1\n"
                + "Completed in the current calendar week: 1\n"
                + "Completed in the current calendar month: 1", response);
        assertEquals(Command.STYLE_STATS, dukey.getResponseStyleClass());
    }

    @Test
    public void getResponse_statsCommandWithArguments_returnsError() {
        Path dataFile = temporaryDirectory.resolve("dukey.txt");
        Dukey dukey = new Dukey(dataFile.toString(), false, FIXED_CLOCK);

        String response = dukey.getResponse("stats today");

        assertEquals("OOPS!!! The stats command does not take any arguments. Let's keep the shelves neat.", response);
        assertEquals(Command.STYLE_ERROR, dukey.getResponseStyleClass());
    }
}
