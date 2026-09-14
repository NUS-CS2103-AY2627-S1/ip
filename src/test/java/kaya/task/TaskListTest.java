package kaya.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/** Tests collection ownership, search behaviour, snapshots, and index contracts. */
public class TaskListTest {
    @Test
    public void find_keywordInDescriptions_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("return book"),
                new Todo("buy groceries")));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_keywordWithDifferentCase_returnsMatchingTask() {
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));

        List<Task> matches = tasks.find("book");

        assertEquals(1, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
    }

    @Test
    public void find_keywordNotPresent_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("meeting").isEmpty());
    }

    @Test
    public void collectionOperations_preserveOrderAndDoNotExposeInternalList() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        List<Task> input = new ArrayList<>(List.of(first, second));
        TaskList tasks = new TaskList(input);
        input.clear();
        tasks.asList().clear();
        assertEquals(2, tasks.size());

        Todo replacement = new Todo("replacement");
        tasks.set(1, replacement);
        tasks.add(new Todo("third"));
        assertSame(first, tasks.delete(0));
        assertSame(replacement, tasks.get(0));
        assertEquals("third", tasks.get(1).getDescription());
        assertEquals(2, tasks.size());
    }

    @Test
    public void copy_mutatingEitherList_preservesTheOtherListAndItsStatuses() {
        TaskList original = new TaskList(List.of(new Todo("first"), new Todo("second")));
        original.get(0).markAsDone();
        TaskList snapshot = original.copy();

        assertNotSame(original.get(0), snapshot.get(0));
        original.get(0).markAsNotDone();
        original.delete(1);
        assertTrue(snapshot.get(0).isDone());
        assertEquals(2, snapshot.size());

        snapshot.get(1).markAsDone();
        snapshot.add(new Todo("snapshot only"));
        assertEquals(1, original.size());
        assertFalse(original.get(0).isDone());
        assertEquals("first", original.get(0).getDescription());
    }

    @Test
    public void invalidArguments_assertionContractsRejectBadStateBeforeMutation() {
        assertTrue(TaskList.class.desiredAssertionStatus(), "These contract checks require assertions enabled.");
        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () -> new TaskList(Collections.singletonList(null)));
        TaskList tasks = new TaskList(List.of(new Todo("keep")));
        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.set(0, null));
        for (int index : new int[] {-1, 1}) {
            assertThrows(AssertionError.class, () -> tasks.get(index));
            assertThrows(AssertionError.class, () -> tasks.delete(index));
            assertThrows(AssertionError.class, () -> tasks.set(index, new Todo("invalid")));
        }
        assertEquals(1, tasks.size());
        assertEquals("keep", tasks.get(0).getDescription());
    }

    @Test
    @ResourceLock("default-locale")
    public void find_turkishDefaultLocale_matchesEnglishCaseAndKeepsOriginalOrder() {
        Locale previous = Locale.getDefault();
        Locale previousFormat = Locale.getDefault(Locale.Category.FORMAT);
        Locale previousDisplay = Locale.getDefault(Locale.Category.DISPLAY);
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            TaskList tasks = new TaskList(List.of(new Todo("FILE report"), new Todo("file notes"), new Todo("other")));
            List<Task> found = tasks.find("file");

            assertEquals(List.of("FILE report", "file notes"), found.stream().map(Task::getDescription).toList());
            found.clear();
            assertEquals(3, tasks.size());
            assertTrue(new TaskList().find("file").isEmpty());
        } finally {
            Locale.setDefault(previous);
            Locale.setDefault(Locale.Category.FORMAT, previousFormat);
            Locale.setDefault(Locale.Category.DISPLAY, previousDisplay);
        }
    }
}
