package oreo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import oreo.exception.OreoException;

/** Tests state-changing operations performed by {@link TaskList}. */
class TaskListTest {
    @Test
    void constructor_emptyContent_emptyListCreated() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.getTaskCount());
        assertEquals(" Here are the tasks in your list:", taskList.toString());
    }

    @Test
    void constructor_initialTasks_tasksAddedInGivenOrder() {
        TaskList taskList = new TaskList(new Todo("read book"), new Todo("return book"));

        assertEquals(2, taskList.getTaskCount());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] read book" + System.lineSeparator()
                + " 2.[T][ ] return book", taskList.toString());
    }

    @Test
    void constructor_savedTasks_tasksRestoredWithCompletionState() {
        TaskList taskList = new TaskList("T | X | read book\nT |   | return book");

        assertEquals(2, taskList.getTaskCount());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[T][X] read book" + System.lineSeparator()
                + " 2.[T][ ] return book", taskList.toString());
    }

    @Test
    void addTask_validTask_taskAddedAndIncludedInStorageFormat() {
        TaskList taskList = new TaskList();

        taskList.addTask(new Todo("read book"));

        assertEquals(1, taskList.getTaskCount());
        assertEquals("T |   | read book" + System.lineSeparator(), taskList.storageStringRepresentation());
    }

    @Test
    void markAndUnmarkTask_validTaskNumber_completionStateUpdated() {
        TaskList taskList = taskListWithTodos("read book");

        Task markedTask = taskList.markTask(1);
        assertEquals("X", markedTask.getStatusIcon());

        Task unmarkedTask = taskList.unmarkTask(1);
        assertEquals(" ", unmarkedTask.getStatusIcon());
    }

    @Test
    void deleteTask_middleTask_remainingTasksShiftedAndCountReduced() {
        TaskList taskList = taskListWithTodos("first", "second", "third");

        Task deletedTask = taskList.deleteTask(2);

        assertEquals("second", deletedTask.getDescription());
        assertEquals(2, taskList.getTaskCount());
        assertEquals(" Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] first" + System.lineSeparator()
                + " 2.[T][ ] third", taskList.toString());
    }

    @Test
    void taskOperation_invalidTaskNumber_exceptionThrown() {
        TaskList taskList = taskListWithTodos("read book");

        assertThrows(OreoException.class, () -> taskList.markTask(0));
        assertThrows(OreoException.class, () -> taskList.unmarkTask(2));
        assertThrows(OreoException.class, () -> taskList.deleteTask(-1));
    }

    @Test
    void addTask_moreThanCapacity_exceptionThrown() {
        TaskList taskList = new TaskList();
        for (int i = 0; i < 100; i++) {
            taskList.addTask(new Todo("task " + i));
        }

        assertThrows(OreoException.class, () -> taskList.addTask(new Todo("one too many")));
    }

    @Test
    void addTask_duplicateDetails_exceptionThrown() {
        TaskList taskList = taskListWithTodos("read book");

        assertThrows(OreoException.class, () -> taskList.addTask(new Todo("READ BOOK")));
    }

    @Test
    void constructor_invalidCompletionState_exceptionThrown() {
        assertThrows(OreoException.class, () -> new TaskList("T | ? | read book"));
    }

    @Test
    void findTasks_matchingKeyword_caseInsensitiveMatchesAreNumbered() {
        TaskList taskList = taskListWithTodos("read book", "buy groceries", "return BOOK");

        String matchingTasks = taskList.findTasks("book");

        assertEquals(" Here are the matching tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] read book" + System.lineSeparator()
                + " 2.[T][ ] return BOOK", matchingTasks);
    }

    @Test
    void findTasks_noMatchingKeyword_headerOnly() {
        TaskList taskList = taskListWithTodos("read book");

        assertEquals(" Here are the matching tasks in your list:", taskList.findTasks("meeting"));
    }

    @Test
    void tags_addRemoveAndDisplay_tagsAreNormalizedAndShownWithTask() {
        TaskList taskList = taskListWithTodos("watch movie");

        Task taggedTask = taskList.addTags(1, List.of("#Fun", "#weekend", "#fun"));
        assertEquals(List.of("#fun", "#weekend"), taggedTask.getTags());
        assertEquals("[T][ ] watch movie #fun #weekend", taggedTask.toString());

        Task untaggedTask = taskList.removeTags(1, List.of("#FUN"));
        assertEquals(List.of("#weekend"), untaggedTask.getTags());
    }

    @Test
    void listTags_taggedTasks_groupsTagsWithOriginalTaskNumbers() {
        TaskList taskList = taskListWithTodos("watch movie", "submit report");
        taskList.addTags(1, List.of("#fun", "#weekend"));
        taskList.addTags(2, List.of("#school"));

        assertEquals(" Here are the tags and their tasks:" + System.lineSeparator()
                + "#fun" + System.lineSeparator()
                + "  1.[T][ ] watch movie #fun #weekend" + System.lineSeparator()
                + "#school" + System.lineSeparator()
                + "  2.[T][ ] submit report #school" + System.lineSeparator()
                + "#weekend" + System.lineSeparator()
                + "  1.[T][ ] watch movie #fun #weekend", taskList.listTags());
    }

    @Test
    void listTags_noTaggedTasks_noTagsMessage() {
        TaskList taskList = taskListWithTodos("watch movie");

        assertEquals(" There are no tags in your list.", taskList.listTags());
    }

    @Test
    void constructor_savedTaggedTasks_tagsRestoredAndWrittenInStorageFormat() {
        TaskList taskList = new TaskList("T | X | read book | tags: #fun,#weekend");

        assertEquals(List.of("#fun", "#weekend"), taskList.markTask(1).getTags());
        assertEquals("T | X | read book | tags: #fun,#weekend" + System.lineSeparator(),
                taskList.storageStringRepresentation());
    }

    @Test
    void constructor_savedDeadlineAndEvent_restoresDateDetails() {
        TaskList taskList = new TaskList("D |   | submit report | 2026-09-30\n"
                + "E | X | team meeting | 2026-09-15 | 2026-09-16");

        assertEquals(" Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[D][ ] submit report (by: Sep 30 2026)" + System.lineSeparator()
                + " 2.[E][X] team meeting (from: Sep 15 2026 to: Sep 16 2026)", taskList.toString());
    }

    @Test
    void constructor_malformedStorageLines_exceptionThrown() {
        assertThrows(OreoException.class, () -> new TaskList("T |   |"));
        assertThrows(OreoException.class, () -> new TaskList("D |   | report"));
        assertThrows(OreoException.class, () -> new TaskList("T |   | task | tags: "));
        assertThrows(OreoException.class, () -> new TaskList("T |   | task | unknown: #tag"));
        assertThrows(OreoException.class, () -> new TaskList("Z |   | task"));
    }

    @Test
    void constructor_duplicateSavedTasks_exceptionThrown() {
        assertThrows(OreoException.class, () -> new TaskList("T |   | read book\nT | X | READ BOOK"));
    }

    @Test
    void tags_invalidTaskNumber_exceptionThrown() {
        TaskList taskList = taskListWithTodos("watch movie");

        assertThrows(OreoException.class, () -> taskList.addTags(2, List.of("#fun")));
        assertThrows(OreoException.class, () -> taskList.removeTags(0, List.of("#fun")));
    }

    /** Creates a task list containing to-do tasks with the supplied descriptions. */
    private TaskList taskListWithTodos(String... descriptions) {
        TaskList taskList = new TaskList();
        for (String description : descriptions) {
            taskList.addTask(new Todo(description));
        }
        return taskList;
    }
}
