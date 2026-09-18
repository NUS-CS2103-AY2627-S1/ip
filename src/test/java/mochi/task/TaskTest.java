package mochi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void taskTypes_formatAndCopyAllDetailsIndependently() {
        List<Task> tasks = List.of(new Task("base"), new Todo("book"),
                new Deadline("report", LocalDate.of(2028, 2, 29)), new Event("meeting", "noon", "evening"));
        List<String> expectedRecords = List.of("1 | base", "T | 1 | book", "D | 1 | report | 2028-02-29",
                "E | 1 | meeting | noon | evening");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            task.markAsDone();
            Task copy = task.copy();
            assertNotSame(task, copy);
            assertEquals(task.getClass(), copy.getClass());
            assertEquals(task.toString(), copy.toString());
            assertEquals(expectedRecords.get(i), copy.toFileString());
            copy.markAsNotDone();
            assertEquals("X", task.getStatusIcon());
            assertEquals(" ", copy.getStatusIcon());
        }
    }

    @Test
    void searchAndDeadline_withTurkishLocale_remainConsistent() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(new Todo("FINISH report").containsKeyword("finish"));
            assertFalse(new Todo("read book").containsKeyword("bread"));
            assertEquals("[D][ ] report (by: Sep 18 2026)",
                    new Deadline("report", LocalDate.of(2026, 9, 18)).toString());
        } finally {
            Locale.setDefault(previous);
        }
    }
}
