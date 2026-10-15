package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Verifies literal description searches and independence from locale settings.
 */
public class TaskSearchTest {
    @Test
    public void findTasks_literalPhraseAndUnicode_matchesDescriptionOnly() {
        TaskList tasks = new TaskList();
        Task todo = new ToDo("read book [draft] 读书");
        todo.markDone();
        tasks.addTask(todo);
        tasks.addTask(new Deadline("report", LocalDate.of(2026, 9, 16)));
        assertEquals(List.of(todo), tasks.findTasks("READ BOOK"));
        assertEquals(List.of(todo), tasks.findTasks("[draft]"));
        assertEquals(List.of(todo), tasks.findTasks("读书"));
        assertTrue(tasks.findTasks("2026").isEmpty());
        assertTrue(tasks.findTasks("[X]").isEmpty());
        tasks.findTasks("book").clear();
        assertEquals(2, tasks.getSize());
    }

    @Test
    @ResourceLock("defaultLocale")
    public void findTasks_turkishLocale_matchesEnglishCaseInsensitively() {
        Locale original = Locale.getDefault();
        Locale display = Locale.getDefault(Locale.Category.DISPLAY);
        Locale format = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Locale.setDefault(Locale.Category.FORMAT, format);
            TaskList tasks = new TaskList();
            Task task = new ToDo("TITLE");
            tasks.addTask(task);
            assertEquals(List.of(task), tasks.findTasks("title"));
        } finally {
            Locale.setDefault(original);
            Locale.setDefault(Locale.Category.DISPLAY, display);
            Locale.setDefault(Locale.Category.FORMAT, format);
        }
    }
}
