package stewie.ui.gui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import stewie.model.TaskList;
import stewie.storage.Storage;
import stewie.ui.Dialogue;

/**
 * Verifies chat command replies and saved state without starting the JavaFX toolkit.
 */
class GuiCommandHandlerTest {
    @TempDir
    private Path directory;
    private Path file;
    private TaskList tasks;
    private GuiCommandHandler handler;

    @BeforeEach
    void setUp() {
        file = directory.resolve("tasks.txt");
        tasks = new TaskList(new Storage(file));
        handler = new GuiCommandHandler(tasks);
    }

    @Test
    void commands_preserveReplyOrderAndTaskDetails() {
        assertEquals(List.of(Dialogue.ADDED), handler.handle("todo read book").messages());
        assertEquals(List.of("Deadline recorded. Time is now officially judging you."),
                handler.handle("deadline report /by 2026-01-02").messages());
        assertEquals(List.of("Event scheduled. I trust the occasion warrants all this organisation."),
                handler.handle("event trip /from 2026-01-01 /to 2026-01-03").messages());

        GuiCommandResult listed = handler.handle("list");
        assertEquals(List.of(Dialogue.LIST), listed.messages());
        assertEquals(List.of("[T] [ ] read book", "[D] [ ] report (by: 02 Jan 2026)",
                "[E] [ ] trip (from: 01 Jan 2026 to: 03 Jan 2026)"), listed.tasks());
        assertTrue(listed.isInteractive());

        GuiCommandResult marked = handler.handle("mark 2");
        assertEquals(List.of("Completed. Rather well done, actually. Let us not make a scene.",
                "The revised agenda, for your inspection:"), marked.messages());
        assertEquals("[D] [X] report (by: 02 Jan 2026)", marked.tasks().get(1));
        GuiCommandResult updated = handler.handle("update 2 final report d/2026-01-04");
        assertEquals(List.of(Dialogue.UPDATED, "The revised agenda, for your inspection:"), updated.messages());
        assertEquals("[D] [X] final report (by: 04 Jan 2026)", updated.tasks().get(1));
        assertEquals("[D] [ ] final report (by: 04 Jan 2026)", handler.handle("unmark 2").tasks().get(1));
        assertEquals(2, handler.handle("delete 1").tasks().size());
        assertArrayEquals(tasks.produceTaskList(), new TaskList(new Storage(file)).produceTaskList());
        // A reply retains the task snapshot from its command even after later changes.
        assertEquals("[D] [ ] report (by: 02 Jan 2026)", listed.tasks().get(1));
    }

    @Test
    void search_returnsMatchingCardsWithoutNumberedActions() {
        handler.handle("todo first");
        handler.handle("todo second");
        GuiCommandResult found = handler.handle("find second");
        assertEquals(List.of("Aha. The evidence you requested:"), found.messages());
        assertEquals(List.of("[T] [ ] second"), found.tasks());
        assertFalse(found.isInteractive());
        GuiCommandResult missing = handler.handle("find absent");
        assertEquals(List.of("Nothing matches. Even my brilliance needs a clue. Try another keyword or `list`."),
                missing.messages());
        assertTrue(missing.tasks().isEmpty());
    }

    @Test
    void emptyListAndLastDeletion_includeEmptyStateAfterHeading() {
        assertEquals(List.of(Dialogue.LIST, Dialogue.EMPTY), handler.handle("list").messages());
        handler.handle("todo only task");
        GuiCommandResult deleted = handler.handle("delete 1");
        assertEquals(List.of("Deleted. I have dismissed it from our affairs.", "The surviving commitments:",
                Dialogue.EMPTY), deleted.messages());
        assertTrue(deleted.tasks().isEmpty());
    }

    @Test
    void invalidCommands_leaveSavedTasksUnchanged() throws IOException {
        handler.handle("todo original");
        byte[] saved = Files.readAllBytes(file);
        List<String> commands = List.of("mark 9", "unmark 0", "delete two", "update 1", "update 1 d/2026-01-01",
                "deadline bad /by 2026-02-30", "event missing", "todo original", "unknown", "find");
        for (String command : commands) {
            GuiCommandResult result = handler.handle(command);
            assertEquals(1, result.messages().size(), command);
            assertTrue(result.tasks().isEmpty(), command);
            assertFalse(result.isInteractive(), command);
            assertEquals(1, tasks.getRevision(), command);
            assertArrayEquals(saved, Files.readAllBytes(file), command);
        }
        assertEquals(List.of("A slight flaw in your plan: Use: todo <description>."),
                handler.handle("todo").messages());
    }

    @Test
    void failedSaves_replyWithErrorWithoutSuccessOrCards() throws IOException {
        handler.handle("todo original");
        Files.writeString(file, "T | 0 | external");
        for (String command : List.of("todo new", "mark 1", "unmark 1", "update 1 revised", "delete 1")) {
            GuiCommandResult result = handler.handle(command);
            assertEquals(List.of("The task file changed outside Stewie. Restart before making changes."),
                    result.messages(), command);
            assertTrue(result.tasks().isEmpty(), command);
            assertArrayEquals(new String[] {"[T] [ ] original"}, tasks.produceTaskList(), command);
            assertEquals(1, tasks.getRevision(), command);
            assertEquals("T | 0 | external", Files.readString(file), command);
        }
    }

    @Test
    void helpAndBye_doNotChangeTasks() {
        GuiCommandResult help = handler.handle("help");
        assertEquals(List.of(GuiCommandHandler.COMMAND_HELP), help.messages());
        assertTrue(help.tasks().isEmpty());
        assertEquals(List.of(Dialogue.GOODBYE), handler.handle("bye").messages());
        assertEquals(0, tasks.getRevision());
    }
}
