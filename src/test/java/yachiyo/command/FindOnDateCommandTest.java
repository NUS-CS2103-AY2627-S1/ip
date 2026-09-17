package yachiyo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yachiyo.storage.Storage;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.NumberedTask;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.task.ToDo;
import yachiyo.ui.Ui;

/**
 * Tests date-filtered display behavior of {@link FindOnDateCommand}.
 */
public class FindOnDateCommandTest {
    private static final LocalDate SEARCH_DATE = LocalDate.of(2026, 8, 20);

    @Test
    public void execute_noMatchingTasks_noTasksMessageShown() {
        Deadline nonMatchingDeadline = new Deadline(
                "Submit report",
                LocalDateTime.of(2026, 8, 21, 17, 0)
        );
        TaskList tasks = new TaskList(List.of(
                new ToDo("Read book"),
                nonMatchingDeadline
        ));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new FindOnDateCommand(SEARCH_DATE).execute(tasks, ui, storage);

        assertEquals(SEARCH_DATE, ui.noTasksDate);
        assertNull(ui.headerDate);
        assertEquals(List.of(), ui.shownTasks);
        assertEquals(0, storage.saveCallCount);
    }

    @Test
    public void execute_matchingTasks_headerAndOriginalTaskNumbersShown() {
        Deadline matchingDeadline = new Deadline(
                "Submit report",
                SEARCH_DATE.atTime(17, 0)
        );
        Deadline nonMatchingDeadline = new Deadline(
                "Pay bill",
                SEARCH_DATE.plusDays(1).atTime(12, 0)
        );
        Event matchingEvent = new Event(
                "Orientation",
                SEARCH_DATE.minusDays(1).atTime(9, 0),
                SEARCH_DATE.plusDays(1).atTime(17, 0)
        );
        TaskList tasks = new TaskList(List.of(
                new ToDo("Read book"),
                matchingDeadline,
                nonMatchingDeadline,
                matchingEvent
        ));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage();

        new FindOnDateCommand(SEARCH_DATE).execute(tasks, ui, storage);

        assertEquals(SEARCH_DATE, ui.headerDate);
        assertNull(ui.noTasksDate);
        assertEquals(
                List.of(
                        new NumberedTask(2, matchingDeadline),
                        new NumberedTask(4, matchingEvent)
                ),
                ui.shownTasks
        );
        assertEquals(0, storage.saveCallCount);
    }

    /**
     * UI test double that records date-search output calls.
     */
    private static final class RecordingUi extends Ui {
        private LocalDate headerDate;
        private LocalDate noTasksDate;
        private final List<NumberedTask> shownTasks = new ArrayList<>();

        @Override
        public void showTasksOnDateHeader(LocalDate date) {
            headerDate = date;
        }

        @Override
        public void showNoTasksOnDate(LocalDate date) {
            noTasksDate = date;
        }

        @Override
        public void showIndexedTask(int taskNumber, Task task) {
            shownTasks.add(new NumberedTask(taskNumber, task));
        }
    }

    /**
     * Storage test double that records unexpected save attempts.
     */
    private static final class RecordingStorage extends Storage {
        private int saveCallCount;

        private RecordingStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void saveTasks(List<Task> tasks) {
            saveCallCount++;
        }
    }
}
