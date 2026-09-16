package puyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.Deadline;
import puyo.task.Event;
import puyo.task.TaskList;
import puyo.task.ToDo;
import puyo.ui.Ui;

/**
 * Tests daily schedule filtering and its displayed results.
 * Written with assistance from ChatGPT.
 */
public class ViewScheduleCommandTest {

    private TaskList tasks;
    private RecordingUi ui;
    private Storage storage;

    /**
     * Records messages without changing the global console output stream.
     */
    private static class RecordingUi extends Ui {
        private String message;

        @Override
        public void showMessage(String message) {
            this.message = message;
        }
    }

    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        tasks = new TaskList();
        ui = new RecordingUi();
        storage = new Storage(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void execute_matchingDeadline_includesActualDueDate() {
        Deadline deadline = new Deadline("Submit assignment", LocalDateTime.of(2026, 9, 3, 18, 45));
        tasks.add(deadline);

        new ViewScheduleCommand(LocalDate.of(2026, 9, 3)).execute(tasks, ui, storage);

        assertEquals("Here are the schedules for 2026-09-03:\n1. " + deadline, ui.message);
    }

    @Test
    public void execute_dateInDescriptions_ignoresUnrelatedDates() throws PuyoException {
        tasks.add(new Deadline("Discuss Sep 03 2026", LocalDateTime.of(2026, 9, 4, 10, 0)));
        tasks.add(new Event("Review Sep 03 2026", LocalDateTime.of(2026, 9, 4, 11, 0),
                LocalDateTime.of(2026, 9, 5, 12, 0)));

        new ViewScheduleCommand(LocalDate.of(2026, 9, 3)).execute(tasks, ui, storage);

        assertEquals("No schedules found for date: 2026-09-03", ui.message);
    }

    @Test
    public void execute_dayAppearsInTime_ignoresUnrelatedDate() {
        tasks.add(new Deadline("Early meeting", LocalDateTime.of(2026, 9, 13, 3, 0)));

        new ViewScheduleCommand(LocalDate.of(2026, 9, 3)).execute(tasks, ui, storage);

        assertEquals("No schedules found for date: 2026-09-03", ui.message);
    }

    @Test
    public void execute_septemberTask_ignoresOtherMonths() {
        tasks.add(new Deadline("Submit report", LocalDateTime.of(2026, 9, 3, 18, 0)));

        new ViewScheduleCommand(LocalDate.of(2026, 10, 3)).execute(tasks, ui, storage);

        assertEquals("No schedules found for date: 2026-10-03", ui.message);
    }

    @Test
    public void execute_multiDayEvent_includesStartMiddleAndMidnightEnd() throws PuyoException {
        Event event = new Event("Conference", LocalDateTime.of(2026, 9, 2, 23, 0),
                LocalDateTime.of(2026, 9, 4, 0, 0));
        tasks.add(event);

        for (int day = 2; day <= 4; day++) {
            LocalDate date = LocalDate.of(2026, 9, day);
            new ViewScheduleCommand(date).execute(tasks, ui, storage);

            assertEquals("Here are the schedules for " + date + ":\n1. " + event, ui.message);
        }
    }

    @Test
    public void execute_outsideEventDates_excludesEvent() throws PuyoException {
        tasks.add(new Event("Conference", LocalDateTime.of(2026, 9, 2, 23, 0),
                LocalDateTime.of(2026, 9, 4, 0, 0)));

        new ViewScheduleCommand(LocalDate.of(2026, 9, 1)).execute(tasks, ui, storage);
        assertEquals("No schedules found for date: 2026-09-01", ui.message);

        new ViewScheduleCommand(LocalDate.of(2026, 9, 5)).execute(tasks, ui, storage);
        assertEquals("No schedules found for date: 2026-09-05", ui.message);
    }

    @Test
    public void execute_todosOnly_reportsNoSchedules() {
        tasks.add(new ToDo("Review Sep 03 2026 notes"));
        tasks.add(new ToDo("Buy groceries"));

        new ViewScheduleCommand(LocalDate.of(2026, 9, 3)).execute(tasks, ui, storage);

        assertEquals("No schedules found for date: 2026-09-03", ui.message);
    }

    @Test
    public void execute_emptyTaskList_reportsNoSchedules() {
        new ViewScheduleCommand(LocalDate.of(2026, 9, 3)).execute(tasks, ui, storage);

        assertEquals("No schedules found for date: 2026-09-03", ui.message);
    }

    @Test
    public void execute_multipleMatches_preservesOrderAndNumbersResults() throws PuyoException {
        Deadline deadline = new Deadline("Submit assignment", LocalDateTime.of(2026, 9, 3, 18, 0));
        deadline.markDone();
        Event event = new Event("Workshop", LocalDateTime.of(2026, 9, 3, 10, 0),
                LocalDateTime.of(2026, 9, 3, 12, 0));
        tasks.add(new ToDo("Buy groceries"));
        tasks.add(deadline);
        tasks.add(new Deadline("Later task", LocalDateTime.of(2026, 9, 5, 10, 0)));
        tasks.add(event);

        new ViewScheduleCommand(LocalDate.of(2026, 9, 3)).execute(tasks, ui, storage);

        assertEquals("Here are the schedules for 2026-09-03:\n1. " + deadline + "\n2. " + event, ui.message);
    }
}
