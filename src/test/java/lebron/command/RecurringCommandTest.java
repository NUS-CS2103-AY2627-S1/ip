package lebron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.exception.LeBronException;
import lebron.task.RecurrenceInterval;
import lebron.task.TaskList;
import lebron.ui.Ui;

public class RecurringCommandTest {
    @Test
    public void execute_validArgs_taskAddedToListAndReportedToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Command command = new RecurringCommand("project meeting",
                LocalDateTime.of(2019, 10, 15, 18, 0), RecurrenceInterval.WEEKLY);

        String response = command.execute(taskList, new Ui());

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[R][ ] project meeting (every week, next due: Oct 15 2019, 6:00 PM)"));
        assertTrue(response.contains("1 tasks left to grind now!"));
    }

    @Test
    public void execute_duplicateRecurringTask_exceptionThrownAndListUnchanged() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        LocalDateTime nextDue = LocalDateTime.of(2019, 10, 15, 18, 0);
        new RecurringCommand("project meeting", nextDue, RecurrenceInterval.WEEKLY).execute(taskList, new Ui());
        Command duplicate = new RecurringCommand("project meeting", nextDue, RecurrenceInterval.WEEKLY);

        assertThrows(LeBronException.class, () -> duplicate.execute(taskList, new Ui()));
        assertEquals(1, taskList.size());
    }
}
