package nova.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {

    @Test
    public void add_newTask_increasesSize() {

        TaskList taskList = new TaskList();

        taskList.add(new ToDo("read book"));

        assertEquals(1, taskList.size());
    }

    @Test
    public void add_multipleTasks_preservesOrder() {

        TaskList taskList = new TaskList();

        taskList.add(new ToDo("read book"));
        taskList.add(new ToDo("return book"));

        assertEquals(
                "[T][ ] read book",
                taskList.get(0).toString()
        );

        assertEquals(
                "[T][ ] return book",
                taskList.get(1).toString()
        );
    }

    @Test
    public void delete_existingTask_removesCorrectTask() {

        TaskList taskList = new TaskList();

        taskList.add(new ToDo("read book"));
        taskList.add(new ToDo("return book"));

        Task removedTask = taskList.delete(0);

        assertEquals(
                "[T][ ] read book",
                removedTask.toString()
        );

        assertEquals(1, taskList.size());

        assertEquals(
                "[T][ ] return book",
                taskList.get(0).toString()
        );
    }

    @Test
    public void mark_existingTask_marksTaskDone() {

        TaskList taskList = new TaskList();

        taskList.add(new ToDo("read book"));

        taskList.mark(0);

        assertEquals(
                "[T][X] read book",
                taskList.get(0).toString()
        );
    }

    @Test
    public void unmark_doneTask_marksTaskNotDone() {

        TaskList taskList = new TaskList();

        taskList.add(new ToDo("read book"));

        taskList.mark(0);
        taskList.unmark(0);

        assertEquals(
                "[T][ ] read book",
                taskList.get(0).toString()
        );
    }
}