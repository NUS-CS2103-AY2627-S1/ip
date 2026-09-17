package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the real CLI in separate JVMs so its fixed save path never touches user data.
 */
public class PennyTest {
    @TempDir
    Path directory;

    @Test
    void main_freshSession_persistsSuccessfulCommandsOnly() throws Exception {
        String output = runSession("todo read\nmark 1\nfoobar\nlist\nbye\n");
        assertTrue(output.contains("Hi, I'm Penny, what can I do for you?"));
        assertFalse(output.contains("I have loaded your tasks"));
        assertTrue(output.contains("Added: [T][ ] read"));
        assertTrue(output.contains("Marked as done: [T][X] read"));
        assertTrue(output.contains("I don't think I understand."));
        assertTrue(output.contains("1. [T][X] read"));
        assertTrue(output.contains("Bye! See you soon!"));
        assertEquals("todo read\nmark 1", Files.readString(directory.resolve("commands.txt")));
    }

    @Test
    void main_secondSession_restoresAndUpdatesTasks() throws Exception {
        runSession("todo read\ndeadline report /by 15092026\nmark 2\nbye\n");
        String output = runSession("list\nunmark 2\ndelete 1\nbye\n");
        assertTrue(output.contains("I have loaded your tasks from our previous session."));
        assertTrue(output.contains("1. [T][ ] read\n2. [D][X] report (by: 15 09 2026)"));
        assertTrue(output.contains("Deleted: [T][ ] read"));
        String reloaded = runSession("list\nbye\n");
        assertTrue(reloaded.contains("1. [D][ ] report (by: 15 09 2026)"));
        assertFalse(reloaded.contains("[T]"));
    }

    @Test
    void main_corruptSavedCommand_stopsBeforeDependentCommands() throws Exception {
        Files.writeString(directory.resolve("commands.txt"), "todo read\nfoobar\ntodo write\nmark 2");
        String output = runSession("list\nbye\n");
        assertTrue(output.contains("Stopped loading at line 2: I don't think I understand."));
        assertTrue(output.contains("1. [T][ ] read"));
        assertFalse(output.contains("[T][X] write"));
        assertEquals("todo read\nfoobar\ntodo write\nmark 2",
                Files.readString(directory.resolve("commands.txt")));
    }

    @Test
    void main_endOfInput_exitsCleanly() throws Exception {
        assertTrue(runSession("").contains("Hi, I'm Penny"));
        runSession("todo read\n");
        assertEquals("todo read", Files.readString(directory.resolve("commands.txt")));
    }

    @Test
    void main_exitImmediately_doesNotAppendExitCommand() throws Exception {
        assertTrue(runSession("bye\n").contains("Bye! See you soon!"));
        assertEquals("", Files.readString(directory.resolve("commands.txt")));
    }

    /**
     * Runs the CLI with the test JVM's Java version and an isolated working directory.
     * Output goes to a file to avoid filling a subprocess pipe and blocking the test.
     */
    private String runSession(String input) throws Exception {
        String executable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classes = Path.of(Penny.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        Path output = directory.resolve("output.txt");
        Process process = new ProcessBuilder(executable, "-ea", "-cp", classes, "penny.Penny")
                .directory(directory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(output.toFile())
                .start();
        try {
            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "CLI did not exit after bye");
            String text = Files.readString(output).replace("\r\n", "\n");
            assertEquals(0, process.exitValue(), text);
            return text;
        } finally {
            process.destroyForcibly();
        }
    }
}
