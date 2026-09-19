package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.InvalidTaskNumberException;
import yuno.exception.YunoException;
import yuno.task.Deadline;
import yuno.task.Event;
import yuno.task.Todo;

class SortCommandTest extends CommandTestSupport {
    @Test
    void execute_noArguments_sortsAscendingSavesAndDisplaysTasks()
            throws YunoException, IOException {
        Todo todo = taskList.addTask("todo");
        Deadline laterDeadline = taskList.addTask(
                "later deadline", LocalDateTime.of(2026, 9, 10, 12, 0));
        Event earlierEvent = taskList.addTask(
                "earlier event",
                LocalDateTime.of(2026, 9, 8, 9, 0),
                LocalDateTime.of(2026, 9, 8, 10, 0));

        CommandResult result = new SortCommand("").execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, result);
        assertSame(earlierEvent, taskList.getTask(1));
        assertSame(laterDeadline, taskList.getTask(2));
        assertSame(todo, taskList.getTask(3));
        assertSame(taskList, ui.getDisplayedTaskList());
        assertEquals(List.of(
                earlierEvent.toStorageString(),
                laterDeadline.toStorageString(),
                todo.toStorageString()),
                Files.readAllLines(tempDir.resolve("tasks.txt")));
    }

    @Test
    void execute_descendingOrder_sortsDatedTasksDescendingAndLeavesTodoLast()
            throws YunoException {
        Todo todo = taskList.addTask("todo");
        Deadline earlierDeadline = taskList.addTask(
                "earlier deadline", LocalDateTime.of(2026, 9, 8, 9, 0));
        Deadline laterDeadline = taskList.addTask(
                "later deadline", LocalDateTime.of(2026, 9, 10, 12, 0));

        new SortCommand("/order desc").execute(taskList, ui, storage);

        assertSame(laterDeadline, taskList.getTask(1));
        assertSame(earlierDeadline, taskList.getTask(2));
        assertSame(todo, taskList.getTask(3));
    }

    @Test
    void execute_invalidArguments_throwsWithoutSorting() throws InvalidTaskNumberException {
        Deadline laterDeadline = taskList.addTask(
                "later deadline", LocalDateTime.of(2026, 9, 10, 12, 0));
        Deadline earlierDeadline = taskList.addTask(
                "earlier deadline", LocalDateTime.of(2026, 9, 8, 9, 0));
        List<String> invalidArguments = List.of(
                "asc", "/order", "/order sideways", "/order asc extra");

        for (String invalidArgument : invalidArguments) {
            assertThrows(
                    InvalidCommandFormatException.class,
                    () -> new SortCommand(invalidArgument).execute(taskList, ui, storage));
            assertSame(laterDeadline, taskList.getTask(1));
            assertSame(earlierDeadline, taskList.getTask(2));
        }
    }
}
