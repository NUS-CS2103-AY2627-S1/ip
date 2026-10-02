package ari.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ari.exception.TaskNotFoundException;

public class TaskListTest {
    @Test
    public void sortByDescription_mixedTypesAndCase_preservesEqualOrderAndState() {
        TaskList tasks = new TaskList();
        Task zebra = new TodoTask("zebra");
        Task apple = new DeadlineTask("Apple", "Sunday");
        Task equalApple = new EventTask("apple", "Monday", "Tuesday");
        apple.markTask();
        tasks.addTask(zebra);
        tasks.addTask(apple);
        tasks.addTask(equalApple);

        tasks.sortByDescription();
        tasks.sortByDescription();

        assertSame(apple, tasks.getTask(0));
        assertSame(equalApple, tasks.getTask(1));
        assertSame(zebra, tasks.getTask(2));
        assertEquals("[D][X] Apple (by: Sunday)", tasks.getTask(0).toString());
        assertEquals(3, tasks.getLength());
    }

    @Test
    public void sortByDescription_emptyAndSingleItem_succeeds() {
        TaskList tasks = new TaskList();
        tasks.sortByDescription();
        assertEquals(0, tasks.getLength());
        Task onlyTask = new TodoTask("only task");
        tasks.addTask(onlyTask);
        tasks.sortByDescription();
        assertSame(onlyTask, tasks.getTask(0));
    }

    @Test
    public void unmarkAndDelete_invalidIds_preserveList() {
        TaskList tasks = createListWithOneTask();
        int[] invalidIds = {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE};
        for (int taskId : invalidIds) {
            assertThrows(TaskNotFoundException.class, () -> tasks.unmarkTask(taskId));
            assertThrows(TaskNotFoundException.class, () -> tasks.deleteTask(taskId));
        }
        assertEquals(1, tasks.getLength());
        assertEquals("[T][ ] only task", tasks.getTask(0).toString());
    }

    @Test
    public void deleteTask_emptyList_preservesExistingResponse() throws TaskNotFoundException {
        assertEquals("None", new TaskList().deleteTask(1));
    }

    @Test
    public void markTask_validSecondId_marksSecondTaskOnly() throws TaskNotFoundException {
        TaskList tasks = new TaskList();
        tasks.addTask(new TodoTask("first"));
        tasks.addTask(new TodoTask("second"));

        String markedTask = tasks.markTask(2);

        assertEquals("[T][X] second", markedTask);
        assertEquals("[T][ ] first", tasks.getTask(0).toString());
        assertEquals("[T][X] second", tasks.getTask(1).toString());
    }

    @Test
    public void markTask_zeroId_throwsTaskNotFoundException() {
        TaskList tasks = createListWithOneTask();

        assertMarkTaskFails(tasks, 0);
    }

    @Test
    public void markTask_negativeId_throwsTaskNotFoundException() {
        TaskList tasks = createListWithOneTask();

        assertMarkTaskFails(tasks, -1);
    }

    @Test
    public void markTask_idBeyondListSize_throwsTaskNotFoundException() {
        TaskList tasks = createListWithOneTask();

        assertMarkTaskFails(tasks, 2);
    }

    @Test
    public void markTask_emptyList_throwsTaskNotFoundException() {
        TaskList tasks = new TaskList();

        assertMarkTaskFails(tasks, 1);
    }

    @Test
    public void findMatchingTasks_mixedCaseKeyword_returnsMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.addTask(new TodoTask("read book"));
        tasks.addTask(new DeadlineTask("return BOOK", "Sunday"));
        tasks.addTask(new TodoTask("write code"));

        TaskList matches = tasks.findMatchingTasks("Book");

        assertEquals(2, matches.getLength());
        assertEquals("[T][ ] read book", matches.getTask(0).toString());
        assertEquals("[D][ ] return BOOK (by: Sunday)", matches.getTask(1).toString());
    }

    @Test
    public void findMatchingTasks_noMatch_returnsEmptyMutableTaskList() {
        TaskList tasks = createListWithOneTask();

        TaskList matches = tasks.findMatchingTasks("missing");
        matches.addTask(new TodoTask("later addition"));

        assertEquals(1, matches.getLength());
        assertEquals("[T][ ] later addition", matches.getTask(0).toString());
    }

    private TaskList createListWithOneTask() {
        TaskList tasks = new TaskList();
        tasks.addTask(new TodoTask("only task"));
        return tasks;
    }

    private void assertMarkTaskFails(TaskList tasks, int id) {
        try {
            tasks.markTask(id);
            fail();
        } catch (TaskNotFoundException e) {
            String expectedMessage = String.format(
                    "Task %s does not exist!\nSend 'list' to see which tasks you have left!",
                    id
            );
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
