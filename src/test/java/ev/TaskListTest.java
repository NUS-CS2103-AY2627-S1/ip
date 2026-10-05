package ev;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ev.task.Todo;

/**
 * Contains unit tests for {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void add_threeTasks_sizeReflectsCount() {
        TaskList tasks = TaskList.of(new Todo("borrow book"),
                new Todo("return book"), new Todo("buy milk"));

        assertEquals(3, tasks.size());
    }

    @Test
    public void remove_middleTask_returnsItAndShrinksList() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        assertEquals("[T][ ] second", tasks.remove(1).toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] third", tasks.get(1).toString());
    }

    @Test
    public void findPositions_matchingKeyword_returnsOnlyMatches() {
        TaskList tasks = TaskList.of(new Todo("borrow book"),
                new Todo("return book"), new Todo("buy milk"));

        assertEquals(List.of(1, 2), tasks.findPositions("book"));
    }

    @Test
    public void findPositions_matchLaterInRegistry_returnsRealPosition() {
        TaskList tasks = TaskList.of(new Todo("alpha"), new Todo("beta"), new Todo("buy milk"));

        assertEquals(List.of(3), tasks.findPositions("buy"));
    }

    @Test
    public void findPositions_noMatch_returnsEmptyList() {
        assertEquals(List.of(), TaskList.of(new Todo("borrow book")).findPositions("spiderman"));
    }

    @Test
    public void findPositions_differentCase_stillMatches() {
        TaskList tasks = TaskList.of(new Todo("Borrow Book"));

        assertEquals(List.of(1), tasks.findPositions("book"));
        assertEquals(List.of(1), tasks.findPositions("BOOK"));
    }

    @Test
    public void findPositions_statusIcon_returnsNoMatches() {
        Todo done = new Todo("borrow book");

        done.markAsDone();

        assertEquals(List.of(), TaskList.of(done).findPositions("X"));
    }

    @Test
    public void sortedPositions_unorderedTasks_returnsDescriptionOrder() {
        TaskList tasks = TaskList.of(new Todo("write report"),
                new Todo("borrow book"), new Todo("mail parcel"));

        assertEquals(List.of(2, 3, 1), tasks.sortedPositions());
    }

    @Test
    public void sortedPositions_mixedCase_ignoresCase() {
        TaskList tasks = TaskList.of(new Todo("apple"), new Todo("Zebra"), new Todo("banana"));

        assertEquals(List.of(1, 3, 2), tasks.sortedPositions());
    }

    @Test
    public void positions_threeTasks_returnsOneToThree() {
        assertEquals(List.of(1, 2, 3),
                TaskList.of(new Todo("a"), new Todo("b"), new Todo("c")).positions());
    }

    @Test
    public void sortedPositions_doesNotReorderRegistry() {
        TaskList tasks = TaskList.of(new Todo("write report"), new Todo("borrow book"));

        tasks.sortedPositions();

        assertEquals("[T][ ] write report", tasks.get(0).toString());
    }
}
