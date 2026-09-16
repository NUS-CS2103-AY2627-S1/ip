package eve.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import eve.TestUtil;
import eve.Ui;
import eve.task.Deadline;
import eve.task.TaskList;
import eve.task.ToDo;

/**
 * Tests {@link ScheduleCommand}: the whole chronological schedule when no
 * date is given, or just that date's tasks (matching {@link OnCommand})
 * when one is.
 */
public class ScheduleCommandTest {
    @Test
    public void execute_noDate_showsWholeScheduleInChronologicalOrder() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("just a todo"));
        tasks.add(new Deadline("later", LocalDate.of(2020, 6, 1)));
        tasks.add(new Deadline("earlier", LocalDate.of(2020, 1, 1)));

        String output = TestUtil.captureStdOut(() ->
                new ScheduleCommand(Optional.empty()).execute(tasks, new Ui(), null));

        assertTrue(output.indexOf("earlier") < output.indexOf("later"));
    }

    @Test
    public void execute_withDate_showsOnlyThatDatesTasks() throws Exception {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2020, 1, 1);
        tasks.add(new Deadline("on the day", date));
        tasks.add(new Deadline("day after", date.plusDays(1)));

        String output = TestUtil.captureStdOut(() ->
                new ScheduleCommand(Optional.of(date)).execute(tasks, new Ui(), null));

        assertTrue(output.contains("on the day"));
        assertFalse(output.contains("day after"));
        assertTrue(output.contains("Jan 1 2020"));
    }
}
