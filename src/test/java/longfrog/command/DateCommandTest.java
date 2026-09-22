package longfrog.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import longfrog.task.Deadline;
import longfrog.task.Event;
import longfrog.task.TaskList;
import longfrog.task.Todo;
import longfrog.ui.Ui;

class DateCommandTest {
    @Test
    void execute_tasksOccurOnDate_displaysOnlyMatchingDateBasedTasks() {
        LocalDate targetDate = LocalDate.of(2024, 1, 2);
        TaskList taskList = new TaskList(List.of(
                new Todo("undated"),
                new Deadline("submit", targetDate.atTime(9, 0)),
                new Event("camp", targetDate.minusDays(1).atTime(8, 0), targetDate.plusDays(1).atTime(17, 0)),
                new Deadline("later", targetDate.plusDays(1).atTime(9, 0))));
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new DateCommand(taskList, targetDate).execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertEquals(List.of(
                "Temporal query complete for 2/1/2024:",
                "1: [D][ ] submit (by: Jan 02 2024, 9:00 am)",
                "2: [E][ ] camp (from: Jan 01 2024, 8:00 am to: Jan 03 2024, 5:00 pm)"), messages);
    }

    @Test
    void execute_noTasksOccurOnDate_displaysNoMatchMessage() {
        List<String> messages = new ArrayList<>();

        boolean shouldExit = new DateCommand(new TaskList(), LocalDate.of(2024, 1, 2))
                .execute(new Ui(messages::add));

        assertFalse(shouldExit);
        assertEquals(List.of("Temporal query returned zero tasks for 2/1/2024."), messages);
    }
}
