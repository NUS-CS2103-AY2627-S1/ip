package charlie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charlie.exception.CharlieException;
import charlie.storage.Storage;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.TaskList;
import charlie.task.Todo;
import charlie.ui.Ui;

public class CommandTest {
    @Test
    public void addCommand_execute_addsSavesAndDisplaysTask(@TempDir Path temporaryDirectory)
            throws IOException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("borrow book", false);
        List<String> messages = new ArrayList<>();
        Storage storage = createStorage(temporaryDirectory);

        new AddCommand(todo).execute(tasks, new Ui(messages::add), storage);

        assertEquals(List.of(todo), tasks.getTasks());
        assertEquals("T | Not done | borrow book" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("charlie.txt")));
        assertEquals(List.of(
                "A happy little roar! I've tucked this task safely into the nest:",
                "  [T][ ] borrow book",
                "There is now 1 task in our nest."), messages);
    }

    @Test
    public void addCommand_saveFails_doesNotAddOrDisplay(@TempDir Path temporaryDirectory)
            throws IOException {
        TaskList tasks = new TaskList();
        List<String> messages = new ArrayList<>();
        Storage storage = createFailingStorage(temporaryDirectory);
        AddCommand command = new AddCommand(new Todo("borrow book", false));

        CharlieException exception = assertThrows(
                CharlieException.class, () -> command.execute(tasks, new Ui(messages::add), storage));

        assertEquals("Could not save tasks.", exception.getMessage());
        assertTrue(tasks.getTasks().isEmpty());
        assertTrue(messages.isEmpty());
    }

    @Test
    public void deleteCommand_execute_deletesSavesAndDisplaysTask(@TempDir Path temporaryDirectory)
            throws IOException {
        Todo firstTask = new Todo("first", false);
        Todo secondTask = new Todo("second", false);
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        List<String> messages = new ArrayList<>();

        new DeleteCommand(0).execute(
                tasks, new Ui(messages::add), createStorage(temporaryDirectory));

        assertEquals(List.of(secondTask), tasks.getTasks());
        assertEquals("T | Not done | second" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("charlie.txt")));
        assertEquals(List.of(
                "I've cleared this task from our trail:",
                "  [T][ ] first",
                "There is now 1 task in our nest."), messages);
    }

    @Test
    public void deleteCommand_saveFails_keepsTask(@TempDir Path temporaryDirectory)
            throws IOException {
        Todo todo = new Todo("first", false);
        TaskList tasks = new TaskList(List.of(todo));
        DeleteCommand command = new DeleteCommand(0);

        assertThrows(
                CharlieException.class, () -> command.execute(
                        tasks, new Ui(message -> { }), createFailingStorage(temporaryDirectory)));

        assertEquals(List.of(todo), tasks.getTasks());
    }

    @Test
    public void markAndUnmarkCommands_execute_updatePersistAndDisplay(
            @TempDir Path temporaryDirectory) throws IOException {
        TaskList tasks = new TaskList(List.of(new Todo("borrow book", false)));
        Storage storage = createStorage(temporaryDirectory);
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new MarkCommand(0).execute(tasks, ui, storage);

        assertEquals("[T][X] borrow book", tasks.get(0).toString());
        assertTrue(Files.readString(temporaryDirectory.resolve("charlie.txt")).contains("Done"));
        assertEquals(List.of(
                "Tiny victory roar! This task is done:",
                "  [T][X] borrow book"), messages);

        messages.clear();
        new UnmarkCommand(0).execute(tasks, ui, storage);

        assertEquals("[T][ ] borrow book", tasks.get(0).toString());
        assertTrue(Files.readString(temporaryDirectory.resolve("charlie.txt")).contains("Not done"));
        assertEquals(List.of(
                "That's okay - this task isn't quite ready yet:",
                "  [T][ ] borrow book"), messages);
    }

    @Test
    public void statusCommand_saveFails_keepsOriginalStatus(@TempDir Path temporaryDirectory)
            throws IOException {
        TaskList tasks = new TaskList(List.of(new Todo("borrow book", false)));
        MarkCommand command = new MarkCommand(0);

        assertThrows(
                CharlieException.class, () -> command.execute(
                        tasks, new Ui(message -> { }), createFailingStorage(temporaryDirectory)));

        assertEquals("[T][ ] borrow book", tasks.get(0).toString());
    }

    @Test
    public void listCommand_execute_displaysTasksInOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("first", false),
                new Todo("second", true)));
        List<String> messages = new ArrayList<>();

        new ListCommand().execute(tasks, new Ui(messages::add), null);

        assertEquals(List.of(
                "Here's what's currently in our task nest:",
                "1.[T][ ] first",
                "2.[T][X] second"), messages);
    }

    @Test
    public void listCommand_emptyList_displaysEmptyMessage() {
        TaskList tasks = new TaskList();
        List<String> messages = new ArrayList<>();

        new ListCommand().execute(tasks, new Ui(messages::add), null);

        assertEquals(List.of("Your task nest is empty."), messages);
    }

    @Test
    public void findCommand_matchesAndNoMatches_displaysExpectedMessages() {
        TaskList tasks = new TaskList(List.of(
                new Todo("borrow book", false),
                new Todo("return book", true)));
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new FindCommand("book").execute(tasks, ui, null);

        assertEquals(List.of(
                "I sniffed around and found these matching tasks:",
                "1.[T][ ] borrow book",
                "2.[T][X] return book"), messages);

        messages.clear();
        new FindCommand("report").execute(tasks, ui, null);

        assertEquals(List.of(
                "I sniffed around and found these matching tasks:",
                "Hmm... I couldn't track down a matching task."), messages);
    }

    @Test
    public void onCommand_matchesAndNoMatches_displaysExpectedMessages() {
        LocalDate searchDate = LocalDate.of(2026, 9, 21);
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", false, searchDate),
                new Event(
                        "meeting",
                        false,
                        LocalDateTime.of(2026, 9, 20, 14, 0),
                        LocalDateTime.of(2026, 9, 22, 16, 0))));
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new OnCommand(searchDate).execute(tasks, ui, null);

        assertEquals(3, messages.size());
        assertEquals("Here's what I found for 2026-09-21:", messages.get(0));
        assertTrue(messages.get(1).startsWith("1.[D]"));
        assertTrue(messages.get(2).startsWith("2.[E]"));

        messages.clear();
        new OnCommand(LocalDate.of(2025, 1, 1)).execute(tasks, ui, null);

        assertEquals(List.of(
                "Here's what I found for 2025-01-01:",
                "Hmm... I couldn't track down anything for this date."), messages);
    }

    @Test
    public void exitCommand_execute_displaysOutroAndSignalsExit() {
        List<String> messages = new ArrayList<>();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), new Ui(messages::add), null);

        assertEquals(
                List.of("Bye for now! I'll guard the task nest until you return."),
                messages);
        assertTrue(command.isExit());
        assertFalse(new ListCommand().isExit());
    }

    @Test
    public void commandType_parseKeyword_recognizesAllKeywords() {
        for (CommandType commandType : CommandType.values()) {
            assertEquals(commandType, CommandType.parseKeyword(commandType.name().toLowerCase()));
        }
    }

    @Test
    public void commandType_unknownKeyword_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> CommandType.parseKeyword("unknown"));

        assertEquals("Oh! I don't recognize that command yet.", exception.getMessage());
    }

    private Storage createStorage(Path temporaryDirectory) {
        return new Storage(temporaryDirectory.resolve("charlie.txt").toString());
    }

    private Storage createFailingStorage(Path temporaryDirectory) throws IOException {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "not a directory");
        return new Storage(blockedParent.resolve("charlie.txt").toString());
    }
}
