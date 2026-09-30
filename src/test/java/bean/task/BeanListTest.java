package bean.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bean.exception.InvalidSyntaxException;

public class BeanListTest {

    @Test
    public void addDeadline_validDate_taskAddedSuccessfully() {
        BeanList beanList = new BeanList();

        // Action: Add a deadline with the correct yyyy-mm-dd format
        beanList.addDeadline("Finish CS2103T iP", "2026-08-28");

        // Assertion: The list size should increase to 1
        assertEquals(1, beanList.getSize());
    }

    @Test
    public void addDeadline_invalidDate_catchesExceptionAndIgnoresTask() {
        BeanList beanList = new BeanList();

        // Action: Add a deadline with an incorrect date format
        beanList.addDeadline("Finish CS2103T iP", "28-08-2026");

        // Assertion: The exception should be caught, and the list should remain empty
        assertEquals(0, beanList.getSize());
    }

    @Test
    public void markTask_invalidIndices_doesNotThrowException() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("Read textbook", Priority.LOW); // List size is now 1

        // Action & Assertion: Pass boundary-breaking indices and verify the program
        // silently returns instead of crashing with an IndexOutOfBoundsException
        assertEquals("", beanList.markTask(0)); // Zero index
        assertEquals("", beanList.markTask(-1)); // Negative index
        assertEquals("", beanList.markTask(5)); // Out of bounds index
    }

    @Test
    public void taskOperations_todoIsMarkedFoundAndDeleted() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("Read textbook", Priority.HIGH);

        assertEquals("0|0|2|Read textbook|", beanList.formatTask(0));
        assertEquals("Here are the tasks in your list:\n\n1. HIGH [T][ ] Read textbook",
                beanList.findTasks("textbook"));
        assertEquals("Good Job! I'll mark the task as done!\n\n [T][X] Read textbook",
                beanList.markTask(1));
        assertEquals("Alrighty! I've removed the following task:\n\nHIGH [T][X] Read textbook"
                + "\n\nNow you have 0 tasks in the list.", beanList.deleteTask(1));
        assertEquals(0, beanList.getSize());
    }

    @Test
    public void prioritySortedDisplay_actionsUseDisplayedTaskIndex() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("Low priority task", Priority.LOW);
        beanList.addTodoSilent("High priority task", Priority.HIGH);

        assertEquals("Good Job! I'll mark the task as done!\n\n [T][X] High priority task",
                beanList.markTask(1));
        assertEquals("Alrighty! I've removed the following task:\n\nLOW [T][ ] Low priority task"
                + "\n\nNow you have 1 tasks in the list.", beanList.deleteTask(2));
    }

    @Test
    public void addEvent_validDates_taskIsStoredWithDates() {
        BeanList beanList = new BeanList();

        beanList.addEvent("Project meeting", "2026-09-01", "2026-09-02");

        assertEquals(1, beanList.getSize());
        assertEquals("[E]", beanList.getTaskTag(1));
        assertEquals("Project meeting", beanList.getTaskName(1));
        assertEquals("2|0|0|Project meeting|2026-09-01|2026-09-02", beanList.formatTask(0));
    }

    @Test
    public void addTask_overloads_storeCorrectTypesAndPriorities() {
        BeanList beanList = new BeanList();

        beanList.addTodo("Plain todo");
        beanList.addDeadline("Deadline", "2026-08-28", Priority.HIGH);
        beanList.addEvent("Event", "2026-09-01", "2026-09-02", Priority.MEDIUM);

        assertEquals(3, beanList.getSize());
        assertEquals("1|0|2|Deadline|2026-08-28", beanList.formatTask(1));
        assertEquals("2|0|1|Event|2026-09-01|2026-09-02", beanList.formatTask(2));
        assertEquals("0|0|0|Plain todo|", beanList.formatTask(0));
    }

    @Test
    public void addTask_invalidDates_doNotChangeList() {
        BeanList beanList = new BeanList();

        assertTrue(beanList.addDeadline("Deadline", "2026/08/28").contains("wrong date format"));
        assertTrue(beanList.addEvent("Event", "2026-09-01", "tomorrow").contains("wrong date format"));
        assertTrue(beanList.addEvent("Event", "2026-10-10", "2026-10-01")
                .contains("cannot end before it starts"));
        assertEquals(0, beanList.getSize());
    }

    @Test
    public void displayTasks_sortsAllTaskTypesByPriority() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("Low todo", Priority.LOW);
        beanList.addEventSilent("Medium event", "2026-09-01", "2026-09-02", Priority.MEDIUM);
        beanList.addDeadlineSilent("High deadline", "2026-08-28", Priority.HIGH);

        assertEquals("Here are the tasks in your list:\n\n"
                + "1. HIGH [D][ ] High deadline (by: Aug 28 2026)\n"
                + "2. MEDIUM [E][ ] Medium event (from: Sep 01 2026 to: Sep 02 2026)\n"
                + "3. LOW [T][ ] Low todo", beanList.displayTasks());
        assertEquals("[D]", beanList.getTaskTag(1));
        assertEquals("High deadline", beanList.getTaskName(1));
    }

    @Test
    public void findTasks_matchesNamesOnlyAndHandlesNoMatch() {
        BeanList beanList = new BeanList();
        beanList.addDeadlineSilent("Submit report", "2026-08-28", Priority.LOW);

        assertTrue(beanList.findTasks("REPORT").contains("Submit report"));
        assertEquals("No matching tasks found.", beanList.findTasks("2026"));
    }

    @Test
    public void findTasks_preservesIndicesFromCompleteDisplayedList() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("Low report", Priority.LOW);
        beanList.addTodoSilent("High task", Priority.HIGH);

        assertEquals("Here are the tasks in your list:\n\n2. LOW [T][ ] Low report",
                beanList.findTasks("report"));
        assertTrue(beanList.markTask(2).contains("Low report"));
    }

    @Test
    public void markAndUnmarkTask_updateDisplayedTaskStatus() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("Finish assignment", Priority.MEDIUM);

        assertTrue(beanList.markTask(1).contains("[X] Finish assignment"));
        assertTrue(beanList.displayTasks().contains("[X] Finish assignment"));
        assertTrue(beanList.unmarkTask(1).contains("[ ] Finish assignment"));
        assertTrue(beanList.displayTasks().contains("[ ] Finish assignment"));
    }

    @Test
    public void markLastAddedTask_marksUnderlyingLatestTask() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("First high", Priority.HIGH);
        beanList.addTodoSilent("Second low", Priority.LOW);

        beanList.markLastAddedTask();

        assertTrue(beanList.displayTasks().contains("HIGH [T][ ] First high"));
        assertTrue(beanList.displayTasks().contains("LOW [T][X] Second low"));
    }

    @Test
    public void taskOperations_invalidIndices_throwExpectedExceptions() {
        BeanList beanList = new BeanList();
        beanList.addTodoSilent("Read textbook", Priority.LOW);

        assertThrows(IllegalArgumentException.class, () -> beanList.getTaskTag(0));
        assertThrows(IllegalArgumentException.class, () -> beanList.getTaskName(2));
        assertThrows(IllegalArgumentException.class, () -> beanList.unmarkTask(0));
        assertThrows(IllegalArgumentException.class, () -> beanList.deleteTask(2));
        assertThrows(IllegalArgumentException.class, () -> beanList.formatTask(-1));
        assertThrows(IllegalArgumentException.class, () -> beanList.formatTask(1));
    }

    @Test
    public void priority_fromString_parsesValidValuesAndRejectsInvalidValue() {
        assertEquals(Priority.HIGH, Priority.fromString("HiGh"));
        assertEquals(Priority.MEDIUM, Priority.fromString("medium"));
        assertEquals(Priority.LOW, Priority.fromString("LOW"));
        assertEquals(2, Priority.HIGH.getLevel());
        assertThrows(InvalidSyntaxException.class, () -> Priority.fromString("urgent"));
    }
}
