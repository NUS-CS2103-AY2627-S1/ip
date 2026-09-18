package rem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Tests Rem's blocking console command loop with isolated system streams.
 */
@ResourceLock("SYSTEM_STREAMS")
public class RemRunTest {
    @TempDir
    Path directory;

    @Test
    public void run_commandsUntilBye_processesCommandsAndClosesNormally() throws Exception {
        Path dataFile = directory.resolve("rem.txt");
        String output = runConsole("todo read book\nlist\nbye\n", dataFile);

        assertTrue(output.contains("Rem: Got it! I put it on the list:\nRem: [T][ ] read book\n"));
        assertTrue(output.contains("Rem: 1.[T][ ] read book\n"));
        assertTrue(output.contains("Rem: [Yawn] Need more sleep. Time for bed...\n"));
        assertTrue(output.endsWith("____________________________________________________________\n"));
        assertTrue(Files.readString(dataFile).contains("T | 0 | read book"));
    }

    @Test
    public void run_endOfInput_stopsWithoutFarewell() throws Exception {
        Path dataFile = directory.resolve("rem.txt");
        String output = runConsole("", dataFile);

        assertTrue(output.contains("Rem: Hi! I'm Rem.\n"));
        assertTrue(output.endsWith("Me: "));
        assertFalse(output.contains("Time for bed"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void run_loadFailure_printsWarningAndErrorBeforeReading() throws Exception {
        Path dataDirectory = Files.createDirectory(directory.resolve("rem.txt"));
        String output = runConsole("bye\n", dataDirectory);

        assertTrue(output.contains("Rem: Oh. I couldn't load your saved tasks. I'm showing an empty list.\n"));
        assertTrue(output.contains("Rem: The saved task path is a directory."));
        assertTrue(output.contains("Rem: [Yawn] Need more sleep. Time for bed...\n"));
    }

    private static String runConsole(String input, Path dataFile) {
        PrintStream originalOutput = System.out;
        java.io.InputStream originalInput = System.in;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            new Rem(dataFile.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
