package gunna.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Deadline;
import gunna.task.Event;
import gunna.task.Task;
import gunna.task.Todo;

/**
 * JUnit test class for testing the SortCommand class.
 * Tests cover all sorting criteria (status, description, date),
 * edge cases, and verification of immutability.
 */
public class SortCommandTest {
    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage("data/test_command_tasks.txt");
    }

    @Test
    public void execute_sortByStatus_notDoneTasksFirst() throws DukeException {
        // Add tasks and mark some as done
        Todo task1 = new Todo("task1");
        Todo task2 = new Todo("task2");
        Todo task3 = new Todo("task3");
        task1.markAsDone();
        task3.markAsDone();

        tasks.add(task1); // done
        tasks.add(task2); // not done
        tasks.add(task3); // done

        SortCommand command = new SortCommand("status");
        String result = command.execute(tasks, ui, storage);

        // Verify not-done task appears first in result
        assertTrue(result.contains("1.[T][ ] task2"));
        assertTrue(result.contains("2.[T][X] task1"));
        assertTrue(result.contains("3.[T][X] task3"));
    }

    @Test
    public void execute_sortByDescription_alphabeticalOrder() throws DukeException {
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("mango"));

        SortCommand command = new SortCommand("description");
        String result = command.execute(tasks, ui, storage);

        // Verify alphabetical order
        assertTrue(result.contains("1.[T][ ] apple"));
        assertTrue(result.contains("2.[T][ ] mango"));
        assertTrue(result.contains("3.[T][ ] zebra"));
    }

    @Test
    public void execute_sortByDescriptionCaseInsensitive_correctOrder() throws DukeException {
        tasks.add(new Todo("Zebra"));
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("MANGO"));

        SortCommand command = new SortCommand("description");
        String result = command.execute(tasks, ui, storage);

        // Verify case-insensitive alphabetical order
        assertTrue(result.contains("1.[T][ ] apple"));
        assertTrue(result.contains("2.[T][ ] MANGO"));
        assertTrue(result.contains("3.[T][ ] Zebra"));
    }

    @Test
    public void execute_sortByDate_deadlinesFirst() throws DukeException {
        tasks.add(new Todo("todo task"));
        tasks.add(new Deadline("late deadline", LocalDate.of(2024, 12, 31)));
        tasks.add(new Event("event task", "2pm", "4pm"));
        tasks.add(new Deadline("early deadline", LocalDate.of(2024, 1, 15)));

        SortCommand command = new SortCommand("date");
        String result = command.execute(tasks, ui, storage);

        // Verify deadlines come first, sorted by date (oldest first)
        assertTrue(result.contains("1.[D][ ] early deadline"));
        assertTrue(result.contains("2.[D][ ] late deadline"));
        // Todo and Event should appear after deadlines
        assertTrue(result.contains("3.[T][ ] todo task"));
        assertTrue(result.contains("4.[E][ ] event task"));
    }

    @Test
    public void execute_sortByDateWithOnlyDeadlines_oldestFirst() throws DukeException {
        tasks.add(new Deadline("newest", LocalDate.of(2025, 6, 15)));
        tasks.add(new Deadline("oldest", LocalDate.of(2024, 1, 1)));
        tasks.add(new Deadline("middle", LocalDate.of(2024, 12, 31)));

        SortCommand command = new SortCommand("date");
        String result = command.execute(tasks, ui, storage);

        // Verify oldest deadline appears first
        assertTrue(result.contains("1.[D][ ] oldest"));
        assertTrue(result.contains("2.[D][ ] middle"));
        assertTrue(result.contains("3.[D][ ] newest"));
    }

    @Test
    public void execute_emptyTaskList_executesSuccessfully() throws DukeException {
        SortCommand command = new SortCommand("status");
        String result = command.execute(tasks, ui, storage);

        // Should execute successfully and indicate no tasks
        assertTrue(result.contains("You have no tasks in your list"));
    }

    @Test
    public void execute_singleTask_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("single task"));

        SortCommand command = new SortCommand("description");
        String result = command.execute(tasks, ui, storage);

        // Should execute successfully
        assertTrue(result.contains("1.[T][ ] single task"));
    }

    @Test
    public void execute_sortDoesNotModifyOriginalList() throws DukeException {
        // Add tasks in specific order
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("mango"));

        // Store original order
        ArrayList<Task> originalTasks = new ArrayList<>(tasks.getTasks());

        // Execute sort command
        SortCommand command = new SortCommand("description");
        command.execute(tasks, ui, storage);

        // Verify original task list is unchanged
        assertEquals(originalTasks.get(0).getDescription(), tasks.get(0).getDescription());
        assertEquals(originalTasks.get(1).getDescription(), tasks.get(1).getDescription());
        assertEquals(originalTasks.get(2).getDescription(), tasks.get(2).getDescription());
        assertEquals("zebra", tasks.get(0).getDescription(), "Original order should be preserved");
        assertEquals("apple", tasks.get(1).getDescription(), "Original order should be preserved");
        assertEquals("mango", tasks.get(2).getDescription(), "Original order should be preserved");
    }

    @Test
    public void execute_stableSortMaintainsOrder() throws DukeException {
        // Add tasks with same description to test stable sort
        Todo task1 = new Todo("same");
        Todo task2 = new Todo("same");
        task1.markAsDone();

        tasks.add(task1); // done
        tasks.add(task2); // not done

        SortCommand command = new SortCommand("description");
        String result = command.execute(tasks, ui, storage);

        // With stable sort, original order should be maintained for equal elements
        // First occurrence should still be task1 (done), second should be task2 (not done)
        String[] lines = result.split("\n");
        assertTrue(lines[1].contains("[X]"), "First task should be the originally first one (done)");
        assertTrue(lines[2].contains("[ ]"), "Second task should be the originally second one (not done)");
    }

    @Test
    public void execute_sortByDateWithNoDeadlines_maintainsOrder() throws DukeException {
        tasks.add(new Todo("first todo"));
        tasks.add(new Event("event", "2pm", "4pm"));
        tasks.add(new Todo("second todo"));

        SortCommand command = new SortCommand("date");
        String result = command.execute(tasks, ui, storage);

        // Since no deadlines, original relative order should be maintained
        assertTrue(result.contains("1.[T][ ] first todo"));
        assertTrue(result.contains("2.[E][ ] event"));
        assertTrue(result.contains("3.[T][ ] second todo"));
    }

    @Test
    public void execute_allCriteria_executesSuccessfully() throws DukeException {
        tasks.add(new Todo("test task"));

        // Test all three criteria
        SortCommand statusCommand = new SortCommand("status");
        statusCommand.execute(tasks, ui, storage);

        SortCommand descCommand = new SortCommand("description");
        descCommand.execute(tasks, ui, storage);

        SortCommand dateCommand = new SortCommand("date");
        dateCommand.execute(tasks, ui, storage);

        // If no exception thrown, all criteria work correctly
        assertTrue(true, "All sort criteria should execute successfully");
    }
}
