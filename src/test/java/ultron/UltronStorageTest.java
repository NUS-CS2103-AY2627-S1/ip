package ultron;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises writing tasks to storage and restoring them on the next startup.
 */
public class UltronStorageTest {
    /**
     * Verifies that changed tasks are saved and restored on the next startup.
     *
     * @throws Exception if the temporary storage file cannot be used
     */
    @Test
    void savesAndRestoresTasks() throws Exception {
        Path testSaveFile = Files.createTempFile("ultron-storage-test", ".txt");
        Files.delete(testSaveFile);
        String originalSaveFile = System.getProperty("ultron.saveFile");

        try {
            System.setProperty("ultron.saveFile", testSaveFile.toString());
            String commands = "todo read book\n"
                    + "deadline return book /by 2/12/2019\n"
                    + "event project meeting /from 2pm /to 3pm\n"
                    + "mark 2\n"
                    + "unmark 2\n"
                    + "delete 3\n"
                    + "bye\n";
            runUltron(commands);

            List<String> expectedLines = List.of(
                    "T | 0 | read book",
                    "D | 0 | return book( by: 02/Dec/2019 )");
            List<String> actualLines = Files.readAllLines(testSaveFile, StandardCharsets.UTF_8);
            assertEquals(expectedLines, actualLines);

            String restoredOutput = runUltron("list\nbye\n");
            assertTrue(restoredOutput.contains("1.[T] [ ] read book"));
            assertTrue(restoredOutput.contains("2.[D] [ ] return book( by: 02/Dec/2019 )"));
        } finally {
            if (originalSaveFile == null) {
                System.clearProperty("ultron.saveFile");
            } else {
                System.setProperty("ultron.saveFile", originalSaveFile);
            }
            Files.deleteIfExists(testSaveFile);
        }
    }

    /**
     * Runs the chatbot with the supplied commands and captures its output.
     *
     * @param commands newline-separated chatbot commands
     * @return the chatbot output
     */
    private static String runUltron(String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput));
            Ultron.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return capturedOutput.toString();
    }
}
