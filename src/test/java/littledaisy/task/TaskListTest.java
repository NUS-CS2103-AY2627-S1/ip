package littledaisy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void get_indexOutsideList_assertionErrorThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.get(1));
    }

    @Test
    void find_keywordWithDifferentCase_matchingDescriptionsReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("return BOOK"),
                new Todo("write report")));

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return BOOK", matches.get(1).getDescription());
    }

    @Test
    void find_keywordNotPresent_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(0, tasks.find("report").size());
    }

    @Test
    void containsDuplicate_sameDetailsIgnoringCase_duplicateFound() {
        TaskList tasks = new TaskList(List.of(new Todo("Read book")));

        assertEquals(true, tasks.containsDuplicate(new Todo("read BOOK")));
        assertEquals(false, tasks.containsDuplicate(new Todo("return book")));
    }
}
