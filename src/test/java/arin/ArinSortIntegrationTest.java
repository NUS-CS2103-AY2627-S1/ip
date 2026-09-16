package arin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises sorting through the console without touching the user's task file.
 */
class ArinSortIntegrationTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void main_endOfInput_exitsNormallyAndLoadsTasksOnRestart() throws Exception {
        assertTrue(runConsole("todo persisted\n").contains("added"));
        assertTrue(runConsole("list\n").contains("1. [T][ ] persisted"));
    }

    @Test
    void main_sortThenEditTasks_usesNewTaskNumbers() throws Exception {
        String output = runConsole(String.join("\n",
                "todo zebra",
                "deadline beta /by 2026-09-15",
                "event charlie /from 2pm /to 4pm",
                "deadline alpha /by 2026-09-11",
                "sort deadline",
                "mark 1",
                "sort   name  ",
                "unmark 1",
                "delete 2",
                "find alpha",
                "list",
                "bye", ""));

        assertTrue(output.contains(String.join(System.lineSeparator(),
                "1. [D][ ] alpha (by: Sep 11 2026)",
                "2. [D][ ] beta (by: Sep 15 2026)",
                "3. [T][ ] zebra",
                "4. [E][ ] charlie (from: 2pm to: 4pm)")));
        assertTrue(output.contains("1. [D][X] alpha (by: Sep 11 2026)"));
        assertTrue(output.contains(String.join(System.lineSeparator(),
                "Here are the matching tasks in your list:",
                "1. [D][ ] alpha (by: Sep 11 2026)")));
        assertEquals(List.of(
                "D |   | alpha | 2026-09-11",
                "E |   | charlie | 2pm | 4pm",
                "T |   | zebra"
        ), Files.readAllLines(temporaryDirectory.resolve("data/arin.txt")));
    }

    @Test
    void main_invalidSortCommands_showsErrorsAndKeepsOrder() throws Exception {
        String output = runConsole(String.join("\n",
                "todo zebra",
                "todo apple",
                "sort",
                "sort date",
                "sort name extra",
                "sorter name",
                "list",
                "bye", ""));

        assertEquals(3, output.lines()
                .filter(line -> line.equals("Oops! Use: sort name OR sort deadline.")).count());
        assertTrue(output.contains("delete, find, sort, or bye."));
        assertTrue(output.contains(String.join(System.lineSeparator(),
                "1. [T][ ] zebra", "2. [T][ ] apple")));
        assertEquals(List.of("T |   | zebra", "T |   | apple"),
                Files.readAllLines(temporaryDirectory.resolve("data/arin.txt")));
    }

    /**
     * Runs the console in an isolated directory using the test JVM's Java version.
     *
     * @param commands Newline-separated commands ending with {@code bye}.
     * @return Console output after a successful exit.
     * @throws Exception If starting, communicating with, or waiting for the console fails.
     */
    private String runConsole(String commands) throws Exception {
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classPath = Path.of(Arin.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        Process process = new ProcessBuilder(javaExecutable, "-ea", "-cp", classPath, "arin.Arin")
                .directory(temporaryDirectory.toFile())
                .redirectErrorStream(true)
                .start();

        try {
            try (OutputStream input = process.getOutputStream()) {
                input.write(commands.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Console did not exit after bye.");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            process.destroyForcibly();
        }
    }
}
