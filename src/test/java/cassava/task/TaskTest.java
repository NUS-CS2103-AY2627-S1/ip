package cassava.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void testToString_incompleteNoPriority() {
        Task task = new ToDoTask("water the plants");
        assertEquals("[T] [ ] water the plants", task.toString());
    }

    @Test
    public void testToString_completeNoPriority() {
        Task task = new ToDoTask("water the plants");
        task.mark();
        assertEquals("[T] [X] water the plants", task.toString());
    }

    @Test
    public void testToString_withPriority() {
        Task task = new ToDoTask("water the plants");
        task.setTaskPriority(Task.TaskPriority.HIGH);
        assertEquals("[T] [ ] water the plants [high]", task.toString());
    }

    @Test
    public void testToDataString_noPriority() {
        Task task = new ToDoTask("water the plants");
        assertEquals("T | 0 | water the plants | unknown", task.toDataString());
    }

    @Test
    public void testToDataString_completedWithPriority() {
        Task task = new ToDoTask("water the plants");
        task.mark();
        task.setTaskPriority(Task.TaskPriority.LOW);
        assertEquals("T | 1 | water the plants | low", task.toDataString());
    }

    @Test
    public void testEquals_sameDataString_true() {
        Task task1 = new ToDoTask("water the plants");
        Task task2 = new ToDoTask("water the plants");
        assertTrue(task1.equals(task2));
    }

    @Test
    public void testEquals_differentDescription_false() {
        Task task1 = new ToDoTask("water the plants");
        Task task2 = new ToDoTask("water the flowers");
        assertFalse(task1.equals(task2));
    }

    @Test
    public void testEquals_differentCompletedState_false() {
        Task task1 = new ToDoTask("water the plants");
        Task task2 = new ToDoTask("water the plants");
        task2.mark();
        assertFalse(task1.equals(task2));
    }

    @Test
    public void testEquals_differentPriority_false() {
        Task task1 = new ToDoTask("water the plants");
        Task task2 = new ToDoTask("water the plants");
        task2.setTaskPriority(Task.TaskPriority.HIGH);
        assertFalse(task1.equals(task2));
    }

    @Test
    public void testMarkAndUnmark() {
        Task task = new ToDoTask("water the plants");
        assertFalse(task.toString().contains("[X]"));

        task.mark();
        assertTrue(task.toString().contains("[X]"));

        task.unmark();
        assertTrue(task.toString().contains("[ ]"));
    }

    @Test
    public void testGetDescription() {
        Task task = new ToDoTask("water the plants");
        assertEquals("water the plants", task.getDescription());
    }

    @Test
    public void testStringToTaskPriority_validLevels() {
        assertEquals(Task.TaskPriority.HIGH, Task.stringToTaskPriority("high"));
        assertEquals(Task.TaskPriority.MEDIUM, Task.stringToTaskPriority("medium"));
        assertEquals(Task.TaskPriority.LOW, Task.stringToTaskPriority("low"));
    }

    @Test
    public void testStringToTaskPriority_unrecognised_returnsUnknown() {
        assertEquals(Task.TaskPriority.UNKNOWN, Task.stringToTaskPriority("urgent"));
    }

    @Test
    public void testTaskPriorityToString_allLevels() {
        assertEquals("high", Task.taskPriorityToString(Task.TaskPriority.HIGH));
        assertEquals("medium", Task.taskPriorityToString(Task.TaskPriority.MEDIUM));
        assertEquals("low", Task.taskPriorityToString(Task.TaskPriority.LOW));
        assertEquals("unknown", Task.taskPriorityToString(Task.TaskPriority.UNKNOWN));
    }

}
