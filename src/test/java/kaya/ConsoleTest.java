package kaya;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

import kaya.task.Todo;
import kaya.ui.Ui;

/**
 * Exercises console sessions with disposable input, captured output, and temporary task files.
 */
@ResourceLock("system-streams")
public class ConsoleTest {
    @TempDir
    private Path temporaryDirectory;

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    public void captureConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
        setInput("");
    }

    @AfterEach
    public void restoreConsole() {
        System.out.close();
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void run_multipleCommands_stopsOnlyAtExactByeAndSavesEarlierChanges() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        setInput("todo read book\nbye later\nlist\nbye\ntodo must not run\n");

        new Kaya(file).run();

        assertTrue(output().contains("Hey, I'm Kaya."));
        assertTrue(output().contains("does not take any extra details"));
        assertTrue(output().contains("Here's what's on your plate:\n1.[T][ ] read book"));
        assertTrue(output().contains("See you! One thing at a time, okay?"));
        assertFalse(output().contains("must not run"));
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book", new Kaya(file).getResponse("list"));
    }

    @Test
    public void run_emptyInput_endsCleanlyWithoutCreatingADataFile() {
        Path file = temporaryDirectory.resolve("tasks.txt");

        new Kaya(file).run();

        assertTrue(output().contains("Grab a kopi"));
        assertFalse(output().contains("See you!"));
        assertFalse(Files.exists(file));
    }

    @Test
    public void run_endOfInputAfterCommands_keepsUnicodeTasksAndSavedStatus() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        setInput("  todo 读书  \nmark 1\n");

        new Kaya(file).run();

        assertTrue(output().contains("Settled! You've finished:"));
        assertFalse(output().contains("See you!"));
        assertEquals("Here's what's on your plate:\n1.[T][X] 读书", new Kaya(file).getResponse("list"));
    }

    @Test
    public void run_corruptedStartupData_printsWarningAndStillAllowsReading() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | cmVhZCBib29r\nbroken record\n";
        Files.writeString(file, original);
        setInput("list\nbye\n");
        Kaya kaya = new Kaya(file);

        kaya.run();

        assertTrue(output().contains(kaya.getStartupWarning()));
        assertTrue(output().contains("1.[T][ ] read book"));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void uiOutput_emptyAndPopulatedLists_keepsHeadingsNumbersAndErrorDetails() {
        Ui ui = new Ui();
        Todo completed = new Todo("读书");
        completed.markAsDone();

        ui.showTasks(List.of());
        ui.showTasks(List.of(completed, new Todo("return book")));
        ui.showMatchingTasks(List.of(completed));
        ui.showMatchingTasks(List.of());
        ui.showError("The data file is read-only.");
        ui.close();

        assertEquals("Your task list is empty. Time for a kopi break?\n"
                + "Here's what's on your plate:\n1.[T][X] 读书\n2.[T][ ] return book\n"
                + "Here's what I found on your plate:\n1.[T][X] 读书\n"
                + "No matching tasks on your plate. Try another keyword.\n"
                + "Sorry, The data file is read-only.\n", output());
    }

    @Test
    public void main_chineseLocaleInSeparateProcess_usesTemporaryDataAndEnglishDateFormat() throws Exception {
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classes = Path.of(Kaya.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        Path transcript = temporaryDirectory.resolve("console.txt");
        Process process = new ProcessBuilder(javaExecutable, "-Duser.language=zh", "-Duser.country=CN",
                "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                "-cp", classes, "kaya.Kaya")
                .directory(temporaryDirectory.toFile()).redirectErrorStream(true).redirectOutput(transcript.toFile())
                .start();
        try {
            try (var input = process.getOutputStream()) {
                input.write("deadline 交报告 /by 2026-09-20\nbye\n".getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Console application did not exit after bye.");
            assertEquals(0, process.exitValue(), Files.readString(transcript));
            assertTrue(Files.readString(transcript).contains("[D][ ] 交报告 (by: Sep 20 2026)"));
            Path file = temporaryDirectory.resolve("data").resolve("kaya.txt");
            assertEquals("Here's what's on your plate:\n1.[D][ ] 交报告 (by: Sep 20 2026)",
                    new Kaya(file).getResponse("list"));
        } finally {
            process.destroyForcibly();
            process.waitFor(5, TimeUnit.SECONDS);
        }
    }

    /** Supplies UTF-8 input before constructing the UI and its scanner. */
    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    /** Normalises platform line endings for portable output assertions. */
    private String output() {
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
