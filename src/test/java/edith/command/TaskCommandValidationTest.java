package edith.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import edith.exception.EdithException;
import edith.task.TaskList;
import edith.task.Todo;

/** Tests safety checks applied before task-mutating commands execute. */
public class TaskCommandValidationTest {
    @Test
    public void execute_emptyTaskList_reportsCommandSpecificError() {
        TaskList tasks = new TaskList();

        assertEquals("There are no tasks to mark. Add a task first.",
                assertThrows(EdithException.class, () -> new MarkCommand(1).execute(tasks, null)).getMessage());
        assertEquals("There are no tasks to unmark. Add a task first.",
                assertThrows(EdithException.class, () -> new UnmarkCommand(1).execute(tasks, null)).getMessage());
        assertEquals("There are no tasks to delete. Add a task first.",
                assertThrows(EdithException.class, () -> new DeleteCommand(1).execute(tasks, null)).getMessage());
        assertEquals("There are no tasks to tag. Add a task first.",
                assertThrows(EdithException.class, () -> new TagCommand(1, List.of("#fun"))
                        .execute(tasks, null)).getMessage());
        assertEquals("There are no tasks to untag. Add a task first.",
                assertThrows(EdithException.class, () -> new UntagCommand(1, List.of("#fun"))
                        .execute(tasks, null)).getMessage());
    }

    @Test
    public void execute_taskNumberOutsideList_reportsValidRange() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals("Please provide a task number from 1 to 1.",
                assertThrows(EdithException.class, () -> new MarkCommand(0).execute(tasks, null)).getMessage());
        assertEquals("Please provide a task number from 1 to 1.",
                assertThrows(EdithException.class, () -> new UnmarkCommand(2).execute(tasks, null)).getMessage());
        assertEquals("Please provide a task number from 1 to 1.",
                assertThrows(EdithException.class, () -> new DeleteCommand(-1).execute(tasks, null)).getMessage());
        assertEquals("Please provide a task number from 1 to 1.",
                assertThrows(EdithException.class, () -> new TagCommand(2, List.of("#fun"))
                        .execute(tasks, null)).getMessage());
        assertEquals("Please provide a task number from 1 to 1.",
                assertThrows(EdithException.class, () -> new UntagCommand(0, List.of("#fun"))
                        .execute(tasks, null)).getMessage());
    }

    @Test
    public void execute_saveFailure_restoresAddedTask() {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(new Todo("read book")) {
            @Override
            protected void saveTasks(TaskList changedTasks) throws EdithException {
                throw new EdithException("Save failed.");
            }
        };

        assertThrows(EdithException.class, () -> command.execute(tasks, null));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_saveFailure_restoresDeletedTaskAtOriginalPosition() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));
        DeleteCommand command = new DeleteCommand(1) {
            @Override
            protected void saveTasks(TaskList changedTasks) throws EdithException {
                throw new EdithException("Save failed.");
            }
        };

        assertThrows(EdithException.class, () -> command.execute(tasks, null));
        assertEquals(List.of("[T][ ] first", "[T][ ] second"),
                tasks.asList().stream().map(Object::toString).toList());
    }

    @Test
    public void execute_saveFailure_restoresStatusAndTagOrder() {
        Todo task = new Todo("read book");
        task.addTag("#first");
        task.addTag("#second");
        TaskList tasks = new TaskList(List.of(task));
        MarkCommand markCommand = new MarkCommand(1) {
            @Override
            protected void saveTasks(TaskList changedTasks) throws EdithException {
                throw new EdithException("Save failed.");
            }
        };
        UntagCommand untagCommand = new UntagCommand(1, List.of("#first")) {
            @Override
            protected void saveTasks(TaskList changedTasks) throws EdithException {
                throw new EdithException("Save failed.");
            }
        };

        assertThrows(EdithException.class, () -> markCommand.execute(tasks, null));
        assertThrows(EdithException.class, () -> untagCommand.execute(tasks, null));
        assertEquals("[T][ ] read book [tags: #first #second]", task.toString());
    }
}
