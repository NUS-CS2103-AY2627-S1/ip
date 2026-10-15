package duchess.tasks.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duchess.tasks.Task;
import duchess.tasks.exceptions.TaskException;


/** Tests command parsing and the persisted representations produced by {@link TaskFactory}. */
class TaskFactoryTest {

    @Test
    void createFromCommand_todoCommand_returnsExpectedStorageFormat() throws TaskException {
        Task task = TaskFactory.createFromCommand("todo read book");

        assertEquals("0 | read book | T |", task.getStorageFormat());
    }

    @Test
    void createFromCommand_deadlineCommand_returnsExpectedStorageFormat() throws TaskException {
        Task task = TaskFactory.createFromCommand("deadline submit assignment /by 2024-12-30");

        assertEquals("0 | submit assignment | D | /by 2024-12-30", task.getStorageFormat());
    }

    @Test
    void createFromCommand_eventCommand_returnsExpectedStorageFormat() throws TaskException {
        Task task = TaskFactory.createFromCommand("event team meeting /from 2024-10-15 /to 2024-10-16");

        assertEquals("0 | team meeting | E | /from 2024-10-15 /to 2024-10-16",
                task.getStorageFormat());
    }

    @Test
    void createFromCommand_recurringCommand_returnsExpectedStorageFormat()
            throws TaskException {
        Task task = TaskFactory.createFromCommand(
                "recurring take medication /by 2024-10-15 /repeat 2 days");

        assertEquals("0 | take medication | R | /by 2024-10-15 /repeat 2 days",
                task.getStorageFormat());
    }

    @Test
    void createFromCommand_recurringMarkersReversed_returnsExpectedStorageFormat()
            throws TaskException {
        Task task = TaskFactory.createFromCommand(
                "recurring take medication /repeat 2 days /by 2024-10-15");

        assertEquals("0 | take medication | R | /by 2024-10-15 /repeat 2 days",
                task.getStorageFormat());
    }

    @Test
    void createFromCommand_recurringUnsupportedUnit_throwsTaskException() {
        String recurringCreator =
                "recurring take medication /by 2024-10-15 /repeat 2 weeks";

        assertThrows(TaskException.class, () ->
                TaskFactory.createFromCommand(recurringCreator));
    }

    @Test
    void createFromCommand_eventFromDateAfterToDate_throwsTaskException() {
        String eventCreator = "event test /from 2027-12-12 /to 2020-12-12";
        assertThrows(TaskException.class, () -> TaskFactory.createFromCommand(eventCreator));

    }

    @Test
    void createDeadlineFromCommand_deadlineInvalidDateFormat_throwsTaskException() {
        String deadlineCreator = "deadline a /by wrong date";
        assertThrows(TaskException.class, () -> TaskFactory.createFromCommand(deadlineCreator));
    }
}
