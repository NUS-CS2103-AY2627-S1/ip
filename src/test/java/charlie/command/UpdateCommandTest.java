package charlie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charlie.exception.CharlieException;
import charlie.storage.Storage;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.task.Todo;
import charlie.ui.Ui;

public class UpdateCommandTest {
    @Test
    public void execute_validDescription_replacesPersistsAndDisplaysTask(
            @TempDir Path temporaryDirectory) throws IOException {
        Task originalTask = new Todo("borrow book", false);
        TaskList tasks = new TaskList(List.of(originalTask));
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Storage storage = new Storage(saveFile.toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        UpdateCommand command = new UpdateCommand(0, UpdateField.DESCRIPTION, "borrow library book");

        command.execute(tasks, ui, storage);

        Task updatedTask = tasks.get(0);
        assertNotSame(originalTask, updatedTask);
        assertEquals("[T][ ] borrow library book", updatedTask.toString());
        assertEquals(
                "T | Not done | borrow library book" + System.lineSeparator(),
                Files.readString(saveFile));
        assertEquals(
                List.of("All changed! Here's the updated task:", "  [T][ ] borrow library book"),
                messages);
    }

    @Test
    public void execute_validDeadline_replacesDateAndPreservesStatus(
            @TempDir Path temporaryDirectory) throws IOException {
        Task originalTask = new Deadline("submit report", true, LocalDate.of(2026, 9, 18));
        TaskList tasks = new TaskList(List.of(originalTask));
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Storage storage = new Storage(saveFile.toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        UpdateCommand command = new UpdateCommand(0, UpdateField.DEADLINE, "2026-09-20");

        command.execute(tasks, ui, storage);

        Task updatedTask = tasks.get(0);
        assertNotSame(originalTask, updatedTask);
        assertEquals("[D][X] submit report (by: Sep 20 2026)", updatedTask.toString());
        assertEquals(
                "D | Done | submit report | 2026-09-20" + System.lineSeparator(),
                Files.readString(saveFile));
        assertEquals(
                List.of("All changed! Here's the updated task:", "  [D][X] submit report (by: Sep 20 2026)"),
                messages);
    }

    @Test
    public void execute_validEventStart_replacesStartAndPreservesStatus(
            @TempDir Path temporaryDirectory) throws IOException {
        Task originalTask = new Event(
                "project meeting",
                true,
                LocalDateTime.of(2026, 9, 18, 9, 0),
                LocalDateTime.of(2026, 9, 18, 10, 0));
        TaskList tasks = new TaskList(List.of(originalTask));
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Storage storage = new Storage(saveFile.toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        UpdateCommand command = new UpdateCommand(0, UpdateField.FROM, "2026-09-18 0830");

        command.execute(tasks, ui, storage);

        Task updatedTask = tasks.get(0);
        assertNotSame(originalTask, updatedTask);
        assertEquals(
                "[E][X] project meeting (from: Sep 18 2026, 8:30 AM to: Sep 18 2026, 10:00 AM)",
                updatedTask.toString());
        assertEquals(
                "E | Done | project meeting | 2026-09-18T08:30 | 2026-09-18T10:00"
                        + System.lineSeparator(),
                Files.readString(saveFile));
    }

    @Test
    public void execute_validEventEnd_replacesEndAndPreservesStatus(
            @TempDir Path temporaryDirectory) throws IOException {
        Task originalTask = new Event(
                "project meeting",
                true,
                LocalDateTime.of(2026, 9, 18, 9, 0),
                LocalDateTime.of(2026, 9, 18, 10, 0));
        TaskList tasks = new TaskList(List.of(originalTask));
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Storage storage = new Storage(saveFile.toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        UpdateCommand command = new UpdateCommand(0, UpdateField.TO, "2026-09-18 1030");

        command.execute(tasks, ui, storage);

        Task updatedTask = tasks.get(0);
        assertNotSame(originalTask, updatedTask);
        assertEquals(
                "[E][X] project meeting (from: Sep 18 2026, 9:00 AM to: Sep 18 2026, 10:30 AM)",
                updatedTask.toString());
        assertEquals(
                "E | Done | project meeting | 2026-09-18T09:00 | 2026-09-18T10:30"
                        + System.lineSeparator(),
                Files.readString(saveFile));
    }

    @Test
    public void execute_storageFailure_keepsOriginalTaskAndDisplaysNothing(
            @TempDir Path temporaryDirectory) throws IOException {
        Task originalTask = new Todo("borrow book", false);
        TaskList tasks = new TaskList(List.of(originalTask));
        Path blockedParent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockedParent, "blocks directory creation");
        Storage storage = new Storage(blockedParent.resolve("charlie.txt").toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        UpdateCommand command = new UpdateCommand(0, UpdateField.DESCRIPTION, "borrow library book");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("Could not save tasks.", exception.getMessage());
        assertSame(originalTask, tasks.get(0));
        assertTrue(messages.isEmpty());
    }
}
