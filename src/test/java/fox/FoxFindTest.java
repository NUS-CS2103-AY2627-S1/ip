package fox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/** Integration tests for Level-9 keyword searches and existing task commands. */
public class FoxFindTest {
    /** Runs all find-command tests. */
    public static void main(String[] args) throws Exception {
        testValidCaseInsensitiveWholeWordSearch();
        testMultipleKeywordsUseOrMatching();
        testNoMatchesAndMalformedCommands();
        testExistingCommandsStillWork();
    }

    private static void testValidCaseInsensitiveWholeWordSearch() throws Exception {
        String output = runFox("todo Buy milk\n"
                + "todo buying milkshake\n"
                + "todo READ a book\n"
                + "find BUY\nbye\n");
        assertContains(output, "1.[T][ ] Buy milk");
        assertNotContains(output, "2.[T][ ] buying milkshake\n    __");
        assertNotContains(output, "3.[T][ ] READ a book\n    __");
    }

    private static void testMultipleKeywordsUseOrMatching() throws Exception {
        String output = runFox("todo read book\n"
                + "todo attend project meeting\n"
                + "todo plan holiday\n"
                + "find BOOK PROJECT\nbye\n");
        assertContains(output, "1.[T][ ] read book");
        assertContains(output, "2.[T][ ] attend project meeting");
        assertNotContains(output, "3.[T][ ] plan holiday\n    __");
    }

    private static void testNoMatchesAndMalformedCommands() throws Exception {
        String output = runFox("todo buy milk\nfind absent\nfind\nfind   \nbye\n");
        assertContains(output, "There are no matching tasks.");
        assertContains(output, "Please provide at least one keyword to find.");
        assertOccurrences(output, "Please provide at least one keyword to find.", 2);
    }

    private static void testExistingCommandsStillWork() throws Exception {
        String output = runFox("todo remember this\nmark 1\nfind REMEMBER\nunmark 1\nlist\nbye\n");
        assertContains(output, "1.[T][X] remember this");
        assertContains(output, "1.[T][ ] remember this");
        assertContains(output, "Here are the tasks in your list:");
    }

    private static String runFox(String input) throws IOException, InterruptedException {
        Path directory = Files.createTempDirectory("fox-find");
        String classPath = Path.of(System.getProperty("java.class.path")).toAbsolutePath().toString();
        Process process = new ProcessBuilder(System.getProperty("java.home") + "/bin/java",
                "-cp", classPath, "fox.Fox")
                .directory(directory.toFile())
                .redirectErrorStream(true)
                .start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();
        if (!process.waitFor(10, TimeUnit.SECONDS) || process.exitValue() != 0) {
            throw new AssertionError("Fox process did not finish successfully");
        }
        return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void assertContains(String output, String expected) {
        assert output.contains(expected) : "Expected output to contain: " + expected;
    }

    private static void assertNotContains(String output, String unexpected) {
        assert !output.contains(unexpected) : "Expected output not to contain: " + unexpected;
    }

    private static void assertOccurrences(String output, String expected, int count) {
        int occurrences = 0;
        int start = 0;
        while ((start = output.indexOf(expected, start)) >= 0) {
            occurrences++;
            start += expected.length();
        }
        assert occurrences == count : "Expected " + count + " occurrences but found " + occurrences;
    }
}
