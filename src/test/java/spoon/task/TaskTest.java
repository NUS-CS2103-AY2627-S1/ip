package spoon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TaskTest {

    /**
     * Concrete subclass stub to test abstract Task methods.
     */
    private static class DummyTask extends Task {
        public DummyTask(String name) {
            super(name);
        }
    }

    private Task task;

    @BeforeEach
    void setUp() {
        task = new DummyTask("Read book");
    }

    @Nested
    class CompletionStatusTests {

        @Test
        void complete_uncompletedTask_marksAsCompleted() {
            task.complete();
            assertTrue(task.format().startsWith("1 | "));
            assertTrue(task.toString().contains("[X]"));
        }

        @Test
        void uncomplete_completedTask_marksAsUncompleted() {
            task.complete();
            task.uncomplete();
            assertTrue(task.format().startsWith("0 | "));
            assertTrue(task.toString().contains("[ ]"));
        }
    }

    @Nested
    class DefaultDateEvaluationTests {

        @Test
        void isDueOn_defaultTask_returnsFalse() {
            assertFalse(task.isDueOn(LocalDate.of(1, 1, 1)));
        }

        @Test
        void isDueBy_defaultTask_returnsFalse() {
            assertFalse(task.isDueBy(LocalDate.of(1, 1, 1)));
        }
    }

    @Nested
    class ContainsWordTests {

        @Test
        void containsWord_exactMatch_returnsTrue() {
            assertTrue(task.containsWord("Read"));
        }

        @Test
        void containsWord_caseInsensitiveMatch_returnsTrue() {
            assertTrue(task.containsWord("read"));
            assertTrue(task.containsWord("BOOK"));
        }

        @Test
        void containsWord_partialSubstringMatch_returnsTrue() {
            assertTrue(task.containsWord("ea"));
        }

        @Test
        void containsWord_nonMatchingWord_returnsFalse() {
            assertFalse(task.containsWord("write"));
        }
    }

    @Nested
    class FormattingTests {

        @Test
        void format_uncompletedTask_returnsZeroStatusWithDescription() {
            assertEquals("0 | Read book", task.format());
        }

        @Test
        void format_completedTask_returnsOneStatusWithDescription() {
            task.complete();
            assertEquals("1 | Read book", task.format());
        }

        @Test
        void toString_uncompletedTask_includesUncompletedSymbol() {
            assertEquals("[ ] Read book", task.toString());
        }

        @Test
        void toString_completedTask_includesCompletedSymbol() {
            task.complete();
            assertEquals("[X] Read book", task.toString());
        }
    }

    @Nested
    class EqualityAndHashCodeTests {

        @Test
        void equals_sameInstance_returnsTrue() {
            assertEquals(task, task);
        }

        @Test
        void equals_differentInstanceSameDescription_returnsTrue() {
            Task otherTask = new DummyTask("Read book");
            assertEquals(task, otherTask);
            assertEquals(task.hashCode(), otherTask.hashCode());
        }

        @Test
        void equals_caseInsensitiveDescription_returnsTrue() {
            Task otherTask = new DummyTask("READ BOOK");
            assertEquals(task, otherTask);
            assertEquals(task.hashCode(), otherTask.hashCode());
        }

        @Test
        void equals_differentDescription_returnsFalse() {
            Task otherTask = new DummyTask("Write essay");
            assertNotEquals(task, otherTask);
        }

        @Test
        void equals_nullOrDifferentType_returnsFalse() {
            assertNotEquals(null, task);
            assertNotEquals("Read book", task);
        }
    }
}
