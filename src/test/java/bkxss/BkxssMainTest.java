package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the actual console entry point in child JVMs with isolated working directories. */
class BkxssMainTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void main_byeWithWhitespace_printsGreetingAndStopsBeforeLaterCommands() throws Exception {
        String output = runSession("list\n  bye  \ntodo should not run\n");

        assertTrue(output.contains("Hello hello ~ This is Bkxss here ;)"));
        assertTrue(output.contains("What can I do for you?"));
        assertTrue(output.contains("Here are the tasks in your list:"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertFalse(output.contains("should not run"));
        assertFalse(Files.exists(temporaryDirectory.resolve("data/bkxss.txt")));
    }

    @Test
    void main_byeWithUnicodeWhitespace_stopsBeforeLaterCommands() throws Exception {
        String output = runSession("\u2003bye\u00a0\ntodo should not run\n");

        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertFalse(output.contains("should not run"));
        assertFalse(Files.exists(temporaryDirectory.resolve("data/bkxss.txt")));
    }

    @Test
    void main_endOfInput_exitsWithoutRequiringBye() throws Exception {
        String output = runSession("bye extra\ntodo keep me\nlist\n");

        assertTrue(output.contains("list and bye do not accept extra arguments."));
        assertTrue(output.contains("1.[T][ ] keep me"));
        assertFalse(output.contains("Bye. Hope to see you again soon!"));
        assertEquals("T | 0 | keep me\n", readSavedTasks());
    }

    @Test
    void main_emptyInput_printsGreetingAndExitsWithoutCreatingStorage() throws Exception {
        String output = runSession("");

        assertTrue(output.contains("What can I do for you?"));
        assertFalse(output.contains("ERROR"));
        assertFalse(Files.exists(temporaryDirectory.resolve("data")));
    }

    @Test
    void main_restart_preservesUnicodeTypesOrderAndCompletion() throws Exception {
        runSession("todo 买书\ndeadline return book /by 2028-02-29 1800\n"
                + "event meeting /from 2028-02-29 2300 /to 2028-03-01 0100\nmark 2\nbye\n");

        String output = runSession("list\nunmark 2\ndelete 1\nbye\n");
        assertTrue(output.contains("1.[T][ ] 买书"));
        assertTrue(output.contains("2.[D][X] return book (by: Feb 29 2028 18:00)"));
        assertTrue(output.contains("3.[E][ ] meeting (from: 2028-02-29 2300 to: 2028-03-01 0100)"));
        assertEquals("D | 0 | return book | Feb 29 2028 18:00\n"
                + "E | 0 | meeting | 2028-02-29 2300 to 2028-03-01 0100\n", readSavedTasks());
        String restartedOutput = runSession("list\nbye\n");
        assertTrue(restartedOutput.contains("1.[D][ ] return book (by: Feb 29 2028 18:00)"));
        assertTrue(restartedOutput.contains("2.[E][ ] meeting"));
        assertFalse(restartedOutput.contains("买书"));
    }

    @Test
    void main_corruptStorage_warnsAndKeepsValidTasksWithoutOverwritingFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data/bkxss.txt");
        Files.createDirectories(dataFile.getParent());
        String savedText = "T | 0 | first\nbroken record\nT | 1 | last\n";
        Files.writeString(dataFile, savedText);

        String output = runSession("list\ndelete 1\nlist\nbye\n");

        assertTrue(output.contains("I skipped invalid saved task on line 2."));
        assertTrue(output.contains("the data file could not be fully loaded."));
        assertEquals(2, output.lines().filter(line -> line.contains("1.[T][ ] first")).count());
        assertEquals(2, output.lines().filter(line -> line.contains("2.[T][X] last")).count());
        assertFalse(output.contains("I've removed"));
        assertEquals(savedText, readSavedTasks());
    }

    /** Runs a real console session without touching the repository's data or global standard streams. */
    private String runSession(String input) throws Exception {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path classes = Path.of(Bkxss.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        ArrayList<String> command = new ArrayList<>(List.of(java.toString()));
        String utf8 = StandardCharsets.UTF_8.name();
        // Include console entry-point execution in the same report when Gradle enables JaCoCo.
        ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                .map(BkxssMainTest::withAbsoluteCoveragePath)
                .forEach(command::add);
        command.addAll(List.of("-Dfile.encoding=" + utf8, "-Dstdin.encoding=" + utf8,
                "-Dstdout.encoding=" + utf8, "-Dstderr.encoding=" + utf8,
                "-cp", classes.toString(), "bkxss.Bkxss"));
        Path outputFile = Files.createTempFile(temporaryDirectory, "console-", ".txt");
        Process process = new ProcessBuilder(command).directory(temporaryDirectory.toFile())
                .redirectErrorStream(true).redirectOutput(outputFile.toFile()).start();
        try {
            try (var inputStream = process.getOutputStream()) {
                inputStream.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "Console session did not exit");
            String output = Files.readString(outputFile, StandardCharsets.UTF_8).replace("\r\n", "\n");
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly().waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    /** Keeps child coverage in Gradle's report even though the child has a temporary working directory. */
    private static String withAbsoluteCoveragePath(String agentArgument) {
        return Pattern.compile("destfile=([^,]+)").matcher(agentArgument).replaceFirst(match ->
                Matcher.quoteReplacement("destfile=" + Path.of(match.group(1)).toAbsolutePath()));
    }

    /** Normalizes only platform line endings, retaining the exact persisted records. */
    private String readSavedTasks() throws Exception {
        return Files.readString(temporaryDirectory.resolve("data/bkxss.txt"), StandardCharsets.UTF_8)
                .replace("\r\n", "\n");
    }
}
