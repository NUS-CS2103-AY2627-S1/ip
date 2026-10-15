package sage.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import sage.task.Deadline;
import sage.task.Event;
import sage.task.Note;
import sage.task.Task;
import sage.task.TaskType;
import sage.task.Todo;

class TaskListTest {
    @Test
    void find_repeatedWhitespace_matchesPhrasesWithoutChangingStoredDescriptions() {
        Task first = new Todo("Read   the\tbook");
        Task second = new Note("Return the book tomorrow");
        TaskList tasks = new TaskList(List.of(first, new Todo("Read another book"), second));

        assertEquals(List.of(first, second), tasks.find("  THE \t book  "));
        assertEquals(List.of(first), tasks.find("read the book"));
        assertEquals("Read   the\tbook", tasks.get(0).getDescription());
        assertEquals(List.of(), tasks.find("\u2003"));
    }

    @Test
    void find_matches_preservesOrderDuplicatesAndIndependentList() {
        Task first = new Task("read book", TaskType.TODO);
        Task second = new Task("return BOOK", TaskType.TODO);
        TaskList tasks = new TaskList(List.of(first, new Task("write report", TaskType.TODO), second, first));

        List<Task> matches = tasks.find("  BOOK  ");

        assertEquals(List.of(first, second, first), matches);
        matches.clear();
        assertEquals(4, tasks.size());
        assertSame(first, tasks.get(0));
    }

    @Test
    void find_emptyOrUnmatchedKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        assertEquals(List.of(), tasks.find("book"));
        tasks.add(new Task("read book", TaskType.TODO));

        assertEquals(List.of(), tasks.find(null));
        assertEquals(List.of(), tasks.find(""));
        assertEquals(List.of(), tasks.find("   "));
        assertEquals(List.of(), tasks.find("missing"));
    }

    @Test
    void addAndGetTrackTasksInOrder() {
        TaskList tasks = new TaskList();
        Task first = new Task("read", TaskType.TODO);
        Task second = new Task("write", TaskType.TODO);

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    void deleteAndMarkingOperationsChangeTaskState() {
        TaskList tasks = new TaskList();
        Task task = new Task("study", TaskType.TODO);
        tasks.add(task);

        Task removed = tasks.delete(0);
        assertSame(task, removed);
        assertEquals(0, tasks.size());

        tasks.add(task);
        tasks.markDone(0);
        assertEquals("X", tasks.get(0).getStatusIcon());

        tasks.markUndone(0);
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    void findReturnsCaseInsensitiveMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("read book", TaskType.TODO));
        tasks.add(new Task("return book", TaskType.TODO));
        tasks.add(new Task("write report", TaskType.TODO));

        assertEquals(2, tasks.find("BOOK").size());
        assertEquals("read book", tasks.find("book").get(0).getDescription());
        assertEquals("return book", tasks.find("book").get(1).getDescription());
    }

    @Test
    void addBeyondLimitThrowsIllegalStateException() {
        TaskList tasks = new TaskList();

        for (int i = 0; i < 100; i++) {
            tasks.add(new Task("task " + i, TaskType.TODO));
        }

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> tasks.add(new Task("overflow", TaskType.TODO)));
        assertEquals("You have reached the maximum number of tasks.", thrown.getMessage());
    }

    @Test
    void add_sameDetailsIgnoresCaseSpacingAndCompletion_rejectsDuplicate() {
        TaskList tasks = new TaskList();
        Todo completed = new Todo("Read book");
        completed.markAsDone();
        tasks.add(completed);
        assertThrows(IllegalArgumentException.class, () -> tasks.add(new Todo("  read   BOOK  ")));
        tasks.add(new Note("Read book"));
        assertThrows(IllegalArgumentException.class, () -> tasks.add(new Note("read BOOK")));
        assertEquals(2, tasks.size());
    }

    @Test
    void add_timeDetailsUseCanonicalDates_distinguishesOnlyDifferentTimes() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("Submit", "2026-09-18"));
        assertThrows(IllegalArgumentException.class, () -> tasks.add(new Deadline("submit", "18/9/2026")));
        tasks.add(new Deadline("Submit", "2026-09-19"));
        tasks.add(new Deadline("Submit", "Sunday"));
        assertThrows(IllegalArgumentException.class, () -> tasks.add(new Deadline("submit", "sunday")));
        tasks.add(new Event("Meet", "2026-09-18 14:00", "2026-09-18 15:00"));
        assertThrows(IllegalArgumentException.class,
                () -> tasks.add(new Event("meet", "18/9/2026 1400", "18/9/2026 1500")));
        tasks.add(new Event("Meet", "2026-09-18 14:00", "2026-09-18 16:00"));
        tasks.add(new Event("Meet", "Monday", "Tuesday"));
        assertThrows(IllegalArgumentException.class,
                () -> tasks.add(new Event("meet", " MONDAY ", "Tuesday")));
        assertEquals(6, tasks.size());
    }

    @Test
    void find_turkishDefaultLocale_keepsCaseInsensitiveMatching() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            TaskList tasks = new TaskList();
            tasks.add(new Todo("FINISH REPORT"));
            assertEquals(1, tasks.find("finish").size());
        } finally {
            Locale.setDefault(original);
        }
    }
}
