package jarvis.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jarvis.classes.Deadline;
import jarvis.classes.Event;
import jarvis.classes.Task;
import jarvis.classes.ToDo;
import jarvis.exceptions.InvalidTaskNumberException;
import jarvis.exceptions.JarvisException;
import jarvis.exceptions.ScheduleConflictException;
import jarvis.storage.Storage;

/** Tests task-list operations. */
public class TaskListTest {
    /** Temporary directory used for test storage. */
    @TempDir
    Path tempDir;

    /** Task list used by each test. */
    private TaskList taskList;

    /**
     * Creates a task list using a temporary storage file before each test.
     */
    @BeforeEach
    public void setUp() {
        Path dataFile = tempDir.resolve("jarvis.txt");
        Storage storage = new Storage(dataFile.toString());
        taskList = new TaskList(Storage.loadTasks());
    }

    /** Verifies that tasks can be added, retrieved, and counted. */
    @Test
    public void addTask_taskIsStored() throws JarvisException {
        TaskList taskList = new TaskList();

        ToDo todoTask = new ToDo("Read book");
        taskList.addTask(todoTask);

        assertEquals(1, taskList.size());

        assertEquals(todoTask, taskList.getTask(0));
        assertEquals(todoTask, taskList.getTasks().get(0));

        Deadline deadlineTask = new Deadline("Return book", LocalDateTime.of(2026, 8, 22, 23, 45));
        taskList.addTask(deadlineTask);

        assertEquals(2, taskList.size());

        assertEquals(deadlineTask, taskList.getTask(1));
        assertEquals(deadlineTask, taskList.getTasks().get(1));
    }

    /** Verifies that a non-conflicting event can be added successfully. */
    @Test
    public void addTask_nonConflictingEventIsStored() throws JarvisException {
        TaskList taskList = new TaskList();

        Event firstEvent = new Event(
                "Meeting",
                LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 22, 10, 0)
        );

        Event secondEvent = new Event(
                "Lunch",
                LocalDateTime.of(2026, 8, 22, 11, 0),
                LocalDateTime.of(2026, 8, 22, 12, 0)
        );

        taskList.addTask(firstEvent);
        taskList.addTask(secondEvent);

        assertEquals(2, taskList.size());
        assertEquals(firstEvent, taskList.getTask(0));
        assertEquals(secondEvent, taskList.getTask(1));

    }

    /** Verifies that adding an overlapping event throws a schedule conflict exception. */
    @Test
    public void addTask_conflictingEvent_throwsScheduleConflictException()
            throws JarvisException {
        TaskList taskList = new TaskList();

        Event existingEvent = new Event(
                "Meeting",
                LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 22, 10, 0)
        );

        Event conflictingEvent = new Event(
                "Project Discussion",
                LocalDateTime.of(2026, 8, 22, 9, 30),
                LocalDateTime.of(2026, 8, 22, 10, 30)
        );

        taskList.addTask(existingEvent);

        assertThrows(
                ScheduleConflictException.class, () ->
                        taskList.addTask(conflictingEvent)
        );
    }

    /** Verifies that a conflicting event is not added to the task list. */
    @Test
    public void addTask_conflictingEventIsNotStored() throws JarvisException {
        TaskList taskList = new TaskList();

        Event existingEvent = new Event(
                "Meeting",
                LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 22, 10, 0)
        );

        Event conflictingEvent = new Event(
                "Project Discussion",
                LocalDateTime.of(2026, 8, 22, 9, 30),
                LocalDateTime.of(2026, 8, 22, 10, 30)
        );

        taskList.addTask(existingEvent);

        assertThrows(
                ScheduleConflictException.class, () ->
                        taskList.addTask(conflictingEvent)
        );

        assertEquals(1, taskList.size());
        assertEquals(existingEvent, taskList.getTask(0));

    }

    /** Verifies that an event ending when another event starts is allowed. */
    @Test
    public void addTask_eventsTouchingAtBoundaryAreStored() throws JarvisException {
        TaskList taskList = new TaskList();

        Event firstEvent = new Event(
                "Meeting",
                LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 22, 10, 0)
        );

        Event secondEvent = new Event(
                "Lunch",
                LocalDateTime.of(2026, 8, 22, 10, 0),
                LocalDateTime.of(2026, 8, 22, 11, 0)
        );

        taskList.addTask(firstEvent);
        taskList.addTask(secondEvent);

        assertEquals(2, taskList.size());
    }

    /** Verifies that deleting a task returns it and removes it from the list. */
    @Test
    public void deleteTask_taskIsRemoved() throws JarvisException {
        TaskList taskList = new TaskList();

        ToDo todoTask = new ToDo("Read book");
        Deadline deadlineTask = new Deadline("Return book",
                LocalDateTime.of(2026, 8, 22, 23, 45));

        taskList.addTask(todoTask);
        taskList.addTask(deadlineTask);

        taskList.deleteTask(0);
        assertEquals(1, taskList.size());

        assertEquals(deadlineTask, taskList.getTask(0));
        assertEquals(deadlineTask, taskList.getTasks().get(0));

        taskList.deleteTask(0);
        assertEquals(0, taskList.size());
    }


    /** Verifies that completion status updates are applied to the selected task. */
    @Test
    public void setCompletionStatus_taskStatusIsUpdated() throws JarvisException {
        TaskList taskList = new TaskList();

        ToDo todoTask = new ToDo("Read book");
        Deadline deadlineTask = new Deadline("Return book", LocalDateTime.of(2026, 8, 22, 23, 45));

        taskList.addTask(todoTask);
        taskList.addTask(deadlineTask);

        taskList.setCompletionStatus(0, Task.CompletionStatus.DONE);
        taskList.setCompletionStatus(1, Task.CompletionStatus.DONE);

        assertEquals(Task.CompletionStatus.DONE, taskList.getTask(0).getStatus());
        assertEquals(Task.CompletionStatus.DONE, taskList.getTask(1).getStatus());

        taskList.setCompletionStatus(0, Task.CompletionStatus.UNDONE);
        taskList.setCompletionStatus(1, Task.CompletionStatus.UNDONE);

        assertEquals(Task.CompletionStatus.UNDONE, taskList.getTask(0).getStatus());
        assertEquals(Task.CompletionStatus.UNDONE, taskList.getTask(1).getStatus());
    }

    /** Verifies that tasks are filtered correctly based on key word. */
    @Test
    public void filterTasks_tasksAreFilteredCorrectly() throws JarvisException {
        TaskList taskList = new TaskList();
        List<Task> testCase1TaskList = new ArrayList<>();
        List<Task> testCase2TaskList = new ArrayList<>();
        List<Task> testCase3TaskList = new ArrayList<>();
        List<Task> testCase4TaskList = new ArrayList<>();

        ToDo todoTask1 = new ToDo("Read book");
        Deadline deadlineTask = new Deadline("Do Coding", LocalDateTime.of(2026, 8, 22, 23, 45));
        ToDo todoTask2 = new ToDo("Check Code");

        taskList.addTask(todoTask1);
        taskList.addTask(deadlineTask);
        taskList.addTask(todoTask2);

        testCase1TaskList.add(deadlineTask);
        testCase2TaskList.add(todoTask1);

        assertEquals(testCase1TaskList, taskList.filterTasks("do"));
        assertEquals(testCase1TaskList, taskList.filterTasks("coding"));

        testCase1TaskList.add(todoTask2);

        assertEquals(testCase1TaskList, taskList.filterTasks("cod"));

        assertEquals(testCase2TaskList, taskList.filterTasks("book"));
        assertEquals(testCase2TaskList, taskList.filterTasks("boo"));

        Event eventTask = new Event("Return book",
                LocalDateTime.of(2026, 8, 21, 9, 45),
                LocalDateTime.of(2026, 8, 22, 16, 45));

        taskList.addTask(eventTask);

        testCase3TaskList.add(todoTask1);
        testCase3TaskList.add(eventTask);

        testCase4TaskList.add(eventTask);

        assertEquals(testCase3TaskList, taskList.filterTasks("boo"));
        assertEquals(testCase3TaskList, taskList.filterTasks("book"));


        assertEquals(testCase4TaskList, taskList.filterTasks("return"));
        assertEquals(testCase4TaskList, taskList.filterTasks("turn"));
    }

    /** Verifies that filtering is case-insensitive. */
    @Test
    public void filterTasks_keywordIsCaseInsensitive() throws JarvisException {
        ToDo todoTask = new ToDo("Read My BOOK");

        taskList.addTask(todoTask);

        assertEquals(
                List.of(todoTask),
                taskList.filterTasks("book")
        );

        assertEquals(
                List.of(todoTask),
                taskList.filterTasks("BOOK")
        );

        assertEquals(
                List.of(todoTask),
                taskList.filterTasks("BoOk")
        );
    }

    /** Verifies that filtering with no matching keyword returns an empty list. */
    @Test
    public void filterTasks_noMatch_returnsEmptyList() throws JarvisException {
        taskList.addTask(new ToDo("Read book"));

        assertTrue(taskList.filterTasks("shopping").isEmpty());
    }

    /** Verifies that filtering with an empty keyword returns all tasks. */
    @Test
    public void filterTasks_emptyKeyword_returnsAllTasks() throws JarvisException {
        ToDo todoTask = new ToDo("Read book");
        Deadline deadlineTask = new Deadline(
                "Return book",
                LocalDateTime.of(2026, 8, 22, 23, 45)
        );

        taskList.addTask(todoTask);
        taskList.addTask(deadlineTask);

        assertEquals(
                List.of(todoTask, deadlineTask),
                taskList.filterTasks("")
        );
    }

    /** Verifies that size reflects additions and deletions. */
    @Test
    public void size_reflectsNumberOfTasks() throws JarvisException {
        assertEquals(0, taskList.size());

        taskList.addTask(new ToDo("Task 1"));
        assertEquals(1, taskList.size());

        taskList.addTask(new ToDo("Task 2"));
        assertEquals(2, taskList.size());

        taskList.deleteTask(0);
        assertEquals(1, taskList.size());

        taskList.deleteTask(0);
        assertEquals(0, taskList.size());
    }

    /** Verifies that a negative task index is rejected. */
    @Test
    public void validateTaskIndex_negativeIndex_throwsException() {
        assertThrows(InvalidTaskNumberException.class, () -> taskList.getTask(-1));
    }

    /** Verifies that an index equal to the task list size is rejected. */
    @Test
    public void validateTaskIndex_indexEqualToSize_throwsException() throws JarvisException {
        taskList.addTask(new ToDo("Read book"));

        assertThrows(InvalidTaskNumberException.class, () -> taskList.getTask(1));
    }

    /** Verifies that an index greater than the task list size is rejected. */
    @Test
    public void validateTaskIndex_indexGreaterThanSize_throwsException() throws JarvisException {
        taskList.addTask(new ToDo("Read book"));

        assertThrows(InvalidTaskNumberException.class, () -> taskList.getTask(2));
    }

    /** Verifies that index zero is valid when the list contains a task. */
    @Test
    public void validateTaskIndex_zeroIndex_doesNotThrowException()
            throws JarvisException {
        ToDo todoTask = new ToDo("Read book");
        taskList.addTask(todoTask);

        assertEquals(todoTask, taskList.getTask(0));
    }

    /** Verifies that an invalid task index produces the expected message. */
    @Test
    public void validateTaskIndex_invalidIndex_hasCorrectMessage() throws JarvisException {
        taskList.addTask(new ToDo("Read book"));

        InvalidTaskNumberException exception =
                assertThrows(InvalidTaskNumberException.class, () -> taskList.getTask(1));

        assertEquals(
                "Error: The task number you inputted is invalid.\n"
                        + "Please re-enter with a valid number ranging from 1 to 1",
                exception.getMessage()
        );
    }
}
