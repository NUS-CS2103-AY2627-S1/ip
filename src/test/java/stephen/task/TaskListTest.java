package stephen.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list operations involving task descriptions. */
class TaskListTest {
    /** Verifies that a search can return one matching task. */
    @Test
    void findOneMatchingTaskReturnsMatch() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy groceries")));

        assertEquals(List.of("read book"), descriptions(tasks.find("read")));
    }

    /** Verifies that multiple matches retain their original task order. */
    @Test
    void findMultipleMatchesPreservesOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2024, 6, 15)),
                new Todo("buy groceries")));

        assertEquals(List.of("read book", "return book"), descriptions(tasks.find("book")));
    }

    /** Verifies that an unmatched phrase produces an empty result. */
    @Test
    void findNoMatchingTaskReturnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("groceries"));
    }

    /** Verifies that description matching ignores capitalization. */
    @Test
    void findDifferentCapitalizationMatchesDescription() {
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));

        assertEquals(List.of("Read Book"), descriptions(tasks.find("rEaD bOoK")));
    }

    /** Verifies that all words in a search phrase are matched together. */
    @Test
    void findMultiWordPhraseMatchesCompletePhrase() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read project book"),
                new Todo("read book tonight")));

        assertEquals(List.of("read book tonight"), descriptions(tasks.find("read book")));
    }

    /** Verifies that repeated occurrences do not duplicate a matching task. */
    @Test
    void findRepeatedKeywordInDescriptionReturnsTaskOnce() {
        TaskList tasks = new TaskList(List.of(new Todo("book book book")));

        assertEquals(List.of("book book book"), descriptions(tasks.find("book")));
    }

    /** Verifies case-insensitive stable sorting across task types. */
    @Test
    void sortByDescriptionMixedCaseAndEqualKeysSortedStably() {
        TaskList tasks = new TaskList(List.of(
                new Todo("zebra"),
                new Deadline("Alpha", LocalDate.of(2024, 6, 15)),
                new Todo("alpha"),
                new Todo("middle")));

        tasks.sortByDescription();

        assertEquals(List.of("Alpha", "alpha", "middle", "zebra"),
                descriptions(tasks.getTasks()));
    }

    /** Verifies that sorting an empty task list is safe. */
    @Test
    void sortByDescriptionEmptyListRemainsEmpty() {
        TaskList tasks = new TaskList(List.of());

        tasks.sortByDescription();

        assertEquals(List.of(), tasks.getTasks());
    }

    /**
     * Converts matching tasks to their descriptions for concise assertions.
     *
     * @param tasks tasks to convert
     * @return task descriptions in list order
     */
    private static List<String> descriptions(List<Task> tasks) {
        return tasks.stream().map(Task::getDescription).toList();
    }
}
