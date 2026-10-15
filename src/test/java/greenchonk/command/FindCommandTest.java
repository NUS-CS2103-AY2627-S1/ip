package greenchonk.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import greenchonk.command.CommandTestSupport.RecordingStorage;
import greenchonk.command.CommandTestSupport.RecordingUi;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.Todo;

class FindCommandTest {
    @Test
    void execute_matchingDescriptions_matchesShownWithoutSaving() {
        Task matchingTask = new Todo("Read Book");
        TaskList tasks = new TaskList(List.of(
                new Todo("buy milk"), matchingTask));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new FindCommand("BOOK").execute(tasks, ui, storage);

        assertTrue(ui.isFindHeaderShown());
        assertFalse(ui.isNoMatchingTasksShown());
        assertEquals(List.of(2), ui.getShownTaskNumbers());
        assertEquals(1, ui.getShownTasks().size());
        assertSame(matchingTask, ui.getShownTasks().get(0));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void execute_noMatchingDescription_emptyMatchesShownWithoutSaving() {
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new FindCommand("missing").execute(
                new TaskList(List.of(new Todo("buy milk"))), ui, storage);

        assertFalse(ui.isFindHeaderShown());
        assertTrue(ui.isNoMatchingTasksShown());
        assertTrue(ui.getShownTasks().isEmpty());
        assertEquals(0, storage.getSaveCount());
    }
}
