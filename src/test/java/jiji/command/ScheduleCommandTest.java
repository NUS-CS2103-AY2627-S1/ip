package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jiji.task.Deadline;
import jiji.task.Event;
import jiji.task.TaskList;
import jiji.task.Todo;
import jiji.ui.Ui;

public class ScheduleCommandTest {

    private Ui ui;
    private TaskList tasks;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
        tasks = new TaskList();
    }

    @Test
    public void execute_noTasksScheduled_returnsEmptyScheduleMessage() {
        LocalDate date = LocalDate.of(2026, 8, 30);
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2026-09-01"));

        ScheduleCommand command = new ScheduleCommand(date);
        String result = command.execute(tasks, ui, null);

        assertEquals("No tasks scheduled for Aug 30 2026. A purr-fectly peaceful day to rest! ₍^ ᵕ ᵕ ^₎",
                result);
    }

    @Test
    public void execute_matchingDeadlinesAndEvents_preservesMasterIndices() {
        LocalDate date = LocalDate.of(2026, 8, 30);
        // index 1
        tasks.add(new Todo("read book"));
        // index 2: matching deadline
        tasks.add(new Deadline("return book", "2026-08-30"));
        // index 3: non-matching deadline
        tasks.add(new Deadline("pay bills", "2026-09-15"));
        // index 4: matching event (spans across Aug 30)
        tasks.add(new Event("coding bootcamp", "2026-08-28 0900", "2026-08-31 1800"));
        // index 5: matching single-day event on Aug 30
        tasks.add(new Event("hackathon", "2026-08-30 1000", "2026-08-30 2000"));
        // index 6: non-matching event
        tasks.add(new Event("orientation camp", "2026-09-01 0900", "2026-09-03 1700"));

        ScheduleCommand command = new ScheduleCommand(date);
        String result = command.execute(tasks, ui, null);

        assertTrue(result.startsWith("Schedule for Aug 30 2026:"));
        assertTrue(result.contains("2.[D][ ] return book (by: Aug 30 2026)"));
        assertTrue(result.contains("4.[E][ ] coding bootcamp (from: Aug 28 2026, 9:00AM to: Aug 31 2026, 6:00PM)"));
        assertTrue(result.contains("5.[E][ ] hackathon (from: Aug 30 2026, 10:00AM to: Aug 30 2026, 8:00PM)"));
        // Ensure non-matching tasks are not present
        assertTrue(!result.contains("1.[T]"));
        assertTrue(!result.contains("pay bills"));
        assertTrue(!result.contains("orientation camp"));
    }

    @Test
    public void getTargetDate_returnsCorrectDate() {
        LocalDate date = LocalDate.of(2026, 12, 25);
        ScheduleCommand command = new ScheduleCommand(date);
        assertEquals(date, command.getTargetDate());
    }
}
