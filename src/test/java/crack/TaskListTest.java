package crack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import crack.task.Deadline;
import crack.task.Event;
import crack.task.Task;
import crack.task.Todo;

public class TaskListTest {
    @Test
    public void newList_startsEmpty() {
        assertTrue(new TaskList().isEmpty());
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void addThenRemove_handsBackWhatWasRemoved() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"), new Todo("walk dog"));
        assertEquals(2, tasks.size());

        Task removed = tasks.remove(0);
        assertEquals("[T][ ] buy milk", removed.toString());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] walk dog", tasks.get(0).toString());
    }

    @Test
    public void onDate_keepsOnlyWhatLandsThatDay() throws CrackException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"),
                Deadline.of("return book", "2/12/2020 1500"),
                Event.of("carnival", "1/12/2020 1400", "3/12/2020 1800"),
                Deadline.of("other thing", "2019-10-15"));

        ArrayList<Task> matches = tasks.onDate(LocalDate.of(2020, 12, 2));
        assertEquals(2, matches.size());
        assertEquals("[D][ ] return book (by: Dec 2 2020, 3:00 PM)", matches.get(0).toString());
        assertEquals("[E][ ] carnival (from: Dec 1 2020, 2:00 PM to: Dec 3 2020, 6:00 PM)",
                matches.get(1).toString());
    }

    @Test
    public void find_keywordInDescription_keepsOnlyMatches() throws CrackException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"),
                Deadline.of("return book", "2/12/2020 1500"),
                new Todo("buy milk"));

        ArrayList<Task> matches = tasks.find("book");
        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 2 2020, 3:00 PM)", matches.get(1).toString());
    }

    @Test
    public void find_noMatch_comesBackEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        assertTrue(tasks.find("book").isEmpty());
    }

    @Test
    public void find_matchIsCaseSensitive_doesNotMatchDifferentCase() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertTrue(tasks.find("Book").isEmpty());
        assertEquals(1, tasks.find("book").size());
    }

    @Test
    public void onDate_nothingThatDay_comesBackEmpty() throws CrackException {
        TaskList tasks = new TaskList();
        tasks.add(Deadline.of("return book", "2/12/2020"));
        assertTrue(tasks.onDate(LocalDate.of(2020, 12, 4)).isEmpty());
    }
}
