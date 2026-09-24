package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests collection ordering, varargs, stream formatting, and index validation.
 */
class TaskListTest {
    @Test
    void addAndFind_preserveOriginalTaskNumbers() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"));
        assertEquals("1. [T] [ ] first\n2. [T] [ ] second\n", tasks.formatAll());
        assertEquals("2. [T] [ ] second\n", tasks.formatMatching("SECOND"));
        assertEquals("", tasks.formatMatching("absent"));
        assertEquals("[T] [ ] first", tasks.remove("1").toString());
        assertEquals("1. [T] [ ] second\n", tasks.formatAll());
    }

    @Test
    void sort_caseInsensitive_preservesEqualDescriptionsAndStatuses() {
        TaskList tasks = new TaskList();
        Todo completed = new Todo("alpha");
        completed.mark();
        tasks.add(new Todo("zebra"), new Todo("Alpha"), completed, new Todo("beta"));
        tasks.sortByDescription();
        assertEquals("1. [T] [ ] Alpha\n2. [T] [X] alpha\n3. [T] [ ] beta\n4. [T] [ ] zebra\n",
                tasks.formatAll());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "2", "abc", "1.5", "2147483648", "-2147483648"})
    void get_invalidIndex_doesNotChangeList(String index) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        assertThrows(IllegalArgumentException.class, () -> tasks.get(index));
        assertThrows(IllegalArgumentException.class, () -> tasks.remove(index));
        assertEquals(1, tasks.getTasks().size());
    }

    @Test
    void publicMethods_rejectNullArguments() {
        TaskList tasks = new TaskList();
        assertThrows(IllegalArgumentException.class, () -> tasks.add((Task[]) null));
        assertThrows(IllegalArgumentException.class, () -> tasks.get(null));
    }
}
