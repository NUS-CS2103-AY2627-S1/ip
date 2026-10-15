package alex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alex.storage.Storage;
import alex.storage.StorageException;
import alex.task.Deadline;
import alex.task.Task;
import alex.task.Todo;
import alex.util.TaskDateTime;

/**
 * Tests command execution behavior.
 */
public class AlexTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_upcomingCommand_returnsUpcomingDeadlines() throws StorageException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(
                new Deadline("submit report", new TaskDateTime(
                        LocalDate.of(2026, 9, 16), LocalTime.of(18, 0))),
                new Deadline("later task", new TaskDateTime(
                        LocalDate.of(2026, 9, 18), LocalTime.of(12, 0)))));
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-16T04:00:00Z"), ZoneId.of("Asia/Singapore"));
        Alex alex = new Alex(dataFile.toString(), clock);

        CommandResult result = alex.getResponse("upcoming");

        assertEquals(new CommandResult(
                "These are due in the next 24 hours. They're starting to steam:\n"
                        + " 1.[D][ ] submit report (by: Sep 16 2026 1800)",
                false), result);
    }

    @Test
    public void getResponse_upcomingCommandWithoutMatches_returnsEmptyMessage() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-16T04:00:00Z"), ZoneId.of("Asia/Singapore"));
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString(), clock);

        CommandResult result = alex.getResponse("upcoming");

        assertEquals(new CommandResult(
                "Nothing due in the next 24 hours. Slow shift, apparently.", false), result);
    }

    @Test
    public void getResponse_findCommandWithoutMatches_returnsEmptyMessage() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("find report");

        assertEquals(new CommandResult(
                "Nothing matching 'report'. Maybe it ordered under a different name.", false),
                result);
    }

    @Test
    public void getResponse_taskCreationCommands_persistsAllTaskTypes() {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Alex alex = new Alex(dataFile.toString());

        assertEquals(new CommandResult(
                "One task, house blend. Added to your order:\n"
                        + "   [T][ ] read book\n"
                        + "You've got 1 item(s) brewing.", false),
                alex.getResponse("todo read book"));
        assertEquals(new CommandResult(
                "One deadline with an extra shot of urgency. Coming right up:\n"
                        + "   [D][ ] submit report (by: Dec 2 2019 1800)\n"
                        + "You've got 2 item(s) brewing.", false),
                alex.getResponse("deadline submit report /by 2019-12-02 1800"));
        assertEquals(new CommandResult(
                "All right, one reservation for your schedule:\n"
                        + "   [E][ ] meeting (from: Dec 3 2019 0900 to: Dec 3 2019 1000)\n"
                        + "You've got 3 item(s) brewing.", false),
                alex.getResponse("event meeting /from 2019-12-03 0900 /to 2019-12-03 1000"));

        Alex reloadedAlex = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "Let me check the order slip. Here's what you've got:\n"
                        + " 1.[T][ ] read book\n"
                        + " 2.[D][ ] submit report (by: Dec 2 2019 1800)\n"
                        + " 3.[E][ ] meeting (from: Dec 3 2019 0900 to: Dec 3 2019 1000)",
                false), reloadedAlex.getResponse("list"));
    }

    @Test
    public void getResponse_taskStateCommands_persistChangesAndOrder() throws StorageException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(
                new Todo("first task"),
                new Todo("second task"),
                new Todo("third task")));
        Alex alex = new Alex(dataFile.toString());

        assertEquals(new CommandResult(
                "Done. One task served and off the counter:\n   [T][X] second task", false),
                alex.getResponse("mark 2"));
        assertTrue(storage.loadTasks().get(1).isDone());

        Alex reloadedAfterMark = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "Not finished? Fine. Back into the order queue it goes:\n"
                        + "   [T][ ] second task", false),
                reloadedAfterMark.getResponse("unmark 2"));
        assertFalse(storage.loadTasks().get(1).isDone());

        Alex reloadedAfterUnmark = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "Canceled. I'll toss the order slip:\n"
                        + "   [T][ ] second task\n"
                        + "You've got 2 item(s) left brewing.", false),
                reloadedAfterUnmark.getResponse("delete 2"));

        Alex reloadedAfterDelete = new Alex(dataFile.toString());
        assertEquals(new CommandResult(
                "Let me check the order slip. Here's what you've got:\n"
                        + " 1.[T][ ] first task\n"
                        + " 2.[T][ ] third task", false),
                reloadedAfterDelete.getResponse("list"));
    }

    @Test
    public void getResponse_simpleCommands_returnsExpectedResults() throws StorageException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(new Todo("read book")));
        Alex alex = new Alex(dataFile.toString());

        assertEquals(new CommandResult(
                "Found these tucked behind the espresso machine:\n 1.[T][ ] read book", false),
                alex.getResponse("find book"));
        assertEquals(new CommandResult(
                "All right, closing your tab. Try not to leave your tasks on the table.",
                false, true),
                alex.getResponse("bye"));
        assertEquals(new CommandResult(
                "That's not on the menu. Try 'list', 'todo', 'deadline', or 'event'.", true),
                alex.getResponse("dance"));
    }

    @Test
    public void getResponse_invalidStoredTask_returnsLoadingError() throws IOException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Files.writeString(dataFile, "X | 0 | invalid task", StandardCharsets.UTF_8);
        Alex alex = new Alex(dataFile.toString());

        CommandResult result = alex.getResponse("list");

        assertEquals(new CommandResult(
                "The data file is invalid at line 1: unknown task type 'X'\n"
                        + "Please repair or remove the data file, then restart Alex.",
                true), result);
    }

    @Test
    public void getResponse_blankCommand_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("   ");

        assertEquals(new CommandResult(
                "You'll have to order something. I can't work with an empty cup.", true), result);
    }

    @Test
    public void getResponse_listCommandWithDetails_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("list extra details");

        assertEquals(new CommandResult(
                "The 'list' order comes as-is. No extras needed.", true), result);
    }

    @Test
    public void getResponse_upcomingCommandWithDetails_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("upcoming extra details");

        assertEquals(new CommandResult(
                "The 'upcoming' order comes as-is. No extras needed.", true), result);
    }

    @Test
    public void getResponse_byeCommandWithDetails_returnsSpecificError() {
        Alex alex = new Alex(tempDirectory.resolve("alex.txt").toString());

        CommandResult result = alex.getResponse("bye extra details");

        assertEquals(new CommandResult(
                "The 'bye' order comes as-is. No extras needed.", true), result);
    }

    @Test
    public void getResponse_addWhenSaveFails_doesNotAddTask() {
        Alex alex = createAlexWithSaveFailure(List.of());

        CommandResult result = alex.getResponse("todo read book");

        assertEquals(new CommandResult("Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Let me check the order slip. Here's what you've got:", false),
                alex.getResponse("list"));
    }

    @Test
    public void getResponse_markWhenSaveFails_doesNotMarkTask() {
        Alex alex = createAlexWithSaveFailure(List.of(new Todo("read book")));

        CommandResult result = alex.getResponse("mark 1");

        assertEquals(new CommandResult("Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Let me check the order slip. Here's what you've got:\n 1.[T][ ] read book", false),
                alex.getResponse("list"));
    }

    @Test
    public void getResponse_unmarkWhenSaveFails_doesNotUnmarkTask() {
        Task task = new Todo("read book");
        task.markAsDone();
        Alex alex = createAlexWithSaveFailure(List.of(task));

        CommandResult result = alex.getResponse("unmark 1");

        assertEquals(new CommandResult("Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Let me check the order slip. Here's what you've got:\n 1.[T][X] read book", false),
                alex.getResponse("list"));
    }

    @Test
    public void getResponse_deleteWhenSaveFails_doesNotDeleteTask() {
        Alex alex = createAlexWithSaveFailure(List.of(
                new Todo("first task"),
                new Todo("second task"),
                new Todo("third task")));

        CommandResult result = alex.getResponse("delete 2");

        assertEquals(new CommandResult("Simulated save failure.", true), result);
        assertEquals(new CommandResult(
                "Let me check the order slip. Here's what you've got:\n"
                        + " 1.[T][ ] first task\n"
                        + " 2.[T][ ] second task\n"
                        + " 3.[T][ ] third task", false),
                alex.getResponse("list"));
    }

    private Alex createAlexWithSaveFailure(List<Task> initialTasks) {
        return new Alex(new SaveFailingStorage(tempDirectory.resolve("alex.txt"), initialTasks),
                Clock.systemDefaultZone());
    }

    private static class SaveFailingStorage extends Storage {
        private final List<Task> initialTasks;

        SaveFailingStorage(Path filePath, List<Task> initialTasks) {
            super(filePath);
            this.initialTasks = initialTasks;
        }

        @Override
        public ArrayList<Task> loadTasks() {
            return new ArrayList<>(initialTasks);
        }

        @Override
        public void saveTasks(List<Task> tasks) throws StorageException {
            throw new StorageException("Simulated save failure.");
        }
    }
}
