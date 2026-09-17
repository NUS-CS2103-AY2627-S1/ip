package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the completion status displayed for a task. */
class TaskTest {
    @Test
    void getStatusIcon_newTask_returnsBlankIcon() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getStatusIcon_completedTask_returnsXIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void getStatusIcon_taskMarkedNotDoneAgain_returnsBlankIcon() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void markAsDone_incompleteTask_changesStatusToDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_completedTask_changesStatusToIncomplete() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void restoreStatus_doneStatus_restoresDoneIcon() {
        Task task = new Task("read book");

        task.restoreStatus(true);

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void restoreStatus_notDoneStatus_restoresBlankIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        task.restoreStatus(false);

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void getDescription_task_returnsOriginalDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }

    @Test
    void toString_incompleteTask_returnsTypeStatusAndDescription() {
        Task task = new Task("read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void toString_completedTask_returnsDoneStatusAndDescription() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[T][X] read book", task.toString());
    }
}
