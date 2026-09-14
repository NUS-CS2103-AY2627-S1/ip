package groot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import groot.testutil.IsolatedProcess;

/**
 * Covers complete command workflows, startup errors, and console termination.
 */
public class CommandCoverageTest {
    @TempDir
    public Path directory;

    @Test
    public void getResponse_allCommands_preservesStateAndResponseContracts() throws Exception {
        assertEquals("passed\n", IsolatedProcess.run(directory, CommandScenario.class, ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"D | 0 | report | bad-date\n", "Q | 0 | task\n"})
    public void main_corruptStorage_reportsStartupErrorAndDoesNotRunCommands(String content) throws Exception {
        Files.createDirectories(directory.resolve("data"));
        Files.writeString(directory.resolve("data/groot.txt"), content);
        String output = IsolatedProcess.run(directory, Groot.class, "todo must not be added\nbye\n");
        assertTrue(output.contains("line 1 in " + Path.of("data", "groot.txt") + " is invalid."), output);
        assertFalse(output.contains("Got it."), output);
        assertFalse(output.contains("Bye. Hope"), output);
        assertEquals(content, Files.readString(directory.resolve("data/groot.txt")));
    }

    @Test
    public void main_endOfInput_exitsWithoutFarewell() throws Exception {
        String output = IsolatedProcess.run(directory, Groot.class, "list\n");
        assertTrue(output.contains("Here are the tasks in your list:"), output);
        assertFalse(output.contains("Bye. Hope"), output);
    }

    @Test
    public void main_bye_ignoresLaterInput() throws Exception {
        String output = IsolatedProcess.run(directory, Groot.class, "bye\ntodo ignored\n");
        assertTrue(output.contains("Bye. Hope"), output);
        assertFalse(Files.exists(directory.resolve("data")));
    }

    /**
     * Asserts the application contract in a process with an empty data directory.
     */
    public static class CommandScenario {
        /**
         * Exercises every command and alternating success/error responses.
         */
        public static void main(String[] args) throws Exception {
            Groot groot = new Groot();
            String empty = " Here are the tasks in your list:";
            assertEquals(empty, groot.getResponse("list"));
            assertEquals(" Here are the matching tasks in your list:", groot.getResponse("find absent"));
            String help = groot.getResponse("help");
            assertTrue(help.contains("todo DESCRIPTION"));
            assertEquals(help, groot.getResponse("-h"));
            assertEquals(help, groot.getResponse("--help"));
            assertEquals(" Got it. I've added this task:\n   [T][ ] first\n Now you have 1 task in the list.",
                    groot.getResponse("todo first"));
            assertFalse(groot.getCommandResponse("deadline second /by 2028-02-29").isError());
            assertFalse(groot.getCommandResponse("event third /from Mon /to Tue").isError());
            String before = groot.getResponse("list");
            byte[] saved = Files.readAllBytes(Path.of("data/groot.txt"));
            String[] invalidCommands = {"", "unknown", "todo", "deadline x", "deadline /by 2026-01-01",
                "deadline x /by", "deadline x /by invalid", "event x", "event /from Mon /to Tue",
                "event x /from /to Tue", "event x /from Mon /to", "find", "mark", "unmark", "delete",
                "mark 0", "unmark -1", "delete 4", "mark 2147483648", "unmark 1.5", "delete abc", "sort -x"};
            for (String command : invalidCommands) {
                CommandResponse response = groot.getCommandResponse(command);
                assertTrue(response.isError(), command);
                assertTrue(response.text().startsWith(" Oops!"), command);
                assertEquals(before, groot.getResponse("list"), command);
                assertFalse(groot.getCommandResponse("help").isError(), command);
            }
            assertArrayEquals(saved, Files.readAllBytes(Path.of("data/groot.txt")));
            assertEquals(before, new Groot().getResponse("list"));
            assertEquals(" Here are the matching tasks in your list:\n 1.[T][ ] first",
                    groot.getResponse("find FIRST"));
            assertFalse(groot.getCommandResponse("mark 1").isError());
            assertFalse(groot.getCommandResponse("unmark 1").isError());
            assertFalse(groot.getCommandResponse("delete 3").isError());
            assertTrue(groot.getResponse("delete 2").endsWith("Now you have 1 task in the list."));
            assertTrue(groot.getResponse("delete 1").endsWith("Now you have 0 tasks in the list."));
            assertEquals(empty, new Groot().getResponse("list"));
            assertFalse(groot.getCommandResponse("bye").isError());
            System.out.println("passed");
        }
    }
}
