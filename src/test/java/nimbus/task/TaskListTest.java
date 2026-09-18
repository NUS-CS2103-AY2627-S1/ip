package nimbus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import nimbus.NimbusException;

class TaskListTest {
    @Test
    void get_validAndInvalidNumbers_returnsTaskOrThrows() throws NimbusException {
        TaskList tasks = new TaskList();
        Todo expected = new Todo("read book");
        tasks.add(expected);

        assertEquals(expected, tasks.get(1));
        assertThrows(NimbusException.class, () -> tasks.get(0));
        assertThrows(NimbusException.class, () -> tasks.get(2));
    }

    @Test
    void find_keywordMatchingIgnoresCase_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        Todo firstMatch = new Todo("Read Book");
        Todo secondMatch = new Todo("return book");
        tasks.add(firstMatch);
        tasks.add(new Todo("buy milk"));
        tasks.add(secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), tasks.find("BOOK"));
        assertEquals(List.of(), tasks.find("missing"));
    }

    @Test
    void delete_validNumber_removesAndReturnsTask() throws NimbusException {
        TaskList tasks = new TaskList();
        Todo removed = new Todo("first");
        tasks.add(removed);
        tasks.add(new Todo("second"));

        assertEquals(removed, tasks.delete(1));
        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    void setDescription_validDescription_updatesTask() throws NimbusException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("old description"));

        tasks.get(1).setDescription("new description");

        assertEquals("new description", tasks.get(1).getDescription());
    }
}
