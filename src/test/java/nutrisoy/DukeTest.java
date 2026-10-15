package nutrisoy;

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
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nutrisoy.ui.GuiResponse;

/** Integrates command execution, error metadata, console interaction, and persistence. */
public class DukeTest {
    @TempDir
    private Path directory;

    @Test
    public void responses_restart_restoresTypesTagsAndCompletion() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Duke duke = new Duke(file.toString());
        assertFalse(duke.getGuiResponse("todo read").isError());
        assertFalse(duke.getGuiResponse("deadline report /by 2028-02-29").isError());
        assertFalse(duke.getGuiResponse("event trip /from 2026-12-31 /to 2027-01-01").isError());
        assertFalse(duke.getGuiResponse("tag 2 work").isError());
        assertFalse(duke.getGuiResponse("mark 2").isError());

        Duke restarted = new Duke(file.toString());
        String response = restarted.getResponse("list");
        assertTrue(response.contains("1.[T][ ] read"));
        assertTrue(response.contains("2.[D][X] report #work"));
        assertTrue(response.contains("3.[E][ ] trip"));
        assertFalse(restarted.getGuiResponse("untag 2 WORK").isError());
        assertFalse(restarted.getGuiResponse("unmark 2").isError());
        assertFalse(restarted.getGuiResponse("delete 1").isError());
        assertEquals(List.of("D | 0 | report | 2028-02-29",
                "E | 0 | trip | 2026-12-31 | 2027-01-01"), Files.readAllLines(file));
        assertFalse(new Duke(file.toString()).getResponse("list").contains("read"));
    }

    @Test
    public void response_invalidThenValid_resetsErrorFlagAndCapturedText() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Duke duke = new Duke(file.toString());
        duke.getResponse("todo read");
        String saved = Files.readString(file);
        for (String input : List.of("nonsense", "delete 9", "deadline bad /by 2026-02-30")) {
            GuiResponse error = duke.getGuiResponse(input);
            assertTrue(error.isError(), input);
            assertTrue(error.getMessage().contains("Nice try."));
            assertEquals(saved, Files.readString(file));
        }
        GuiResponse success = duke.getGuiResponse("list");
        assertFalse(success.isError());
        assertTrue(success.getMessage().contains("read"));
        assertFalse(success.getMessage().contains("Nice try."));
    }

    @Test
    public void response_saveFailure_reportsErrorAndCanRetry() throws Exception {
        Path parent = directory.resolve("blocked");
        Files.writeString(parent, "blocking file");
        Duke duke = new Duke(parent.resolve("tasks.txt").toString());
        GuiResponse error = duke.getGuiResponse("todo read");
        assertTrue(error.isError());
        assertTrue(error.getMessage().contains("not a directory"));
        assertEquals("blocking file", Files.readString(parent));
        Files.move(parent, directory.resolve("original-blocker.txt"));
        GuiResponse retry = duke.getGuiResponse("list");
        assertFalse(retry.isError());
        assertEquals(List.of("T | 0 | read"), Files.readAllLines(parent.resolve("tasks.txt")));
    }

    @Test
    public void constructor_unreadablePath_reportsLoadFailure() {
        Duke duke = new Duke(directory.toString());
        assertFalse(duke.getGuiResponse("list").isError());
        assertTrue(duke.getStartupWarning().contains("not a readable file"));
        assertTrue(duke.getGuiResponse("todo read").isError());
    }

    @Test
    public void damagedFile_blocksChangesWithoutOverwritingReadableTasks() throws Exception {
        Path file = directory.resolve("damaged.txt");
        String original = "T | 0 | read\nnot a task\n";
        Files.writeString(file, original);
        Duke duke = new Duke(file.toString());
        assertTrue(duke.getStartupWarning().contains("line 2"));
        assertTrue(duke.getResponse("list").contains("read"));
        assertFalse(duke.getGuiResponse("find read").isError());
        assertTrue(duke.getGuiResponse("todo write").isError());
        assertFalse(duke.getResponse("list").contains("write"));
        assertTrue(duke.getGuiResponse("bye").isExit());
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void failedSave_blocksExitUntilRetrySucceeds() throws Exception {
        Path blocker = directory.resolve("blocker");
        Files.writeString(blocker, "keep");
        Duke duke = new Duke(blocker.resolve("tasks.txt").toString());
        GuiResponse response = duke.getGuiResponse("todo read");
        assertTrue(response.isError());
        assertFalse(response.getMessage().contains("I added"));
        assertTrue(response.getMessage().contains("only in this session"));
        assertTrue(duke.hasUnsavedChanges());
        assertFalse(duke.getGuiResponse("bye").isExit());
        Files.move(blocker, directory.resolve("original.txt"));
        assertTrue(duke.getGuiResponse("bye").isExit());
        assertFalse(duke.hasUnsavedChanges());
        assertEquals(List.of("T | 0 | read"), Files.readAllLines(blocker.resolve("tasks.txt")));
    }

    @Test
    public void emptyStartup_readOnlyCommands_doNotCreateDataFile() {
        Path file = directory.resolve("missing.txt");
        Duke duke = new Duke(file.toString());
        assertEquals("", duke.getStartupWarning());
        assertFalse(duke.getGuiResponse("list").isError());
        assertFalse(duke.getGuiResponse("find missing").isError());
        assertTrue(duke.getGuiResponse("bye").isExit());
        assertFalse(Files.exists(file));
    }

    @Test
    public void nonEnglishLocale_uppercaseCommandsAndUnicode_surviveRestart() {
        Locale original = Locale.getDefault();
        Path file = directory.resolve("unicode.txt");
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Duke duke = new Duke(file.toString());
            assertFalse(duke.getGuiResponse("DEADLINE 阅读 /by 2028-02-29").isError());
            assertFalse(duke.getGuiResponse("FIND 阅读").isError());
            assertTrue(new Duke(file.toString()).getResponse("LIST").contains("阅读"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void run_endOfInput_stopsCleanly() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        try (PrintStream capture = new PrintStream(new ByteArrayOutputStream())) {
            System.setIn(new ByteArrayInputStream(new byte[0]));
            System.setOut(capture);
            new Duke(directory.resolve("eof.txt").toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        assertFalse(Files.exists(directory.resolve("eof.txt")));
    }

    @Test
    public void run_invalidThenValidCommands_continuesAndSavesBeforeExit() throws Exception {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Path file = directory.resolve("console.txt");
        String script = "nonsense\ntodo read\nmark 1\nlist\nbye\n";
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capture);
            new Duke(file.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        String transcript = output.toString(StandardCharsets.UTF_8);
        assertTrue(transcript.contains("Soya"));
        assertTrue(transcript.contains("Nice try."));
        assertTrue(transcript.contains("1.[T][X] read"));
        assertTrue(transcript.contains("That's a wrap"));
        assertEquals(List.of("T | 1 | read"), Files.readAllLines(file));
    }
}
