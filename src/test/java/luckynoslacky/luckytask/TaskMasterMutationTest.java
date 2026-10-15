package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckystorage.CsvSaver;

/** Tests status changes and deletion behavior provided by {@link TaskMaster}. */
class TaskMasterMutationTest {
    private static final LocalDateTime DEADLINE =
            LocalDateTime.of(2026, 12, 6, 23, 59);
    private static final LocalDateTime EVENT_START =
            LocalDateTime.of(2026, 8, 6, 14, 0);
    private static final LocalDateTime EVENT_END =
            LocalDateTime.of(2026, 8, 6, 16, 0);

    @TempDir
    Path temporaryDirectory;

    /** Verifies that marking a valid task updates its status. */
    @Test
    void markTaskDone_validTask_marksTaskAsDone() {
        TaskMaster taskMaster = createTaskMaster();

        taskMaster.addTask(new TodoTask("read book"));
        taskMaster.addTask(new TodoTask("return book"));
        taskMaster.markTaskDone(2);

        assertEquals("Nah, all these things you need to do:\n"
                        + "1.[📌][❗] read book\n"
                        + "2.[📌][✅] return book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that unmarking a done task clears its status. */
    @Test
    void unmarkTaskUndone_doneTask_marksTaskAsUndone() {
        TaskMaster taskMaster = createTaskMaster();

        taskMaster.addTask(new TodoTask("read book"));
        taskMaster.markTaskDone(1);
        taskMaster.unmarkTaskUndone(1);

        assertEquals("Nah, all these things you need to do:\n1.[📌][❗] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that marking an already done task is idempotent. */
    @Test
    void markTaskDone_alreadyDoneTask_remainsDone() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));

        taskMaster.markTaskDone(1);
        taskMaster.markTaskDone(1);

        assertEquals("Nah, all these things you need to do:\n1.[📌][✅] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that unmarking an incomplete task is idempotent. */
    @Test
    void unmarkTaskUndone_alreadyUndoneTask_remainsUndone() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));

        taskMaster.unmarkTaskUndone(1);
        taskMaster.unmarkTaskUndone(1);

        assertEquals("Nah, all these things you need to do:\n1.[📌][❗] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that deletion removes and renumbers later tasks. */
    @Test
    void deleteTask_middleTask_removesAndRenumbersTasks() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("first"));
        taskMaster.addTask(new DeadlineTask("second", DEADLINE));
        taskMaster.addTask(new EventTask("third", EVENT_START, EVENT_END));

        assertEquals("second", taskMaster.deleteTask(2).getDescription());
        assertEquals("Nah, all these things you need to do:\n"
                        + "1.[📌][❗] first\n"
                        + "2.[📆][❗] third (from: Thu Aug 06 2026, 2.00pm"
                        + " to: Thu Aug 06 2026, 4.00pm)",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that an invalid deletion leaves the list unchanged. */
    @Test
    void deleteTask_invalidTaskNumber_preservesTaskList() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> taskMaster.deleteTask(2));
        assertEquals("Invalid task number.", exception.getMessage());
        assertEquals("Nah, all these things you need to do:\n1.[📌][❗] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that deletion frees capacity for a later task. */
    @Test
    void deleteTask_fullList_freesCapacity() {
        TaskMaster taskMaster = createTaskMaster(2);
        taskMaster.addTask(new TodoTask("first"));
        taskMaster.addTask(new TodoTask("second"));

        taskMaster.deleteTask(1);
        taskMaster.addTask(new TodoTask("third"));

        assertEquals("Nah, all these things you need to do:\n"
                        + "1.[📌][❗] second\n"
                        + "2.[📌][❗] third",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that deletion from an empty list is rejected. */
    @Test
    void deleteTask_emptyList_throwsIllegalArgumentException() {
        TaskMaster taskMaster = createTaskMaster();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> taskMaster.deleteTask(1));
        assertEquals("Invalid task number.", exception.getMessage());
    }

    /** Verifies that invalid numbers are rejected when marking tasks. */
    @Test
    void markTaskDone_invalidTaskNumber_throwsIllegalArgumentException() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> taskMaster.markTaskDone(0));
        assertEquals("Invalid task number.", exception.getMessage());
        exception = assertThrows(
                IllegalArgumentException.class, () -> taskMaster.markTaskDone(2));
        assertEquals("Invalid task number.", exception.getMessage());
    }

    /** Verifies that invalid numbers are rejected when unmarking tasks. */
    @Test
    void unmarkTaskUndone_invalidTaskNumber_throwsIllegalArgumentException() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> taskMaster.unmarkTaskUndone(0));
        assertEquals("Invalid task number.", exception.getMessage());
        exception = assertThrows(
                IllegalArgumentException.class, () -> taskMaster.unmarkTaskUndone(2));
        assertEquals("Invalid task number.", exception.getMessage());
    }

    /** Verifies invalid status changes do not alter another task's state. */
    @Test
    void markOrUnmark_invalidTaskNumber_preservesTaskState() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));

        assertThrows(IllegalArgumentException.class, () -> taskMaster.markTaskDone(2));
        assertEquals("Nah, all these things you need to do:\n1.[📌][❗] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());

        taskMaster.markTaskDone(1);
        assertThrows(IllegalArgumentException.class, () -> taskMaster.unmarkTaskUndone(2));
        assertEquals("Nah, all these things you need to do:\n1.[📌][✅] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Creates a task master with the default test capacity. */
    private TaskMaster createTaskMaster() {
        return createTaskMaster(100);
    }

    /** Creates a task master with a temporary backing file and capacity. */
    private TaskMaster createTaskMaster(int maxTasks) {
        return new TaskMaster(
                maxTasks,
                new CsvSaver(temporaryDirectory.resolve("tasks.csv")));
    }
}
