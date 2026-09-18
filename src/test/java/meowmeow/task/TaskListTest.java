package meowmeow.task;

import meowmeow.MeowMeowException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {

    private TaskList threeTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy bread"));
        return tasks;
    }

    @Test
    public void parseIndex_firstTask_zeroReturned() throws MeowMeowException {
        assertEquals(0, threeTasks().parseIndex("1", "mark"));
    }

    @Test
    public void parseIndex_lastTask_correctIndexReturned() throws MeowMeowException {
        assertEquals(2, threeTasks().parseIndex("3", "mark"));
    }

    @Test
    public void parseIndex_emptyArgument_exceptionThrown() {
        MeowMeowException e = assertThrows(MeowMeowException.class, ()
                -> threeTasks().parseIndex("", "mark"));
        assertTrue(e.getMessage().contains("mark"));
    }

    @Test
    public void parseIndex_emptyList_exceptionThrown() {
        MeowMeowException e = assertThrows(MeowMeowException.class, ()
                -> new TaskList().parseIndex("1", "delete"));
        assertTrue(e.getMessage().contains("empty"));
    }

    @Test
    public void parseIndex_notANumber_exceptionThrown() {
        assertThrows(MeowMeowException.class, () -> threeTasks().parseIndex("abc", "mark"));
    }

    @Test
    public void parseIndex_zeroIndex_exceptionThrown() {
        assertThrows(MeowMeowException.class, () -> threeTasks().parseIndex("0", "mark"));
    }

    @Test
    public void parseIndex_negativeIndex_exceptionThrown() {
        assertThrows(MeowMeowException.class, () -> threeTasks().parseIndex("-1", "mark"));
    }

    @Test
    public void parseIndex_indexBeyondSize_exceptionThrown() {
        assertThrows(MeowMeowException.class, () -> threeTasks().parseIndex("4", "mark"));
    }

    @Test
    public void delete_middleTask_remainingTasksShift() {
        TaskList tasks = threeTasks();
        Task removed = tasks.delete(1);
        assertEquals("[T][ ] return book", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] buy bread", tasks.get(1).toString());
    }

    @Test
    public void asList_modifyingReturnedList_originalUnchanged() {
        TaskList tasks = threeTasks();
        tasks.asList().clear();
        assertEquals(3, tasks.size());
    }

    @Test
    public void findByKeyword_matchingTasks_matchesReturnedInOrder() {
        TaskList tasks = threeTasks();
        assertEquals(2, tasks.findByKeyword("book").size());
        assertEquals(1, tasks.findByKeyword("BREAD").size());
        assertEquals(0, tasks.findByKeyword("xyz").size());
    }

    @Test
    public void hasDuplicateOf_sameDescription_returnsTrue() {
        assertTrue(threeTasks().hasDuplicateOf(new Todo("read book")));
    }

    @Test
    public void hasDuplicateOf_differentCase_returnsTrue() {
        assertTrue(threeTasks().hasDuplicateOf(new Todo("READ BOOK")));
    }

    @Test
    public void hasDuplicateOf_unseenDescription_returnsFalse() {
        assertFalse(threeTasks().hasDuplicateOf(new Todo("read magazine")));
    }

    @Test
    public void hasDuplicateOf_sameDescriptionDifferentType_returnsFalse() throws MeowMeowException {
        Deadline deadline = new Deadline("read book", TaskDateTime.parse("2019-12-02"));
        assertFalse(threeTasks().hasDuplicateOf(deadline));
    }

    @Test
    public void hasDuplicateOf_deadlineWithSameDate_returnsTrue() throws MeowMeowException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", TaskDateTime.parse("2019-12-02")));
        assertTrue(tasks.hasDuplicateOf(new Deadline("submit report", TaskDateTime.parse("2019-12-02"))));
    }

    @Test
    public void hasDuplicateOf_deadlineWithDifferentDate_returnsFalse() throws MeowMeowException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", TaskDateTime.parse("2019-12-02")));
        assertFalse(tasks.hasDuplicateOf(new Deadline("submit report", TaskDateTime.parse("2019-12-03"))));
    }
}