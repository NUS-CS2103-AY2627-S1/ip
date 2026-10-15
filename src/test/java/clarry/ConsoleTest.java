package clarry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

import clarry.ui.Ui;

/** Runs the documented console transcripts without touching the user's saved tasks. */
@ResourceLock("systemStreams")
@ResourceLock("defaultLocale")
class ConsoleTest {
    @TempDir
    private Path directory;

    @TestFactory
    List<DynamicTest> run_documentedConsoleCases_matchesExactOutput() throws IOException {
        String plan = Files.readString(Path.of("test", "ui-test-plan.md")).replace("\r\n", "\n");
        Pattern pattern = Pattern.compile("(?ms)^## ([^\\n]+).*?### Inputs\\s+```text\\n(.*?)```"
                + ".*?### Expected output\\s+```text\\n(.*?)```");
        Matcher matcher = pattern.matcher(plan);
        List<DynamicTest> tests = new ArrayList<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            String input = matcher.group(2);
            String expected = matcher.group(3);
            tests.add(DynamicTest.dynamicTest(name, () -> {
                Path file = Files.createTempDirectory(directory, "session-").resolve("tasks.txt");
                assertEquals(expected, capture(input, () -> new Clarry(file).run()));
            }));
        }
        assertEquals(5, tests.size(), "Every documented console case must be discovered");
        return tests;
    }

    @Test
    void run_explicitBye_doesNotConsumeLaterCommands() {
        String output = capture("bye\ntodo ignored\n", () -> new Clarry(directory.resolve("tasks.txt")).run());
        assertTrue(output.contains("See you next tide!"));
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    @Test
    void showErrors_storageProblems_printsCompleteFramedMessages() {
        String divider = "_".repeat(60) + "\n";
        String actual = capture("", () -> {
            Ui ui = new Ui();
            ui.showSaveError();
            ui.showLoadError();
            ui.showCorruptedLineError();
        });
        assertEquals(divider + " Could not save tasks to disk.\n" + divider
                + divider + " Could not load saved tasks.\n" + divider
                + divider + " Skipping a corrupted line in the save file.\n" + divider, actual);
    }

    /** Captures console output, restoring process-wide streams and locale even after a failure. */
    private String capture(String input, Runnable action) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        Locale originalLocale = Locale.getDefault();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            Locale.setDefault(Locale.ENGLISH);
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            action.run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
            Locale.setDefault(originalLocale);
        }
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
