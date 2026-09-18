package aster.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList#find(String)}, the keyword search behind the {@code find}
 * command, and {@link TaskList#getStatistics()}, the counting behind {@code stats}.
 *
 * <p>The cases below pin down every part of its promise: which text it searches, that
 * it ignores letter case, that it takes the keyword literally rather than as a pattern,
 * the order and multiplicity of what it returns, and that searching changes nothing.
 *
 * <p>Matches are compared by description rather than by task identity, so a failure
 * reports which tasks came back rather than only how many.
 */
class TaskListTest {

    // ---------- what it matches ----------

    @Test
    void find_keywordInDescription_returnsMatchingTasks() {
        TaskList tasks = todosOf("read book", "wash up", "return book");

        assertEquals(List.of("read book", "return book"), descriptionsOf(tasks.find("book")));
    }

    @Test
    void find_differentCaseKeyword_returnsMatchingTasks() {
        TaskList tasks = todosOf("Read Book");

        assertEquals(List.of("Read Book"), descriptionsOf(tasks.find("bOOk")));
    }

    @Test
    void find_keywordWithSurroundingWhitespace_returnsMatchingTasks() {
        TaskList tasks = todosOf("read book");

        assertEquals(List.of("read book"), descriptionsOf(tasks.find("  book \t")));
    }

    @Test
    void find_substringInsideWord_returnsMatchingTasks() {
        // Proves the match is a plain substring rather than a whole word.
        TaskList tasks = todosOf("read book");

        assertEquals(List.of("read book"), descriptionsOf(tasks.find("oo")));
    }

    @Test
    void find_multiWordKeyword_matchesAcrossWords() {
        TaskList tasks = todosOf("project meeting", "project");

        assertEquals(List.of("project meeting"), descriptionsOf(tasks.find("project meet")));
    }

    @Test
    void find_duplicateDescriptions_returnsEveryMatchingTask() {
        TaskList tasks = todosOf("read book", "read book");

        assertEquals(List.of("read book", "read book"), descriptionsOf(tasks.find("read")));
    }

    // ---------- what it refuses to match ----------

    @Test
    void find_noMatch_returnsEmptyList() {
        TaskList tasks = todosOf("read book", "wash up");

        assertTrue(tasks.find("bicycle").isEmpty());
    }

    @Test
    void find_emptyList_returnsEmptyList() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    @Test
    void find_regexSpecialCharacters_matchesLiterally() {
        // A regex reading of "a.b" would also match "axb", so this fails if the search
        // ever stops treating the keyword as plain text.
        TaskList tasks = todosOf("a.b", "axb");

        assertEquals(List.of("a.b"), descriptionsOf(tasks.find("a.b")));
    }

    @Test
    void find_statusTypeOrDateText_returnsEmptyList() {
        // The display form of this task is "[D][X] return book (by: Jun 06 2019)", so a
        // search over the whole rendered line would match all three of these.
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        deadline.markAsDone();
        TaskList tasks = new TaskList(List.of(deadline));

        assertTrue(tasks.find("[D]").isEmpty());
        assertTrue(tasks.find("Jun").isEmpty());
        assertTrue(tasks.find("X").isEmpty());
    }

    // ---------- what it promises about the list and the result ----------

    @Test
    void find_anyKeyword_leavesListUnchanged() {
        TaskList tasks = todosOf("read book", "wash up");

        tasks.find("book");
        tasks.find("nothing here");

        assertEquals(2, tasks.size());
        assertEquals(List.of("read book", "wash up"), descriptionsOf(tasks.asList()));
    }

    @Test
    void find_result_isUnmodifiable() {
        TaskList tasks = todosOf("read book");
        List<Task> matches = tasks.find("book");

        assertThrows(UnsupportedOperationException.class, () -> matches.add(new Todo("sneak in")));
    }

    // ---------- getStatistics ----------

    @Test
    void getStatistics_emptyList_returnsAllZero() {
        assertEquals(new TaskStatistics(0, 0, 0, 0, 0), new TaskList().getStatistics());
    }

    @Test
    void getStatistics_oneTodo_countsOneTodoNotCompleted() {
        assertEquals(new TaskStatistics(1, 0, 1, 0, 0), todosOf("read book").getStatistics());
    }

    @Test
    void getStatistics_mixedTypesSomeDone_countsEachFigure() {
        TaskList tasks = mixedList();
        tasks.get(0).markAsDone();
        tasks.get(2).markAsDone();

        assertEquals(new TaskStatistics(5, 2, 2, 2, 1), tasks.getStatistics());
    }

    @Test
    void getStatistics_noneDone_countsNoneCompleted() {
        assertEquals(0, mixedList().getStatistics().completed());
    }

    @Test
    void getStatistics_allDone_countsAllCompleted() {
        TaskList tasks = mixedList();
        for (Task task : tasks.asList()) {
            task.markAsDone();
        }

        assertEquals(5, tasks.getStatistics().completed());
    }

    @Test
    void getStatistics_anyList_leavesListUnchanged() {
        TaskList tasks = mixedList();
        tasks.get(1).markAsDone();
        List<Task> before = List.copyOf(tasks.asList());

        tasks.getStatistics();

        assertEquals(before, tasks.asList());
        assertEquals(List.of(false, true, false, false, false), doneFlagsOf(tasks.asList()));
    }

    // ---------- helpers ----------

    /**
     * Returns a task list holding one todo per description, in the order given.
     *
     * @param descriptions the descriptions to build todos from.
     * @return a list holding those todos.
     */
    private static TaskList todosOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new Todo(description));
        }
        return tasks;
    }

    /**
     * Returns the descriptions of the given tasks, in order.
     *
     * @param tasks the tasks to read.
     * @return their descriptions, so a failure names the tasks that came back.
     */
    private static List<String> descriptionsOf(List<Task> tasks) {
        List<String> descriptions = new ArrayList<>();
        for (Task task : tasks) {
            descriptions.add(task.getDescription());
        }
        return descriptions;
    }

    /**
     * Returns a list of two todos, two deadlines and one event, none of them done, in an
     * order that interleaves the types.
     *
     * @return the list.
     */
    private static TaskList mixedList() {
        return new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 6, 6)),
                new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 8)),
                new Todo("wash up"),
                new Deadline("submit report", LocalDate.of(2019, 9, 1))));
    }

    /**
     * Returns whether each of the given tasks is done, in order.
     *
     * @param tasks the tasks to read.
     * @return their done flags, so a failure names which task changed.
     */
    private static List<Boolean> doneFlagsOf(List<Task> tasks) {
        List<Boolean> flags = new ArrayList<>();
        for (Task task : tasks) {
            flags.add(task.isDone());
        }
        return flags;
    }
}
