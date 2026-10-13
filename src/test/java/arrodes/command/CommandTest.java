package arrodes.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import arrodes.exception.ArrodesException;
import arrodes.storage.Storage;
import arrodes.task.Deadline;
import arrodes.task.Event;
import arrodes.task.TaskList;
import arrodes.task.Todo;
import arrodes.ui.Ui;

/** Tests command execution against task lists, storage, and a message sink. */
class CommandTest {

    private static final LocalDateTime MORNING = LocalDateTime.of(2026, 10, 15, 9, 0);
    private static final LocalDateTime AFTERNOON = LocalDateTime.of(2026, 10, 15, 17, 0);

    /** Adds a todo, saves it, and reports the new count. */
    @Test
    void todoExecute_addsTaskAndReportsResult() {
        TaskList taskList = new TaskList(10);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new TodoCommand("buy milk").execute(ui, taskList, storage);

        assertEquals(1, taskList.getSize());
        assertEquals(1, storage.saveCount);
        assertTrue(ui.lastMessage().contains("[T][ ] buy milk"));
    }

    /** Adds a deadline and an event while preserving their display formats. */
    @Test
    void deadlineAndEventExecute_addTasksAndReportResults() {
        TaskList taskList = new TaskList(10);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new DeadlineCommand("submit report", MORNING).execute(ui, taskList, storage);
        new EventCommand("team meeting", MORNING, AFTERNOON, true, true)
                .execute(ui, taskList, storage);

        assertEquals(2, taskList.getSize());
        assertEquals(2, storage.saveCount);
        assertTrue(ui.lastMessage().contains("[E][ ] team meeting"));
    }

    /** Rejects an event whose end precedes its start before changing the list. */
    @Test
    void eventExecute_reversedTimes_throwsWithoutAddingTask() {
        TaskList taskList = new TaskList(10);
        LocalDateTime end = MORNING.minusHours(1);

        assertThrows(ArrodesException.class, () -> new EventCommand("invalid meeting", MORNING, end, true, true)
                        .execute(new RecordingUi(), taskList, new RecordingStorage()));

        assertEquals(0, taskList.getSize());
    }

    /** Marks and reopens a task, persisting each status change. */
    @Test
    void markAndUnmarkExecute_updatesStatusAndReportsResult() {
        TaskList taskList = new TaskList(10);
        taskList.insert(new Todo("read book"));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new MarkCommand(1).execute(ui, taskList, storage);
        assertTrue(taskList.getTaskByNumber(1).isDone());
        assertTrue(ui.lastMessage().contains("[T][X] read book"));

        new UnmarkCommand(1).execute(ui, taskList, storage);
        assertFalse(taskList.getTaskByNumber(1).isDone());
        assertTrue(ui.lastMessage().contains("[T][ ] read book"));
        assertEquals(2, storage.saveCount);
    }

    /** Tags a task and deletes it, reporting both mutations. */
    @Test
    void tagAndDeleteExecute_updatesListAndReportsResults() {
        TaskList taskList = new TaskList(10);
        taskList.insert(new Todo("read book"));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new TagCommand(1, "personal").execute(ui, taskList, storage);
        assertEquals("personal", taskList.getTaskByNumber(1).getTag());
        assertTrue(ui.lastMessage().contains("#personal"));

        new DeleteCommand(1).execute(ui, taskList, storage);
        assertEquals(0, taskList.getSize());
        assertTrue(ui.lastMessage().contains("read book #personal"));
        assertEquals(2, storage.saveCount);
    }

    /** Restores a task's old state when marking fails to save. */
    @Test
    void markExecute_saveFailure_rollsBackStatus() {
        TaskList taskList = new TaskList(10);
        taskList.insert(new Todo("read book"));

        assertThrows(ArrodesException.class, () -> new MarkCommand(1)
                .execute(new RecordingUi(), taskList, failingStorage()));

        assertFalse(taskList.getTaskByNumber(1).isDone());
    }

    /** Restores a task's old tag when tagging fails to save. */
    @Test
    void tagExecute_saveFailure_rollsBackTag() {
        TaskList taskList = new TaskList(10);
        Todo todo = new Todo("read book");
        todo.tagWith("old");
        taskList.insert(todo);

        assertThrows(ArrodesException.class, () -> new TagCommand(1, "new")
                .execute(new RecordingUi(), taskList, failingStorage()));

        assertEquals("old", taskList.getTaskByNumber(1).getTag());
    }

    /** Restores a deleted task at its original position when deletion cannot be saved. */
    @Test
    void deleteExecute_saveFailure_restoresTask() {
        TaskList taskList = new TaskList(10);
        taskList.insert(new Todo("first"));
        taskList.insert(new Todo("second"));

        assertThrows(ArrodesException.class, () -> new DeleteCommand(1)
                .execute(new RecordingUi(), taskList, failingStorage()));

        assertEquals(2, taskList.getSize());
        assertEquals("first", taskList.getTaskByNumber(1).getDescription());
    }

    /** Finds no matching tasks and reports the empty-result message. */
    @Test
    void findExecute_noMatch_reportsNoMatch() {
        RecordingUi ui = new RecordingUi();

        new FindCommand("missing").execute(ui, new TaskList(10), null);

        assertEquals("The mirror finds no record matching those words.", ui.lastMessage());
    }

    /** Lists tasks in their one-based display order. */
    @Test
    void listExecute_tasksAreNumberedInOrder() {
        TaskList taskList = new TaskList(10);
        taskList.insert(new Todo("first"));
        taskList.insert(new Todo("second"));
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(ui, taskList, null);

        assertTrue(ui.lastMessage().contains("    1.[T][ ] first"));
        assertTrue(ui.lastMessage().contains("    2.[T][ ] second"));
    }

    /** Displays the farewell and marks a bye command as an exit command. */
    @Test
    void byeExecute_isExitAndSendsFarewell() {
        RecordingUi ui = new RecordingUi();
        ByeCommand command = new ByeCommand();

        command.execute(ui, null, null);

        assertTrue(command.isExit());
        assertEquals(1, ui.messages.size());
    }

    /** Shows deadlines and overlapping events for a date-only query. */
    @Test
    void upcomingExecute_dateQuery_showsDeadlinesAndOverlappingEvents() {
        TaskList taskList = new TaskList(10);
        taskList.insert(new Deadline("submit report", AFTERNOON));
        taskList.insert(new Event("overnight meeting", MORNING.minusDays(1).withHour(23),
                MORNING.plusHours(2)));
        taskList.insert(new Todo("buy milk"));
        RecordingUi ui = new RecordingUi();

        new UpcomingCommand(LocalDateTime.of(2026, 10, 15, 0, 0), false)
                .execute(ui, taskList, null);

        assertTrue(ui.lastMessage().contains("    1.[D][ ] submit report"));
        assertTrue(ui.lastMessage().contains("    2.[E][ ] overnight meeting"));
        assertEquals(1, ui.messages.size());
    }

    /** Shows only an event containing the queried time for a time-specific query. */
    @Test
    void upcomingExecute_timeQuery_excludesTasksOutsideQueryTime() {
        TaskList taskList = new TaskList(10);
        taskList.insert(new Event("meeting", MORNING, AFTERNOON));
        taskList.insert(new Deadline("later deadline", AFTERNOON.plusHours(1)));
        RecordingUi ui = new RecordingUi();

        new UpcomingCommand(LocalDateTime.of(2026, 10, 15, 12, 0), true)
                .execute(ui, taskList, null);

        assertTrue(ui.lastMessage().contains("    1.[E][ ] meeting"));
        assertFalse(ui.lastMessage().contains("later deadline"));
    }

    /** Reports when an upcoming query has no matching tasks. */
    @Test
    void upcomingExecute_noMatch_reportsNoMatch() {
        RecordingUi ui = new RecordingUi();

        new UpcomingCommand(LocalDateTime.of(2026, 10, 15, 0, 0), false)
                .execute(ui, new TaskList(10), null);

        assertEquals(2, ui.messages.size());
        assertEquals("The mirror finds no deadlines or events for that appointed hour.", ui.lastMessage());
    }

    /** Displays the command guide without changing the task list. */
    @Test
    void helpExecute_displaysCommandGuide() {
        RecordingUi ui = new RecordingUi();

        new HelpCommand().execute(ui, new TaskList(10), null);

        assertTrue(ui.lastMessage().contains("todo <description>"));
        assertTrue(ui.lastMessage().contains("upcoming /on <yyyy-MM-ddTHH:mm>"));
    }

    /** Confirms that ordinary commands do not request application termination. */
    @Test
    void todoCommand_isNotExit() {
        assertFalse(new TodoCommand("buy milk").isExit());
    }

    /** Creates a storage double that fails every save. */
    private Storage failingStorage() {
        return new Storage(Path.of("build", "test-command-data", "unused.txt")) {
            @Override
            public void save(TaskList taskList) {
                throw new ArrodesException(ArrodesException.SAVE_FAILURE);
            }
        };
    }

    /** Captures messages sent through the application UI. */
    private static class RecordingUi implements Ui {
        private final List<String> messages = new ArrayList<>();

        @Override
        public String readUserInput() {
            return null;
        }

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showOnLoadMessage() {
        }

        @Override
        public void showSeparator() {
        }

        @Override
        public void showOnExitMessage() {
            messages.add("farewell");
        }

        private String lastMessage() {
            return messages.get(messages.size() - 1);
        }
    }

    /** Records persistence calls while leaving the filesystem untouched. */
    private static class RecordingStorage extends Storage {
        private int saveCount;

        RecordingStorage() {
            super(Path.of("build", "test-command-data", "unused.txt"));
        }

        @Override
        public void save(TaskList taskList) {
            saveCount++;
        }
    }
}
