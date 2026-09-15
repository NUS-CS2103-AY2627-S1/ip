package seedu.goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises complete command sequences against isolated saved data. */
public class GoatTest {
    @TempDir
    private Path directory;

    private Goat createGoat() {
        return new Goat(directory.resolve("data/goat.txt").toString());
    }

    @Test
    public void commands_completeLifecycle_survivesRestart() {
        Goat goat = createGoat();
        assertTrue(goat.getGreeting().contains("GOAT"));
        assertEquals("", goat.getLoadOutcome());
        assertTrue(goat.getResponse("todo read book").contains("added"));
        assertTrue(goat.getResponse("deadline submit iP /by 2026-09-18").contains("added"));
        assertTrue(goat.getResponse("event retreat /from 2026-09-15 /to 2026-09-17").contains("added"));
        assertTrue(goat.getResponse("mark 2").contains("[D][X]"));
        Goat restored = createGoat();
        assertTrue(restored.getLoadOutcome().contains("3 tasks"));
        assertEquals(goat.getResponse("list"), restored.getResponse("list"));
        assertTrue(restored.getResponse("unmark 2").contains("[D][ ]"));
        assertTrue(restored.getResponse("find BOOK").contains("read book"));
        assertTrue(restored.getResponse("on 2026-09-16").contains("retreat"));
        assertTrue(restored.getResponse("delete 1").contains("removed"));
        assertFalse(createGoat().getResponse("list").contains("read book"));
        assertTrue(restored.getResponse("bye").contains("Bye"));
        assertTrue(restored.isFinished());
    }

    @Test
    public void commands_invalidInput_keepsListAndConversationUsable() {
        Goat goat = createGoat();
        goat.getResponse("todo read book");
        String before = goat.getResponse("list");
        for (String input : List.of("", "  ", "unknown", "list extra", "bye extra", "todo",
                "mark", "unmark", "delete", "mark 0", "mark -1", "delete 2", "unmark 2147483648",
                "mark 1 2", "on", "find", "deadline book /by 2026-02-30",
                "event trip /from 2026-09-18 /to 2026-09-17", "todo bad | field")) {
            assertFalse(goat.getResponse(input).isBlank(), input);
            assertEquals(before, goat.getResponse("list"), input);
            assertFalse(goat.isFinished(), input);
        }
        assertFalse(goat.getResponse(null).isBlank());
        assertTrue(goat.getResponse("  todo\tnew task  ").contains("added"));
    }

    @Test
    public void commands_emptyList_reportUsefulOutcomes() {
        Goat goat = createGoat();
        for (String command : List.of("mark 1", "unmark 1", "delete 1", "list", "find absent", "on 2026-09-18")) {
            assertFalse(goat.getResponse(command).isBlank());
        }
    }

    @Test
    public void add_duplicateDoneTask_refusedWithoutSaving() throws IOException {
        Goat goat = createGoat();
        goat.getResponse("todo Read  Book");
        goat.getResponse("mark 1");
        Path file = directory.resolve("data/goat.txt");
        String saved = Files.readString(file);
        assertTrue(goat.getResponse("todo read book").contains("already have"));
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void mutations_externalEdit_rollBackAllChanges() throws IOException {
        Goat goat = createGoat();
        goat.getResponse("todo original");
        String before = goat.getResponse("list");
        Path file = directory.resolve("data/goat.txt");
        Files.writeString(file, "T | 1 | externally changed\n");
        for (String input : List.of("todo new", "delete 1", "mark 1", "unmark 1")) {
            assertTrue(goat.getResponse(input).contains("No change was applied"));
            assertEquals(before, goat.getResponse("list"));
            assertEquals("T | 1 | externally changed\n", Files.readString(file));
        }
    }

    @Test
    public void load_damagedFile_keepsRecoveredTasksReadOnly() throws IOException {
        Files.createDirectories(directory.resolve("data"));
        Path file = directory.resolve("data/goat.txt");
        String original = "T | 0 | recover me\nbroken record\n";
        Files.writeString(file, original);
        Goat goat = createGoat();
        assertTrue(goat.getLoadOutcome().contains("disabled"));
        assertTrue(goat.getResponse("list").contains("recover me"));
        assertTrue(goat.getResponse("delete 1").contains("disabled"));
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void load_unreadableLocation_remainsUsableWithoutSaving() throws IOException {
        Files.createDirectories(directory.resolve("data/goat.txt"));
        Goat goat = createGoat();
        assertTrue(goat.getLoadOutcome().contains("could not read"));
        assertTrue(goat.getResponse("todo task").contains("disabled"));
        assertFalse(goat.getResponse("list").contains("[T]"));
    }
}
