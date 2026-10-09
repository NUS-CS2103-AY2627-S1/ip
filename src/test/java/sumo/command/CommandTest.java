package sumo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sumo.storage.Storage;
import sumo.task.Deadline;
import sumo.task.Task;
import sumo.task.TaskList;
import sumo.task.Todo;
import sumo.ui.Ui;

/** Tests commands whose behavior does not mutate the task list. */
public class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that ordinary commands leave the session open. */
    @Test
    public void command_defaultExitState_false() {
        Command command = new ListCommand();

        assertFalse(command.isExit());
    }

    /** Verifies that exiting displays a farewell and signals the end of the session. */
    @Test
    public void exitCommand_executeAndExitState_goodbyeEmittedAndTrueReturned() throws IOException {
        RecordingUi ui = new RecordingUi();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), ui, createStorage());

        assertEquals(List.of("goodbye"), ui.calls);
        assertTrue(command.isExit());
    }

    /** Verifies that listing displays the current task collection. */
    @Test
    public void listCommand_execute_currentTasksDisplayed() throws IOException {
        Todo todo = new Todo("read");
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(new TaskList(List.of(todo)), ui, createStorage());

        assertEquals(List.of("list:read"), ui.calls);
    }

    /** Verifies that keyword searches display only matching tasks. */
    @Test
    public void findCommand_execute_onlyMatchingTasksDisplayed() throws IOException {
        RecordingUi ui = new RecordingUi();
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write notes")));

        new FindCommand("book").execute(tasks, ui, createStorage());

        assertEquals(List.of("matches:read book"), ui.calls);
    }

    /** Verifies that date queries display only tasks occurring on the requested date. */
    @Test
    public void onCommand_execute_onlyTasksOnDateDisplayed() throws IOException {
        RecordingUi ui = new RecordingUi();
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 2, 3));

        new OnCommand(LocalDate.of(2026, 2, 3))
                .execute(new TaskList(List.of(deadline)), ui, createStorage());

        assertEquals(List.of("on:2026-02-03:submit"), ui.calls);
    }

    /** Verifies that sorting selects the empty-state message or the sorted task view. */
    @Test
    public void sortCommand_emptyAndNonEmptyLists_expectedViewSelected() {
        RecordingUi ui = new RecordingUi();
        SortCommand command = new SortCommand();

        command.execute(new TaskList(), ui, createStorage());
        command.execute(new TaskList(List.of(new Todo("read"))), ui, createStorage());

        assertEquals(List.of("empty-sort", "sorted:read"), ui.calls);
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("sumo.txt"));
    }

    /** Records which output operation a command requests. */
    private static class RecordingUi extends Ui {
        private final List<String> calls = new ArrayList<>();

        @Override
        public void showGoodbye() {
            calls.add("goodbye");
        }

        @Override
        public void showTaskList(List<Task> tasks) {
            calls.add("list:" + getDescriptions(tasks));
        }

        @Override
        public void showMatchingTasks(List<Task> tasks) {
            calls.add("matches:" + getDescriptions(tasks));
        }

        @Override
        public void showTasksOnDate(java.time.LocalDateTime date, List<Task> tasks) {
            calls.add("on:" + date.toLocalDate() + ":" + getDescriptions(tasks));
        }

        @Override
        public void showSortedTaskList(List<Task> tasks) {
            calls.add("sorted:" + getDescriptions(tasks));
        }

        @Override
        public void showNoTasksToSort() {
            calls.add("empty-sort");
        }

        /** Returns task descriptions joined in display order for assertions. */
        private String getDescriptions(List<Task> tasks) {
            return String.join(",", tasks.stream().map(Task::getDescription).toList());
        }
    }
}
