package zucc.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zucc.ZuccException;

/**
 * Tests task state transitions and the shared persistence format.
 */
public class TaskTest {
    private Todo todo;

    /**
     * Creates an incomplete to-do used by the task state-transition tests.
     *
     * @throws ZuccException if the valid task fixture cannot be created.
     */
    @BeforeEach
    public void setUp() throws ZuccException {
        todo = new Todo("Read testing chapter");
    }

    /**
     * Verifies that marking an incomplete task changes its status to done.
     *
     * @throws ZuccException if the valid state transition cannot be completed.
     */
    @Test
    public void markAsDone_incompleteTask_statusChangedToDone() throws ZuccException {
        todo.markAsDone();

        assertEquals("X", todo.getStatusIcon());
    }

    /**
     * Verifies that marking an already completed task is rejected.
     *
     * @throws ZuccException if the fixture task cannot first be marked.
     */
    @Test
    public void markAsDone_completedTask_exceptionThrown() throws ZuccException {
        todo.markAsDone();

        assertThrows(ZuccException.class, todo::markAsDone);
    }

    /**
     * Verifies that unmarking a completed task changes its status to incomplete.
     *
     * @throws ZuccException if either valid state transition cannot be completed.
     */
    @Test
    public void markAsNotDone_completedTask_statusChangedToIncomplete() throws ZuccException {
        todo.markAsDone();

        todo.markAsNotDone();

        assertEquals(" ", todo.getStatusIcon());
    }

    /**
     * Verifies that unmarking an already incomplete task is rejected.
     */
    @Test
    public void markAsNotDone_incompleteTask_exceptionThrown() {
        assertThrows(ZuccException.class, todo::markAsNotDone);
    }

    /**
     * Verifies that the base convenience constructor creates a task with no priority marker.
     *
     * @throws ZuccException if the valid task description is rejected.
     */
    @Test
    public void constructor_descriptionOnly_taskHasNoPriorityMarker() throws ZuccException {
        Task task = new BasicTask("Read chapter");

        assertEquals("[ ] Read chapter", task.toString());
    }

    /**
     * Verifies that storage separators and escape characters are encoded safely.
     *
     * @throws ZuccException if the valid task cannot be created or marked.
     */
    @Test
    public void toStorageString_specialStorageCharacters_charactersEscaped()
            throws ZuccException {
        Todo task = new Todo("Compare A | B at 100% confidence");
        task.markAsDone();

        assertEquals(
                "T | 1 | 0 | Compare A %7C B at 100%25 confidence",
                task.toStorageString());
    }

    /**
     * Verifies that every task subtype retains its type, data, and status after storage.
     *
     * @throws ZuccException if a valid fixture cannot be created or reconstructed.
     */
    @Test
    public void storageRoundTrip_eachTaskType_dataAndStatusPreserved() throws ZuccException {
        Todo todoTask = new Todo("Use literal %7C and | symbols", Priority.HIGH);
        Deadline deadlineTask = new Deadline(
                "Submit report", "2/9/2026 1800", Priority.MEDIUM);
        Event eventTask = new Event(
                "Attend workshop", "3/9/2026 0900", "4/9/2026 1700", Priority.LOW);
        deadlineTask.markAsDone();

        assertAll(
                () -> assertStorageRoundTrip(todoTask, Todo.class),
                () -> assertStorageRoundTrip(deadlineTask, Deadline.class),
                () -> assertStorageRoundTrip(eventTask, Event.class));
    }

    /**
     * Verifies that an unsupported stored priority is rejected.
     */
    @Test
    public void fromStorageString_invalidPriority_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> Task.fromStorageString("T | 0 | urgent | Read chapter"));
    }

    /**
     * Verifies that storage data with an unknown task type is rejected.
     */
    @Test
    public void fromStorageString_unknownType_exceptionThrown() {
        assertThrows(ZuccException.class,
                () -> Task.fromStorageString("X | 0 | unsupported"));
    }

    /**
     * Verifies that incomplete fields and invalid completion states are rejected.
     */
    @Test
    public void fromStorageString_invalidFieldCountOrStatus_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class,
                        () -> Task.fromStorageString("T | 0")),
                () -> assertThrows(ZuccException.class,
                        () -> Task.fromStorageString("D | maybe | submit | 2/9/2026 1800")),
                () -> assertThrows(ZuccException.class,
                        () -> Task.fromStorageString(
                                "E | 0 | workshop | 3/9/2026 0900")));
    }

    /** Verifies that subtype storage constructors reject a missing field array. */
    @Test
    public void storageConstructor_nullFields_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class, () -> new Todo((String[]) null)),
                () -> assertThrows(ZuccException.class, () -> new Deadline((String[]) null)));
    }

    /**
     * Verifies that saving and loading one task preserves its concrete type and data.
     *
     * @param original task to save and load.
     * @param expectedType concrete type expected after loading.
     * @throws ZuccException if the stored representation cannot be loaded.
     */
    private void assertStorageRoundTrip(Task original, Class<? extends Task> expectedType)
            throws ZuccException {
        Task restored = Task.fromStorageString(original.toStorageString());

        assertInstanceOf(expectedType, restored);
        assertEquals(original.toStorageString(), restored.toStorageString());
        assertEquals(original.toString(), restored.toString());
    }

    /** Minimal concrete task used to exercise behavior implemented entirely by Task. */
    private static final class BasicTask extends Task {
        /** Creates an undated task through Task's description-only constructor. */
        private BasicTask(String description) throws ZuccException {
            super(description);
        }

        @Override
        public boolean occursOn(LocalDate date) {
            return false;
        }

        @Override
        protected String[] getStorageFields() {
            return new String[] {"B"};
        }
    }
}
