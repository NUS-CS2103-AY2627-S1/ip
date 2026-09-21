package caitlyn;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the real CLI in isolated processes, including startup, shutdown, and locale settings. */
class CaitlynTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void main_emptyInput_exitsWithoutCreatingDataOrSayingFarewell() throws Exception {
        String output = runCli("", "en", "US");
        assertTrue(output.contains("How may I serve you today?"));
        assertFalse(output.contains("Farewell"));
        assertFalse(Files.exists(taskFile()));
    }

    @Test
    void main_byeWithMoreInput_stopsBeforeExecutingLaterCommands() throws Exception {
        String output = runCli("bye\ntodo must not run\n", "en", "US");
        assertTrue(output.contains("Farewell, master. It has been my pleasure to serve you."));
        assertFalse(output.contains("I've added this task"));
        assertFalse(Files.exists(taskFile()));
    }

    @Test
    void main_invalidCommandsThenValidTask_recoversAndSavesAtEndOfInput() throws Exception {
        String output = runCli("\ntodo\nunknown\ntodo keep\nlist", "en", "US");
        assertTrue(output.contains("I do not know how to carry out that command."));
        assertTrue(output.contains("I cannot prepare a task without a description."));
        assertTrue(output.contains("1.[T][ ] keep"));
        assertFalse(output.contains("Farewell"));
        assertEquals(List.of("T | 0 | keep"), Files.readAllLines(taskFile()));
    }

    @Test
    void main_restart_restoresAllTaskTypesAndCompletion() throws Exception {
        runCli("todo 阅读\ndeadline report /by 2027-01-15\n"
                + "event call /from 2027-01-15 /to 2027-01-25\n"
                + "within collect /from 2027-01-15 /to 2027-01-25\nmark 4\nbye\n", "en", "US");
        byte[] saved = Files.readAllBytes(taskFile());
        String output = runCli("list\nbye\n", "en", "US");
        assertTrue(output.contains("1.[T][ ] 阅读"));
        assertTrue(output.contains("2.[D][ ] report (by: Jan 15 2027)"));
        assertTrue(output.contains("3.[E][ ] call (from: Jan 15 2027 to: Jan 25 2027)"));
        assertTrue(output.contains("4.[W][X] collect (within: Jan 15 2027 to: Jan 25 2027)"));
        assertArrayEquals(saved, Files.readAllBytes(taskFile()));
    }

    @Test
    void main_corruptSavedData_blocksChangesAndPreservesBytes() throws Exception {
        Files.createDirectories(taskFile().getParent());
        byte[] original = "T | 1 | keep\r\nT | 2 | invalid\r\n".getBytes(StandardCharsets.UTF_8);
        Files.write(taskFile(), original);
        String output = runCli("todo blocked\nlist\nfind keep\nbye\n", "en", "US");
        assertTrue(output.contains("I could not read data/duke.txt."));
        assertTrue(output.contains("Task changes are disabled because saved tasks could not be loaded."));
        assertTrue(output.contains("Here are the tasks in your list:"));
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("Farewell"));
        assertFalse(output.contains("1.[T]"));
        assertArrayEquals(original, Files.readAllBytes(taskFile()));
    }

    @Test
    void main_nonEnglishLocales_preservesUnicodeDatesAndCaseInsensitiveSearch() throws Exception {
        for (String[] locale : List.of(new String[]{"en", "US"}, new String[]{"zh", "CN"},
                new String[]{"tr", "TR"})) {
            Files.deleteIfExists(taskFile());
            String output = runCli("todo WRITE 阅读 📚\ndeadline report /by 15/1/2027 1200\nfind write\nbye\n",
                    locale[0], locale[1]);
            assertTrue(output.contains("1.[T][ ] WRITE 阅读 📚"), locale[0]);
            assertTrue(output.contains("[D][ ] report (by: Jan 15 2027 12:00 PM)"), locale[0]);
            assertEquals(List.of("T | 0 | WRITE 阅读 📚", "D | 0 | report | 2027-01-15T12:00"),
                    Files.readAllLines(taskFile()), locale[0]);
        }
    }

    /** Returns the isolated file used by the application's default storage constructor. */
    private Path taskFile() {
        return temporaryDirectory.resolve("data").resolve("duke.txt");
    }

    /** Runs a Java 25 CLI with a bounded lifetime and captures UTF-8 output without pipe deadlocks. */
    private String runCli(String input, String language, String country) throws Exception {
        Path outputFile = temporaryDirectory.resolve("console.txt");
        List<String> arguments = new ArrayList<>();
        arguments.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        addCoverageAgent(arguments);
        arguments.addAll(List.of("-ea", "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8",
                "-Dstderr.encoding=UTF-8", "-Duser.language=" + language, "-Duser.country=" + country,
                "-cp", Path.of(Caitlyn.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                Caitlyn.class.getName()));
        Process process = new ProcessBuilder(arguments).directory(temporaryDirectory.toFile())
                .redirectErrorStream(true).redirectOutput(outputFile.toFile()).start();
        try {
            try (var inputStream = process.getOutputStream()) {
                inputStream.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "CLI must exit after bye or end of input");
            String output = Files.readString(outputFile);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            process.destroyForcibly();
        }
    }

    /** Reuses Gradle's JaCoCo agent with a separate file so subprocess coverage joins the report. */
    private void addCoverageAgent(List<String> arguments) throws IOException {
        String coverageDirectory = System.getProperty("caitlyn.cliCoverageDirectory");
        if (coverageDirectory == null) {
            return;
        }
        for (String argument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (argument.startsWith("-javaagent:") && argument.contains("jacoco")) {
                Path destination = Path.of(coverageDirectory).resolve(UUID.randomUUID() + ".exec");
                Files.createDirectories(destination.getParent());
                arguments.add(argument.substring(0, argument.indexOf('=')) + "=destfile=" + destination);
                return;
            }
        }
    }
}
