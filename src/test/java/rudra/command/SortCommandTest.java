package rudra.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Deadline;
import rudra.task.Event;
import rudra.task.Task;
import rudra.task.TaskDateTime;
import rudra.task.ToDo;
import rudra.ui.Ui;

/**
 * Tests for {@link SortCommand}.
 */
public class SortCommandTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_mixedTasks_sortsStablyAndSavesOrder() throws RudraException {
        Task todo = new ToDo("buy milk");
        Task lateDeadline = new Deadline("submit report", TaskDateTime.parse("2026-09-20"));
        Task midnightDeadline = new Deadline("deploy release", TaskDateTime.parse("2026-09-10 0000"));
        Task event = new Event("team sync", TaskDateTime.parse("2026-09-15 1400"),
                TaskDateTime.parse("2026-09-15 1500"));
        Task dateOnlyDeadline = new Deadline("renew license", TaskDateTime.parse("2026-09-10"));
        dateOnlyDeadline.markAsDone();
        Task secondTodo = new ToDo("call Alice");
        Task earlyDeadline = new Deadline("pay bill", TaskDateTime.parse("2026-09-05"));
        ArrayList<Task> tasks = new ArrayList<>(List.of(todo, lateDeadline, midnightDeadline, event,
                dateOnlyDeadline, secondTodo, earlyDeadline));
        ArrayList<String> messages = new ArrayList<>();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        new SortCommand().execute(tasks, new Ui(messages::add), storage);

        List<Task> expectedTasks = List.of(earlyDeadline, midnightDeadline, dateOnlyDeadline, lateDeadline,
                todo, event, secondTodo);
        assertIterableEquals(expectedTasks, tasks);
        assertSame(midnightDeadline, tasks.get(1));
        assertSame(dateOnlyDeadline, tasks.get(2));
        assertEquals(List.of(
                "All set - your deadlines now run from earliest to latest.",
                "Here's what's on your radar:",
                "1.[D][ ] pay bill (by: Sep 5 2026)",
                "2.[D][ ] deploy release (by: Sep 10 2026 12:00am)",
                "3.[D][X] renew license (by: Sep 10 2026)",
                "4.[D][ ] submit report (by: Sep 20 2026)",
                "5.[T][ ] buy milk",
                "6.[E][ ] team sync (from: Sep 15 2026 2:00pm to: Sep 15 2026 3:00pm)",
                "7.[T][ ] call Alice",
                "____________________________________________________________"), messages);

        List<String> reloadedTaskLines = storage.loadTasks().getTasks().stream()
                .map(Task::toStorageString)
                .toList();
        assertEquals(tasks.stream().map(Task::toStorageString).toList(), reloadedTaskLines);
    }

    @Test
    public void execute_saveFails_restoresOriginalOrder() throws RudraException {
        Task todo = new ToDo("buy milk");
        Task laterDeadline = new Deadline("submit report", TaskDateTime.parse("2026-09-20"));
        Task earlierDeadline = new Deadline("pay bill", TaskDateTime.parse("2026-09-05"));
        ArrayList<Task> tasks = new ArrayList<>(List.of(todo, laterDeadline, earlierDeadline));
        List<Task> originalTasks = List.copyOf(tasks);
        ArrayList<String> messages = new ArrayList<>();
        Storage failingStorage = new Storage(tempDir.resolve("tasks.txt").toString()) {
            @Override
            public void saveTasks(List<Task> tasksToSave) throws RudraException {
                throw new RudraException("I couldn't save your tasks.");
            }
        };

        RudraException exception = assertThrows(RudraException.class, () ->
                new SortCommand().execute(tasks, new Ui(messages::add), failingStorage));

        assertEquals("I couldn't save your tasks. Your task list was left unchanged.", exception.getMessage());
        assertIterableEquals(originalTasks, tasks);
        assertEquals(List.of(), messages);
    }
}
