package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.Todo;

class ScheduleCommandTest {
    private static final LocalDate SCHEDULE_DATE = LocalDate.of(2026, 8, 28);

    @Test
    void execute_matchingDeadlineAndEvent_matchingTasksKeepOriginalNumbers() {
        Task todo = new Todo("unscheduled");
        Task deadline = new Deadline("due today", SCHEDULE_DATE);
        Task event = new Event("ongoing event", SCHEDULE_DATE.minusDays(1),
                SCHEDULE_DATE.plusDays(1));
        Task laterDeadline = new Deadline("due later", SCHEDULE_DATE.plusDays(1));
        TaskList tasks = new TaskList(List.of(todo, deadline, event, laterDeadline));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new ScheduleCommand(SCHEDULE_DATE).execute(tasks, ui, storage);

        assertEquals(SCHEDULE_DATE, ui.getScheduleDate());
        assertEquals(List.of(2, 3), ui.getShownTaskNumbers());
        assertEquals(2, ui.getShownTasks().size());
        assertSame(deadline, ui.getShownTasks().get(0));
        assertSame(event, ui.getShownTasks().get(1));
        assertNull(ui.getEmptyScheduleDate());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_noScheduledTasks_emptyScheduleReported() {
        TaskList tasks = new TaskList(List.of(new Todo("unscheduled"),
                new Deadline("due later", SCHEDULE_DATE.plusDays(1))));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new ScheduleCommand(SCHEDULE_DATE).execute(tasks, ui, storage);

        assertNull(ui.getScheduleDate());
        assertEquals(SCHEDULE_DATE, ui.getEmptyScheduleDate());
        assertEquals(List.of(), ui.getShownTasks());
        assertEquals(0, storage.getSaveCount());
    }
}
