package arin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import arin.task.Deadline;
import arin.task.Event;
import arin.task.Task;
import arin.task.Todo;

/**
 * Tests task searching in {@link Arin}.
 */
class ArinTest {
    @Test
    void findTasks_matchingKeyword_returnsAllMatchingTasksInOriginalOrder() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2026, 6, 6));
        Task projectMeeting = new Event("project meeting", "2pm", "4pm");

        List<Task> matchingTasks = Arin.findTasks(
                List.of(readBook, returnBook, projectMeeting), "book");

        assertEquals(List.of(readBook, returnBook), matchingTasks);
    }

    @Test
    void findTasks_keywordWithDifferentCase_returnsMatchingTasks() {
        Task readBook = new Todo("Read Book");

        List<Task> matchingTasks = Arin.findTasks(List.of(readBook), "bOoK");

        assertEquals(List.of(readBook), matchingTasks);
    }

    @Test
    void findTasks_noMatchingDescriptions_returnsEmptyList() {
        List<Task> matchingTasks = Arin.findTasks(List.of(new Todo("read book")), "meeting");

        assertEquals(List.of(), matchingTasks);
    }

    @Test
    void findTasks_emptyTaskList_returnsEmptyList() {
        assertEquals(List.of(), Arin.findTasks(List.of(), "book"));
    }

    @Test
    void findTasks_duplicateMatches_preservesOrderAndDuplicates() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2026, 6, 6));
        List<Task> tasks = List.of(readBook, new Todo("buy milk"), returnBook, readBook);

        assertEquals(List.of(readBook, returnBook, readBook), Arin.findTasks(tasks, "book"));
    }

    @Test
    void findTasks_resultListChanged_doesNotChangeSourceList() {
        Task readBook = new Todo("read book");
        List<Task> tasks = new ArrayList<>(List.of(readBook));
        List<Task> matchingTasks = Arin.findTasks(tasks, "book");

        matchingTasks.clear();

        assertEquals(List.of(readBook), tasks);
    }

    @Test
    void findTasks_turkishDefaultLocale_preservesCaseInsensitiveMatching() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Task writeReport = new Todo("WRITE report");

            assertEquals(List.of(writeReport), Arin.findTasks(List.of(writeReport), "write"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
