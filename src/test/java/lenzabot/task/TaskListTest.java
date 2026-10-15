package lenzabot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lenzabot.LenZaBotException;

class TaskListTest {
    @Test
    void constructor_initialTasks_copiesInputList() {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("read book"));

        TaskList taskList = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, taskList.getSize());
    }

    @Test
    void addTask_validTask_appendsTask() {
        TaskList taskList = new TaskList(List.of());
        Task task = new Todo("read book");

        taskList.addTask(task);

        assertEquals(1, taskList.getSize());
        assertSame(task, taskList.getAllTasks().get(0));
    }

    @Test
    void getAllTasks_attemptedExternalMutation_throwsException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(UnsupportedOperationException.class, () -> taskList.getAllTasks().clear());
        assertEquals(1, taskList.getSize());
    }

    @Test
    void markAndUnmarkTask_validIndex_updatesCompletionStatus() throws LenZaBotException {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));

        Task markedTask = taskList.markTask(1);
        assertSame(task, markedTask);
        assertTrue(task.isCompleted());

        Task unmarkedTask = taskList.unmarkTask(1);
        assertSame(task, unmarkedTask);
        assertFalse(task.isCompleted());
    }

    @Test
    void deleteTask_validIndex_removesAndReturnsTask() throws LenZaBotException {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = taskList.deleteTask(1);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), taskList.getAllTasks());
    }

    @Test
    void findTasks_keywordMatchesDescriptions_returnsAllMatches() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return library book");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, new Todo("buy milk")));

        List<Task> matchingTasks = taskList.findTasks("book");

        assertEquals(List.of(firstTask, secondTask), matchingTasks);
    }

    @Test
    void findTasks_keywordUsesDifferentCase_returnsMatch() {
        Task task = new Todo("Read Book");
        TaskList taskList = new TaskList(List.of(task));

        assertEquals(List.of(task), taskList.findTasks("BOOK"));
    }

    @Test
    void findTasks_noMatchingDescription_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), taskList.findTasks("milk"));
    }

    @Test
    void markTask_emptyList_throwsException() {
        TaskList taskList = new TaskList(List.of());

        LenZaBotException exception = assertThrows(LenZaBotException.class, () -> taskList.markTask(1));

        assertEquals("there are no tasks in the list yet.", exception.getMessage());
    }

    @Test
    void deleteTask_indexBelowRange_throwsException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        LenZaBotException exception = assertThrows(LenZaBotException.class, () -> taskList.deleteTask(0));

        assertEquals("task number must be between 1 and 1.", exception.getMessage());
    }

    @Test
    void deleteTask_indexAboveRange_throwsException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        LenZaBotException exception = assertThrows(LenZaBotException.class, () -> taskList.deleteTask(2));

        assertEquals("task number must be between 1 and 1.", exception.getMessage());
    }
}
