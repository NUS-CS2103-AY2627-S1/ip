package oz;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the interactive console interface.
 */
public class ConsoleUiTest {

    /** Temporary folder for test storage files. */
    @TempDir
    Path temporaryFolder;

    @Test
    public void run_commandSequenceWithBye_executesAndExits() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        try {
            String simulatedInput = "todo read book\nlist\nbye\n";
            ByteArrayInputStream testIn = new ByteArrayInputStream(
                    simulatedInput.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();

            System.setIn(testIn);
            System.setOut(new PrintStream(testOut, true, StandardCharsets.UTF_8));

            Path storagePath = this.temporaryFolder.resolve("cli_tasks.txt");
            Oz oz = new Oz(storagePath.toString());
            new ConsoleUi(oz).run();

            String output = testOut.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Greetings! *oink*"));
            assertTrue(output.contains("Added to the list:"));
            assertTrue(output.contains("Here is the master task list:"));
            assertTrue(output.contains("Farewell! Back to my contraptions. *oink*"));
            assertTrue(oz.isExit());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
