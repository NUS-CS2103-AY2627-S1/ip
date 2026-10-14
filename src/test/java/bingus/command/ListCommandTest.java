package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests the read-only behaviour of {@link ListCommand}.
 */
class ListCommandTest {

    /**
     * Verifies that an unfiltered list command returns every task without changing task order.
     */
    @Test
    void execute_withoutDate_returnsEveryTaskWithoutChangingTaskList() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 18, 0));
        TaskList tasks = new TaskList(List.of(todo, deadline));
        ListCommand command = new ListCommand();

        String response = command.execute(tasks, new Ui(), null);

        assertEquals(CommandType.LIST, command.getType());
        assertEquals("Here are the tasks I am watching:\n"
                + "1. [T][○] read book\n"
                + "2. [D][○] submit report (by: Sep 20 2026, 6:00 PM)", response);
        assertEquals(List.of(todo, deadline), tasks.getTasksSnapshot());
    }

    /**
     * Verifies that a dated list command includes matching deadlines and multi-day events only.
     */
    @Test
    void execute_withDate_returnsOnlyTasksAssociatedWithDate() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 18, 0));
        Event event = new Event("camp", LocalDateTime.of(2026, 9, 19, 9, 0),
                LocalDateTime.of(2026, 9, 21, 18, 0));
        TaskList tasks = new TaskList(List.of(new Todo("read book"), deadline, event));
        ListCommand command = new ListCommand(date);

        String response = command.execute(tasks, new Ui(), null);

        assertEquals("Here are the tasks I am watching on Sep 20 2026:\n"
                + "2. [D][○] submit report (by: Sep 20 2026, 6:00 PM)\n"
                + "3. [E][○] camp (from: Sep 19 2026, 9:00 AM to: Sep 21 2026, 6:00 PM)", response);
    }
}
