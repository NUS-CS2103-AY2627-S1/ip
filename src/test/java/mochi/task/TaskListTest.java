package mochi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void find_mixedCaseKeyword_returnsOnlyMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy bread"));

        TaskList matches = tasks.find("BOOK");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] Read Book", matches.get(0).toString());
        assertEquals("[T][ ] return book", matches.get(1).toString());
    }

    @Test
    void copy_modifyStatusAndOrder_keepsOriginalTasksUnchanged() {
        TaskList tasks = new TaskList(List.of(new Todo("Zebra"), new Todo("apple"), new Todo("Apple")));
        TaskList copy = tasks.copy();
        copy.get(0).markAsDone();
        copy.sortByDescription();
        assertEquals("[T][ ] apple", copy.get(0).toString());
        assertEquals("[T][ ] Apple", copy.get(1).toString());
        assertEquals("[T][X] Zebra", copy.get(2).toString());
        assertEquals("[T][ ] Zebra", tasks.get(0).toString());
        copy.remove(2);
        assertEquals(3, tasks.size());
        assertEquals(2, copy.size());
    }

    @Test
    void constructorAndSearch_ownCollectionsWithoutChangingOriginalList() {
        ArrayList<Task> source = new ArrayList<>(List.of(new Todo("read book")));
        TaskList tasks = new TaskList(source);
        source.clear();
        assertEquals(1, tasks.size());
        tasks.find("book").remove(0);
        assertEquals(1, tasks.size());
        assertEquals(0, tasks.find("missing").size());
        assertEquals("read book", tasks.iterator().next().getDescription());
    }

    @Test
    void sort_emptyAndRepeatedSort_preservesOrder() {
        TaskList tasks = new TaskList();
        tasks.sortByDescription();
        assertEquals(0, tasks.size());
        tasks.add(new Todo("b"));
        tasks.add(new Todo("a"));
        tasks.sortByDescription();
        tasks.sortByDescription();
        assertEquals("a", tasks.get(0).getDescription());
        assertEquals("b", tasks.get(1).getDescription());
    }
}
