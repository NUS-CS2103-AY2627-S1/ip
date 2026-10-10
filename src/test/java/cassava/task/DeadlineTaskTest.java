package cassava.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTaskTest {

    @Test
    public void testGetTaskTypeToken() {
        DeadlineTask task = new DeadlineTask("finish assignment", LocalDate.parse("2026-08-28"));
        assertEquals("D", task.getTaskTypeToken());
    }

    @Test
    public void testConstructor_plain_toString() {
        DeadlineTask task = new DeadlineTask("finish assignment", LocalDate.parse("2026-08-28"));
        assertEquals("[D] [ ] finish assignment (by: Aug 28 2026)", task.toString());
    }

    @Test
    public void testConstructor_fullArgs_toString() {
        DeadlineTask task = new DeadlineTask(
                "finish assignment", true, Task.TaskPriority.HIGH, LocalDate.parse("2026-08-28"));
        assertEquals("[D] [X] finish assignment [high] (by: Aug 28 2026)", task.toString());
    }

    @Test
    public void testToDataString() {
        DeadlineTask task = new DeadlineTask(
                "finish assignment", false, Task.TaskPriority.UNKNOWN, LocalDate.parse("2026-08-28"));
        assertEquals("D | 0 | finish assignment | unknown | 2026-08-28", task.toDataString());
    }

    @Test
    public void testToDataString_completedWithPriority() {
        DeadlineTask task = new DeadlineTask(
                "finish assignment", true, Task.TaskPriority.LOW, LocalDate.parse("2026-01-05"));
        assertEquals("D | 1 | finish assignment | low | 2026-01-05", task.toDataString());
    }

}
