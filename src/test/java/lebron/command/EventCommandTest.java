package lebron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.exception.LeBronException;
import lebron.task.TaskList;
import lebron.ui.Ui;

public class EventCommandTest {
    @Test
    public void execute_validDescriptionAndDates_taskAddedToListAndReportedToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Command command = new EventCommand("project meeting",
                LocalDateTime.of(2019, 10, 16, 9, 0), LocalDateTime.of(2019, 10, 16, 11, 0));

        String response = command.execute(taskList, new Ui());

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[E][ ] project meeting (from Oct 16 2019, 9:00 AM til Oct 16 2019, 11:00 AM)"));
        assertTrue(response.contains("1 tasks left to grind now!"));
    }

    @Test
    public void execute_duplicateEvent_exceptionThrownAndListUnchanged() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        LocalDateTime start = LocalDateTime.of(2019, 10, 16, 9, 0);
        LocalDateTime end = LocalDateTime.of(2019, 10, 16, 11, 0);
        new EventCommand("project meeting", start, end).execute(taskList, new Ui());
        Command duplicate = new EventCommand("project meeting", start, end);

        assertThrows(LeBronException.class, () -> duplicate.execute(taskList, new Ui()));
        assertEquals(1, taskList.size());
    }
}
