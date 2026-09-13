package odysseus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

/**
 * Tests keyword search through the console command path.
 */
class FindCommandTest {
    @Test
    void find_matchingKeyword_showsOriginalTaskNumbers() throws Exception {
        String output = run("""
                todo read book
                todo pay bills
                deadline return book /by 2019-10-15
                find BOOK
                bye
                """);

        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1. [T][ ] read book"));
        assertTrue(output.contains("3. [D][ ] return book (by: Oct 15 2019)"));
        String searchOutput = output.substring(output.indexOf("Here are the matching tasks"));
        assertFalse(searchOutput.contains("2. [T][ ] pay bills"));
    }

    @Test
    void find_missingKeyword_reportsErrorWithoutChangingTasks() throws Exception {
        String output = run("""
                todo read book
                find
                list
                bye
                """);

        assertTrue(output.contains("Provide a keyword after find"));
        assertTrue(output.contains("1. [T][ ] read book"));
    }

    private String run(String commands) throws Exception {
        Path storagePath = Files.createTempDirectory("odysseus-find-test").resolve("tasks.txt");
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner(commands)) {
            Odysseus.run(scanner, output, storagePath);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
