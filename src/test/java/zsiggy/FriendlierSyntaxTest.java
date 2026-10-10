package zsiggy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks the todo alias through the CLI using isolated saved data.
 */
public class FriendlierSyntaxTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void run_aliasAndOriginalCommand_saveAndReloadTasks() throws Exception {
        String response = runCli("t read book\ntodo buy milk\nt\nt   \ntask invalid\nlist\nbye\n");
        assertTrue(response.contains("1. [T][ ] read book"));
        assertTrue(response.contains("2. [T][ ] buy milk"));
        assertTrue(response.contains(
                "A todo needs an actual description. I can't organise invisible tasks."
        ));
        assertTrue(response.contains("That's not a command I understand."));
        assertFalse(response.contains("3. [T]"));
        assertEquals("T | 0 | read book" + System.lineSeparator()
                + "T | 0 | buy milk" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("data/tasks.txt")));
        String reloaded = runCli("list\nbye\n");
        assertTrue(reloaded.contains("1. [T][ ] read book"));
        assertTrue(reloaded.contains("2. [T][ ] buy milk"));
    }

    @Test
    public void getResponse_emptyAliases_returnsSameValidation() {
        Duke duke = new Duke();
        for (String input : new String[]{"t", "t   ", "todo", "todo   "}) {
            assertEquals(
                    "Oi. A todo needs an actual description. "
                            + "I can't organise invisible tasks.",
                    duke.getResponse(input)
            );
        }
    }

    /**
     * Runs an isolated CLI session without touching the user's saved tasks.
     */
    private String runCli(String commands) throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classes = Path.of(Duke.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        Process process = new ProcessBuilder(java, "-ea", "-cp", classes, "zsiggy.Duke")
                .directory(temporaryDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        try {
            process.getOutputStream().write(commands.getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().close();
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "CLI should exit after bye");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            process.destroyForcibly();
        }
    }
}
