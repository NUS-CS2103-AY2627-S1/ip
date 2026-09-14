package groot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests task state, display contracts, serialization, and locale-independent matching.
 */
public class TaskTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void task_allSubtypes_formatsStatusAndEscapesData(boolean isDone) {
        String description = "read | notes\\";
        Task[] tasks = {new Task(description), new Todo(description),
            new Deadline(description, LocalDate.of(2028, 2, 29)), new Event(description, "a|b", "c\\d")};
        String[] displayPrefixes = {"", "[T]", "[D]", "[E]"};
        String[] displaySuffixes = {"", "", " (by: Feb 29 2028)", " (from: a|b to: c\\d)"};
        String[] dataPrefixes = {"", "T | ", "D | ", "E | "};
        String[] dataSuffixes = {"", "", " | 2028-02-29", " | a\\|b | c\\\\d"};
        for (int i = 0; i < tasks.length; i++) {
            Task task = tasks[i];
            assertFalse(task.isDone());
            if (isDone) {
                task.markAsDone();
            }
            assertEquals(isDone, task.isDone());
            assertEquals(description, task.getDescription());
            assertEquals(isDone ? "X" : " ", task.getStatusIcon());
            assertEquals(displayPrefixes[i] + "[" + task.getStatusIcon() + "] " + description
                    + displaySuffixes[i], task.toString());
            assertEquals(dataPrefixes[i] + (isDone ? "1" : "0") + " | read \\| notes\\\\"
                    + dataSuffixes[i], task.toDataString());
            task.markAsDone();
            task.markAsNotDone();
            task.markAsNotDone();
            assertFalse(task.isDone());
        }
        assertEquals(LocalDate.of(2028, 2, 29), ((Deadline) tasks[2]).getDueDate());
    }

    @Test
    public void matches_turkishDefaultLocale_usesLocaleIndependentCaseFolding() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Task task = new Task("FINISH report");
            assertTrue(task.matches("finish"));
            assertTrue(task.matches("REPORT"));
            assertTrue(task.matches(""));
            assertFalse(task.matches("missing"));
            assertEquals("[D][ ] report (by: Jan 01 2026)",
                    new Deadline("report", LocalDate.of(2026, 1, 1)).toString());
        } finally {
            Locale.setDefault(original);
        }
    }
}
