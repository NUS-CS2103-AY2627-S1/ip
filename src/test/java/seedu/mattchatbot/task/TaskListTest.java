package seedu.mattchatbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.mattchatbot.MattChatBotException;

/**
 * Tests that TaskList enforces its own bounds, and that the date search
 * matches the task types that carry dates.
 */
public class TaskListTest {

    private static LocalDateTime at(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    private static TaskList listOfThree() throws MattChatBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"),
                new Deadline("return book", at(2019, 10, 15)),
                new Event("orientation", at(2019, 10, 14), at(2019, 10, 18)));
        return tasks;
    }

    @Test
    public void newList_noArguments_isEmpty() throws Exception {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void newList_existingTasks_holdsThem() throws Exception {
        ArrayList<Task> given = new ArrayList<>();
        given.add(new Todo("read book"));
        TaskList tasks = new TaskList(given);
        assertEquals(1, tasks.size());
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void get_validIndex_taskReturned() throws Exception {
        TaskList tasks = listOfThree();
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[E][ ] orientation (from: Oct 14 2019 to: Oct 18 2019)",
                tasks.get(2).toString());
    }

    @Test
    public void get_emptyList_exceptionThrown() throws Exception {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                new TaskList().get(0));
        assertEquals("Your list is empty, so there is no entry 1 yet.", e.getMessage());
    }

    @Test
    public void get_indexPastEnd_exceptionThrown() throws Exception {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                listOfThree().get(3));
        assertEquals("You have 3 entries, so there is no entry 4. Type list to review them.",
                e.getMessage());
    }

    @Test
    public void get_negativeIndex_exceptionThrown() throws Exception {
        assertThrows(MattChatBotException.class, () -> listOfThree().get(-1));
    }

    @Test
    public void remove_validIndex_taskRemovedAndReturned() throws Exception {
        TaskList tasks = listOfThree();
        Task removed = tasks.remove(1);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", removed.toString());
        assertEquals(2, tasks.size());
        // The tasks after it shift down, so position 1 is now the event.
        assertEquals("[E][ ] orientation (from: Oct 14 2019 to: Oct 18 2019)",
                tasks.get(1).toString());
    }

    @Test
    public void remove_indexPastEnd_exceptionThrownAndListUnchanged() throws Exception {
        TaskList tasks = listOfThree();
        assertThrows(MattChatBotException.class, () -> tasks.remove(5));
        assertEquals(3, tasks.size());
    }

    @Test
    public void getTasksOn_deadlineOnThatDate_deadlineMatched() throws Exception {
        ArrayList<Task> found = listOfThree().getTasksOn(LocalDate.of(2019, 10, 15));
        // The deadline falls on the date, and the event spans it.
        assertEquals(2, found.size());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", found.get(0).toString());
    }

    @Test
    public void getTasksOn_dateInsideEventRange_eventMatched() throws Exception {
        ArrayList<Task> found = listOfThree().getTasksOn(LocalDate.of(2019, 10, 17));
        assertEquals(1, found.size());
        assertEquals("[E][ ] orientation (from: Oct 14 2019 to: Oct 18 2019)",
                found.get(0).toString());
    }

    @Test
    public void getTasksOn_eventBoundaryDates_bothEndsIncluded() throws Exception {
        assertEquals(1, listOfThree().getTasksOn(LocalDate.of(2019, 10, 14)).size());
        assertEquals(1, listOfThree().getTasksOn(LocalDate.of(2019, 10, 18)).size());
    }

    @Test
    public void getTasksOn_dateOutsideEverything_nothingMatched() throws Exception {
        assertTrue(listOfThree().getTasksOn(LocalDate.of(2020, 1, 1)).isEmpty());
        // A todo has no date, so it never matches.
        assertTrue(new TaskList(new ArrayList<>()).getTasksOn(LocalDate.now()).isEmpty());
    }

    @Test
    public void getTasksMatching_keywordInDescription_taskMatched() throws Exception {
        ArrayList<Task> found = listOfThree().getTasksMatching("book");
        assertEquals(2, found.size());
        assertEquals("[T][ ] read book", found.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", found.get(1).toString());
    }

    @Test
    public void getTasksMatching_differentCase_stillMatched() throws Exception {
        assertEquals(2, listOfThree().getTasksMatching("BOOK").size());
        assertEquals(2, listOfThree().getTasksMatching("BoOk").size());
    }

    @Test
    public void getTasksMatching_partialWord_matched() throws Exception {
        // The keyword is looked for anywhere in the description, not only as a
        // whole word, so a prefix matches too.
        assertEquals(1, listOfThree().getTasksMatching("orient").size());
    }

    @Test
    public void getTasksMatching_keywordNotPresent_nothingMatched() throws Exception {
        assertTrue(listOfThree().getTasksMatching("bicycle").isEmpty());
    }

    @Test
    public void getTasksMatching_dateTextNotSearched_nothingMatched() throws Exception {
        // Only the description is searched, not the formatted dates.
        assertTrue(listOfThree().getTasksMatching("Oct").isEmpty());
    }

    @Test
    public void add_severalTasksAtOnce_allAddedInOrder() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"), new Todo("third"));
        assertEquals(3, tasks.size());
        assertEquals("[T][ ] first", tasks.asList().get(0).toString());
        assertEquals("[T][ ] third", tasks.asList().get(2).toString());
    }

    @Test
    public void add_noTasks_listUnchanged() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only one"));
        tasks.add();
        assertEquals(1, tasks.size());
    }

    @Test
    public void describeSize_variousCounts_correctPlural() throws Exception {
        TaskList tasks = new TaskList();
        assertEquals("0 entries", tasks.describeSize());
        tasks.add(new Todo("one"));
        assertEquals("1 entry", tasks.describeSize());
        tasks.add(new Todo("two"));
        assertEquals("2 entries", tasks.describeSize());
    }

    @Test
    public void contains_sameTodoAlreadyPresent_true() throws Exception {
        assertTrue(listOfThree().contains(new Todo("read book")));
    }

    @Test
    public void contains_sameDescriptionButDoneAlready_true() throws Exception {
        // Being ticked off does not make it a different entry.
        TaskList tasks = new TaskList();
        Todo done = new Todo("read book");
        done.markAsDone();
        tasks.add(done);
        assertTrue(tasks.contains(new Todo("read book")));
    }

    @Test
    public void contains_deadlineWithDifferentDueDate_false() throws Exception {
        // The same errand on two different days is two entries, not one.
        assertFalse(listOfThree().contains(
                new Deadline("return book", at(2019, 12, 1))));
    }

    @Test
    public void contains_sameDescriptionButDifferentType_false() throws Exception {
        assertFalse(listOfThree().contains(
                new Deadline("read book", at(2019, 10, 15))));
    }

    @Test
    public void contains_notInList_false() throws Exception {
        assertFalse(listOfThree().contains(new Todo("buy bread")));
    }
}
