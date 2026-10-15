package clarry;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command sequences, persistence across restarts, and recovery from errors. */
class ClarryWorkflowTest {
    @TempDir
    private Path directory;

    @Test
    void commands_fullLifecycle_persistsStatusDeletionAndRenumbering() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Clarry bot = new Clarry(file);
        for (String input : List.of("todo first", "deadline second /by 2024-02-29",
                "event third /from 2024-02-29 10:00 /to 2024-03-01 11:00",
                "mark 2", "unmark 2", "mark 3", "delete 1")) {
            assertFalse(bot.getCommandResponse(input).isError(), input);
        }
        assertEquals(List.of("D | 0 | second | 2024-02-29",
                "E | 1 | third | 2024-02-29 10:00 | 2024-03-01 11:00"), Files.readAllLines(file));
        String beforeRestart = bot.getResponse("list");
        assertTrue(beforeRestart.contains("1.[D][ ] second"));
        assertTrue(beforeRestart.contains("2.[E][X] third"));
        assertEquals(beforeRestart, new Clarry(file).getResponse("list"));
    }

    @Test
    void commands_readOnlyQueries_doNotWriteSaveFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Clarry bot = new Clarry(file);
        for (String input : List.of("help", "list", "find missing", "on 2024-02-29", "nonsense")) {
            bot.getResponse(input);
            assertFalse(Files.exists(file), input);
        }
        bot.getResponse("todo book");
        byte[] original = Files.readAllBytes(file);
        bot.getResponse("find BOOK");
        bot.getResponse("on 2024-02-29");
        assertEquals(new String(original, java.nio.charset.StandardCharsets.UTF_8), Files.readString(file));
    }

    @Test
    void getCommandResponse_errorThenSuccess_doesNotRetainErrorStatus() {
        Clarry bot = new Clarry(directory.resolve("tasks.txt"));
        assertTrue(bot.getCommandResponse("mark abc").isError());
        Response success = bot.getCommandResponse("todo ERROR OOPS!!!");
        assertFalse(success.isError());
        assertTrue(success.text().contains("ERROR OOPS!!!"));
        assertFalse(bot.getCommandResponse("list").isError());
        assertFalse(bot.getCommandResponse("bye extra").text().contains("See you next tide!"));
        assertFalse(bot.isExitRequested());
    }

    @Test
    void getCommandResponse_deletingLastTask_reportsZeroAndPersistsEmptyList() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Clarry bot = new Clarry(file);
        bot.getResponse("todo book");
        assertTrue(bot.getResponse("delete 1").contains("0 tasks aboard"));
        assertEquals("", Files.readString(file));
        assertTrue(new Clarry(file).getResponse("list").contains("Your radar is clear"));
    }

    @Test
    void getCommandResponse_loadWarning_appearsOnceAndRecoveryRequiresRestart() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken\nT | 0 | book\n");
        Clarry bot = new Clarry(file);
        assertTrue(bot.getCommandResponse("help").isError());
        assertFalse(bot.getCommandResponse("list").isError());
        Files.writeString(file, "T | 0 | book\n");
        assertTrue(bot.getCommandResponse("todo trip").isError());
        Clarry restarted = new Clarry(file);
        assertFalse(restarted.getCommandResponse("todo trip").isError());
        assertTrue(restarted.getResponse("list").contains("2.[T][ ] trip"));
    }

    @Test
    void getCommandResponse_invalidUtf8_blocksWritesAndPreservesBytes() throws IOException {
        Path file = directory.resolve("tasks.txt");
        byte[] invalid = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, invalid);
        Clarry bot = new Clarry(file);
        assertTrue(bot.getCommandResponse("list").text().contains("Could not load saved tasks"));
        assertTrue(bot.getCommandResponse("todo trip").isError());
        assertArrayEquals(invalid, Files.readAllBytes(file));
    }
}
