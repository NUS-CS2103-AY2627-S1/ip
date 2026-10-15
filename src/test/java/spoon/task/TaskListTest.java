package spoon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spoon.exception.DuplicateTaskException;

public class TaskListTest {

    private TaskList taskList;
    private ToDo todo1;
    private ToDo todo2;
    private Deadline deadline1;
    private Event event1;

    @BeforeEach
    void setUp() throws Exception {
        taskList = new TaskList();
        todo1 = new ToDo("read book");
        todo2 = new ToDo("write report");
        deadline1 = new Deadline("submit essay", "01/01/0001");
        event1 = new Event("orientation", "01/01/0001 0001", "02/01/0001 0101");

        taskList.add(todo1);
        taskList.add(todo2);
        taskList.add(deadline1);
        taskList.add(event1);
    }

    @Nested
    class AddTests {

        @Test
        void add_validTask_increasesSize() throws DuplicateTaskException {
            int initialSize = taskList.size();
            taskList.add(new ToDo("new unique task"));
            assertEquals(initialSize + 1, taskList.size());
        }

        @Test
        void add_duplicateTask_throwsDuplicateTaskException() {
            assertThrows(DuplicateTaskException.class, () -> taskList.add(new ToDo("read book")));
        }

        @Test
        void add_nullTask_throwsAssertionError() {
            assertThrows(AssertionError.class, () -> taskList.add(null));
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void delete_validIndex_removesTaskAndDecreasesSize() {
            int initialSize = taskList.size();
            taskList.delete(0);
            assertEquals(initialSize - 1, taskList.size());
            assertEquals(todo2, taskList.get(0));
        }

        @Test
        void delete_outOfBoundsIndex_throwsAssertionError() {
            assertThrows(AssertionError.class, () -> taskList.delete(-1));
            assertThrows(AssertionError.class, () -> taskList.delete(taskList.size()));
        }
    }

    @Nested
    class GetTests {

        @Test
        void get_validIndex_returnsCorrectTask() {
            assertEquals(todo1, taskList.get(0));
            assertEquals(deadline1, taskList.get(2));
        }

        @Test
        void get_outOfBoundsIndex_throwsAssertionError() {
            assertThrows(AssertionError.class, () -> taskList.get(-1));
            assertThrows(AssertionError.class, () -> taskList.get(taskList.size()));
        }
    }

    @Nested
    class DateFilterTests {

        @Test
        void getTasksOn_matchingDate_returnsMatchingTasks() {
            LocalDate queryDate = LocalDate.of(1, 1, 1);
            TaskList result = taskList.getTasksOn(queryDate);

            // Matches deadline1 (due on 01/01/0001) and event1 (occurs 01/01/0001 to 02/01/0001)
            assertEquals(2, result.size());
            assertEquals(deadline1, result.get(0));
            assertEquals(event1, result.get(1));
        }

        @Test
        void getTasksOn_nonMatchingDate_returnsEmptyList() {
            LocalDate nonMatchingDate = LocalDate.of(1, 1, 5);
            TaskList result = taskList.getTasksOn(nonMatchingDate);
            assertEquals(0, result.size());
        }

        @Test
        void getTasksBy_matchingDate_returnsTasksDueBeforeOrOnDate() {
            LocalDate queryDate = LocalDate.of(1, 1, 1);
            TaskList result = taskList.getTasksBy(queryDate);

            assertEquals(2, result.size());
            assertEquals(deadline1, result.get(0));
            assertEquals(event1, result.get(1));
        }

        @Test
        void getTasksBy_priorDate_returnsEmptyList() {
            LocalDate priorDate = LocalDate.of(1, 1, 1).minusDays(1);
            TaskList result = taskList.getTasksBy(priorDate);
            assertEquals(0, result.size());
        }
    }

    @Nested
    class FindTasksTests {

        @Test
        void findTasks_singleMatchingKeyword_returnsMatchedTasks() {
            TaskList result = taskList.findTasks("book");
            assertEquals(1, result.size());
            assertEquals(todo1, result.get(0));
        }

        @Test
        void findTasks_multipleKeywords_matchesAnyKeywordWithoutDuplicates() {
            // "book" matches todo1, "essay" matches deadline1
            TaskList result = taskList.findTasks("book", "essay");
            assertEquals(2, result.size());
            assertEquals(todo1, result.get(0));
            assertEquals(deadline1, result.get(1));
        }

        @Test
        void findTasks_multipleKeywordsMatchingSameTask_addsTaskOnlyOnce() {
            // Both "read" and "book" match todo1 ("read book")
            TaskList result = taskList.findTasks("read", "book");
            assertEquals(1, result.size());
            assertEquals(todo1, result.get(0));
        }

        @Test
        void findTasks_nonExistentKeyword_returnsEmptyList() {
            TaskList result = taskList.findTasks("nonexistentword");
            assertEquals(0, result.size());
        }
    }
}
