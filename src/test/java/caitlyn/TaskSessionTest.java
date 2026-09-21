package caitlyn;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests shared CLI/GUI protection, restart behavior, and rollback after save failures. */
class TaskSessionTest {
    private static final String BLOCKED_MESSAGE = "I beg your pardon, master. Task changes are disabled because "
            + "saved tasks could not be loaded. Repair data/duke.txt or its access permissions, "
            + "then restart Caitlyn.";
    private static final String SAVE_ERROR = "I beg your pardon, master. I could not save your tasks to disk.";

    @TempDir
    private Path temporaryDirectory;

    private Path taskFile;
    private TaskStorage storage;

    private final List<String> messages = new ArrayList<>();
    private final Ui ui = new Ui(messages::add);

    @BeforeEach
    void setUp() throws IOException {
        taskFile = temporaryDirectory.resolve("data").resolve("duke.txt");
        storage = new TaskStorage(taskFile);
        Files.createDirectories(taskFile.getParent());
    }

    @Test
    void execute_savedReversedEvent_protectsOriginalFile() throws Exception {
        Files.writeString(taskFile, "T | 1 | keep\nE | 0 | reversed | 2026-12-03T16:00 | 2026-12-03T15:00\n");
        byte[] original = Files.readAllBytes(taskFile);
        TaskSession session = new TaskSession(storage);
        assertTrue(session.hasLoadingError());
        assertEquals(0, session.getTaskCount());
        for (String input : List.of("todo blocked", "delete 1", "mark 1")) {
            assertEquals(BLOCKED_MESSAGE, assertThrows(CaitlynException.class, () ->
                    session.execute(parseCommand(input), ui)).getMessage());
            assertArrayEquals(original, Files.readAllBytes(taskFile));
        }
    }

    @Test
    void execute_loadFailure_blocksEveryMutationBeforeValidation() throws Exception {
        Files.writeString(taskFile, "T | 0 | keep\r\nW | 0 | invalid | 2027-01-26 | 2027-01-25\r\n");
        byte[] original = Files.readAllBytes(taskFile);
        TaskSession session = new TaskSession(storage);
        assertTrue(session.hasLoadingError());
        assertEquals(0, session.getTaskCount());
        for (String input : List.of("todo add", "deadline task /by 2027-01-15",
                "event task /from 2027-01-15 /to 2027-01-25",
                "within task /from 2027-01-15 /to 2027-01-25",
                "mark 999", "unmark 999", "delete 999", "todo", "within")) {
            CaitlynException exception = assertThrows(CaitlynException.class, () ->
                    session.execute(parseCommand(input), ui));
            assertEquals(BLOCKED_MESSAGE, exception.getMessage(), input);
            assertArrayEquals(original, Files.readAllBytes(taskFile), input);
            assertTrue(messages.isEmpty(), input);
        }
        session.execute(parseCommand("list"), ui);
        session.execute(parseCommand("find keep"), ui);
        assertEquals(List.of("     Here are the tasks in your list:",
                "     Here are the matching tasks in your list:"), messages);
        assertEquals("I beg your pardon, master. Please provide a keyword, for example: find book.",
                assertThrows(CaitlynException.class, () -> session.execute(parseCommand("find"), ui)).getMessage());
        assertEquals("I humbly beg your pardon, master. I do not know how to carry out that command.",
                assertThrows(CaitlynException.class, () -> session.execute(parseCommand("unknown"), ui)).getMessage());
        session.execute(parseCommand("bye"), ui);
        assertEquals("     Farewell, master. It has been my pleasure to serve you.", messages.get(2));
        assertArrayEquals(original, Files.readAllBytes(taskFile));
    }

    @Test
    void execute_repairedFile_requiresNewSessionBeforeChanges() throws Exception {
        Files.writeString(taskFile, "T | 2 | invalid\n");
        TaskSession blocked = new TaskSession(storage);
        Files.writeString(taskFile, "T | 1 | repaired\n");
        assertEquals(BLOCKED_MESSAGE, assertThrows(CaitlynException.class, () ->
                blocked.execute(parseCommand("within task /from 2027-01-15 /to 2027-01-25"), ui))
                .getMessage());

        TaskSession restarted = new TaskSession(storage);
        assertFalse(restarted.hasLoadingError());
        restarted.execute(parseCommand("within task /from 2027-01-15 /to 2027-01-25"), ui);
        assertEquals(List.of("T | 1 | repaired", "W | 0 | task | 2027-01-15 | 2027-01-25"),
                Files.readAllLines(taskFile));
        TaskSession reloaded = new TaskSession(storage);
        messages.clear();
        reloaded.execute(parseCommand("list"), ui);
        assertEquals(List.of("     Here are the tasks in your list:", "     1.[T][X] repaired",
                "     2.[W][ ] task (within: Jan 15 2027 to: Jan 25 2027)"), messages);
    }

    @Test
    void execute_missingOrBlankFile_allowsFirstTask() throws Exception {
        for (boolean hasBlankFile : List.of(false, true)) {
            Files.deleteIfExists(taskFile);
            if (hasBlankFile) {
                Files.writeString(taskFile, "\n \t\n");
            }
            TaskSession session = new TaskSession(storage);
            assertFalse(session.hasLoadingError());
            session.execute(parseCommand("within first /from 2027-01-15 /to 2027-01-25"), ui);
            assertEquals(1, session.getTaskCount());
            assertEquals(List.of("W | 0 | first | 2027-01-15 | 2027-01-25"), Files.readAllLines(taskFile));
        }
    }

    @Test
    void execute_unreadableFile_protectsSessionWithoutDeletingData() throws Exception {
        Files.createDirectory(taskFile);
        Files.writeString(taskFile.resolve("blocker"), "keep");
        TaskSession session = new TaskSession(storage);
        assertTrue(session.hasLoadingError());
        assertEquals(BLOCKED_MESSAGE, assertThrows(CaitlynException.class, () ->
                session.execute(parseCommand("todo replacement"), ui)).getMessage());
        assertEquals("keep", Files.readString(taskFile.resolve("blocker")));
    }

    @Test
    void execute_saveFailure_rollsBackEachChangeAndAllowsRetry() throws Exception {
        WithinTask incomplete = new WithinTask("incomplete", "2027-01-15", "2027-01-25");
        WithinTask complete = new WithinTask("complete", "2027-01-15", "2027-01-25");
        complete.markAsDone();
        storage.save(List.of(incomplete, complete));
        TaskSession session = new TaskSession(storage);
        Files.delete(taskFile);
        Files.createDirectory(taskFile);
        Files.writeString(taskFile.resolve("blocker"), "keep");

        for (String input : List.of("within third /from 2027-01-15 /to 2027-01-25",
                "mark 1", "unmark 2", "delete 1")) {
            messages.clear();
            assertEquals(SAVE_ERROR, assertThrows(CaitlynException.class, () ->
                    session.execute(parseCommand(input), ui)).getMessage());
            assertTrue(messages.isEmpty());
            assertEquals(2, session.getTaskCount());
            session.execute(parseCommand("list"), ui);
            assertEquals(List.of("     Here are the tasks in your list:",
                    "     1.[W][ ] incomplete (within: Jan 15 2027 to: Jan 25 2027)",
                    "     2.[W][X] complete (within: Jan 15 2027 to: Jan 25 2027)"), messages);
            assertEquals("keep", Files.readString(taskFile.resolve("blocker")));
        }
        Files.delete(taskFile.resolve("blocker"));
        Files.delete(taskFile);
        session.execute(parseCommand("mark 1"), ui);
        assertFalse(session.hasLoadingError());
        assertEquals(List.of("W | 1 | incomplete | 2027-01-15 | 2027-01-25",
                "W | 1 | complete | 2027-01-15 | 2027-01-25"), Files.readAllLines(taskFile));
    }

    /** Parses commands using the same temporary file as the session. */
    private Command parseCommand(String input) {
        return Parser.parse(input, storage);
    }
}
