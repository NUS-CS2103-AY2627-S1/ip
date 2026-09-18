package benjamin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import benjamin.BenjaminException;

public class TaskListTest {
    private static TaskList sampleList() throws BenjaminException {
        return TaskList.of(
                new Todo("read book"),
                new Deadline("return book", TaskDateTime.parse("2019-06-06")),
                new Event("project meeting",
                        TaskDateTime.parse("2019-08-06 1400"),
                        TaskDateTime.parse("2019-08-08 1600")));
    }

    @Test
    public void of_severalTasks_keepsThemInTheOrderGiven() {
        TaskList tasks = TaskList.of(new Todo("read book"), new Todo("join sports club"));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] join sports club", tasks.get(1).toString());
    }

    @Test
    public void of_noTasks_makesAnEmptyList() {
        assertEquals(0, TaskList.of().size());
    }

    @Test
    public void size_newList_isZero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void add_severalTasks_sizeGrowsAndOrderKept() throws BenjaminException {
        TaskList tasks = sampleList();

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00pm to: Aug 08 2019, 4:00pm)",
                tasks.get(2).toString());
    }

    @Test
    public void remove_middleTask_returnsItAndClosesTheGap() throws BenjaminException {
        TaskList tasks = sampleList();

        Task removed = tasks.remove(1);

        assertEquals("[D][ ] return book (by: Jun 06 2019)", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00pm to: Aug 08 2019, 4:00pm)",
                tasks.get(1).toString());
    }

    @Test
    public void get_indexPastTheEnd_exceptionThrown() throws BenjaminException {
        TaskList tasks = sampleList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(3));
    }

    @Test
    public void findOn_deadlineDueThatDay_taskFound() throws BenjaminException {
        List<Task> matches = sampleList().findOn(LocalDate.of(2019, 6, 6));

        assertEquals(1, matches.size());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", matches.get(0).toString());
    }

    @Test
    public void findOn_dayInsideEventRange_eventFound() throws BenjaminException {
        List<Task> matches = sampleList().findOn(LocalDate.of(2019, 8, 7));

        assertEquals(1, matches.size());
        assertEquals("project meeting", matches.get(0).description);
    }

    @Test
    public void findOn_firstAndLastDayOfEvent_eventFound() throws BenjaminException {
        assertEquals(1, sampleList().findOn(LocalDate.of(2019, 8, 6)).size());
        assertEquals(1, sampleList().findOn(LocalDate.of(2019, 8, 8)).size());
    }

    @Test
    public void findOn_dayOutsideEventRange_nothingFound() throws BenjaminException {
        assertEquals(0, sampleList().findOn(LocalDate.of(2019, 8, 9)).size());
    }

    @Test
    public void findOn_dateWithNoTasks_emptyListReturned() throws BenjaminException {
        assertEquals(0, sampleList().findOn(LocalDate.of(2020, 1, 1)).size());
    }

    @Test
    public void find_keywordInSeveralTasks_allOfThemFoundInOrder() throws BenjaminException {
        List<Task> matches = sampleList().find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", matches.get(1).toString());
    }

    @Test
    public void find_differentCapitalization_stillFound() throws BenjaminException {
        assertEquals(2, sampleList().find("BOOK").size());
        assertEquals(2, sampleList().find("BoOk").size());
    }

    @Test
    public void find_partOfAWord_stillFound() throws BenjaminException {
        assertEquals(1, sampleList().find("meet").size());
    }

    @Test
    public void find_keywordInNoTask_emptyListReturned() throws BenjaminException {
        assertEquals(0, sampleList().find("holiday").size());
    }

    @Test
    public void find_emptyList_emptyListReturned() {
        assertEquals(0, new TaskList().find("book").size());
    }

    @Test
    public void toSaveFormat_mixedTasks_oneLinePerTaskInOrder() throws BenjaminException {
        TaskList tasks = sampleList();
        tasks.get(0).markAsDone();

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2019-06-06",
                "E | 0 | project meeting | 2019-08-06 1400 | 2019-08-08 1600"),
                tasks.toSaveFormat());
    }

    @Test
    public void toSaveFormat_emptyList_noLines() {
        assertEquals(List.of(), new TaskList().toSaveFormat());
    }
}
