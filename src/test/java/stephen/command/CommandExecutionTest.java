package stephen.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.Deadline;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.task.Todo;
import stephen.ui.Ui;

/** Tests each command's observable behavior and persistence boundary. */
class CommandExecutionTest {
    @TempDir
    private Path testDirectory;

    /** Verifies that modifying commands update memory, storage, and user feedback. */
    @Test
    void executeModifyingCommandsUpdatesAndPersistsTaskList() throws Exception {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList(List.of());
        RecordingUi ui = new RecordingUi();

        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);
        new MarkCommand(0).execute(tasks, ui, storage);
        new UnmarkCommand(0).execute(tasks, ui, storage);
        new DeleteCommand(0).execute(tasks, ui, storage);

        assertEquals(List.of(), tasks.getTasks());
        assertEquals(List.of(), Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertEquals(List.of(
                "added:read book:1",
                "marked:read book:true",
                "marked:read book:false",
                "deleted:read book:0"), ui.events);
    }

    /** Verifies that read-only commands delegate the expected data to the UI. */
    @Test
    void executeReadOnlyCommandsDisplaysExpectedResults() throws Exception {
        LocalDate dueDate = LocalDate.of(2024, 6, 15);
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", dueDate)));
        RecordingUi ui = new RecordingUi();
        Storage unusedStorage = new Storage(testDirectory.resolve("unused.txt"));

        new ListCommand().execute(tasks, ui, unusedStorage);
        new FindCommand("book").execute(tasks, ui, unusedStorage);
        new ScheduleCommand(dueDate).execute(tasks, ui, unusedStorage);
        ExitCommand exitCommand = new ExitCommand();
        exitCommand.execute(tasks, ui, unusedStorage);

        assertEquals(List.of("list:2", "find:book:2", "schedule:2024-06-15", "goodbye"),
                ui.events);
        assertTrue(exitCommand.isExit());
        assertFalse(new ListCommand().isExit());
        assertFalse(Files.exists(testDirectory.resolve("unused.txt")));
    }

    /** Verifies that storage failures become friendly application errors. */
    @Test
    void executeAddWhenSaveFailsThrowsChatbotException() throws IOException {
        Path directoryAsFile = testDirectory.resolve("blocked.txt");
        Files.createDirectory(directoryAsFile);
        AddCommand command = new AddCommand(new Todo("unsaved task"));

        ChatbotException exception = assertThrows(ChatbotException.class, () ->
                command.execute(new TaskList(List.of()), new RecordingUi(),
                        new Storage(directoryAsFile)));

        assertEquals("I couldn't save your tasks. Please check the data folder.",
                exception.getMessage());
    }

    /** Captures command-to-UI delegation without depending on console formatting. */
    private static class RecordingUi extends Ui {
        private final List<String> events = new ArrayList<>();

        @Override
        public void showTaskAdded(Task task, int taskCount) {
            events.add("added:" + task.getDescription() + ":" + taskCount);
        }

        @Override
        public void showTaskDeleted(Task task, int taskCount) {
            events.add("deleted:" + task.getDescription() + ":" + taskCount);
        }

        @Override
        public void showTaskMarked(Task task, boolean isDone) {
            events.add("marked:" + task.getDescription() + ":" + isDone);
        }

        @Override
        public void showTaskList(TaskList tasks) {
            events.add("list:" + tasks.size());
        }

        @Override
        public void showFindResults(List<Task> matches, String keyword) {
            events.add("find:" + keyword + ":" + matches.size());
        }

        @Override
        public void showSchedule(TaskList tasks, LocalDate date) {
            events.add("schedule:" + date);
        }

        @Override
        public void showGoodbye() {
            events.add("goodbye");
        }
    }
}
