package kafka.javafx.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javafx.scene.text.Text;
import kafka.parser.TaskParser;
import kafka.storage.TaskStorage;
import kafka.task.Deadline;
import kafka.task.Event;
import kafka.task.SnoozeResult;
import kafka.task.Task;
import kafka.task.TaskList;
import kafka.task.Todo;
import kafka.ui.Ui;

/**
 * Checks task styling against actual console messages and protects literal task descriptions.
 */
class MessageTextFormatterTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void formatResponse_addedTasks_preservesTextAndStylesEachType() {
        List<Task> tasks = List.of(new Todo("read book"), new Deadline("submit report", "Sunday"),
                new Event("meeting", "2pm", "4pm"));
        List<String> styles = List.of("task-todo", "task-deadline", "task-event");
        Ui ui = new Ui();
        for (int i = 0; i < tasks.size(); i++) {
            String message = ui.formatTaskAdded(tasks.get(i), i + 1);
            List<Text> segments = MessageTextFormatter.formatResponse(message);
            assertEquals(message, joinText(segments));
            String expectedStyle = styles.get(i);
            assertTrue(segments.stream().anyMatch(text -> text.getStyleClass().contains(expectedStyle)));
        }
    }

    @Test
    void formatResponse_mixedList_marksOnlyCompletedTask() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        todo.mark();
        tasks.addTask(todo);
        tasks.addTask(new Deadline("submit report", "Sunday"));
        tasks.addTask(new Event("meeting", "2pm", "4pm"));
        String message = new Ui().formatTaskList(tasks);

        List<Text> segments = MessageTextFormatter.formatResponse(message);

        assertEquals(message.replace("[X]", "[\u2713]"), joinText(segments));
        List<Text> completedMarkers = segments.stream()
                .filter(text -> text.getStyleClass().contains("task-done")).toList();
        assertEquals(1, completedMarkers.size());
        assertEquals("[\u2713]", completedMarkers.getFirst().getText());
        for (String style : List.of("task-todo", "task-deadline", "task-event")) {
            assertTrue(segments.stream().anyMatch(text -> text.getStyleClass().contains(style)));
        }
    }

    @Test
    void formatResponse_unmarkedTask_hasNoCompletedMarker() {
        Task task = new Todo("read book");
        task.mark();
        task.unmark();
        String message = new Ui().formatTaskUnmarked(task.display());

        List<Text> segments = MessageTextFormatter.formatResponse(message);

        assertEquals(message, joinText(segments));
        assertFalse(segments.stream().anyMatch(text -> text.getStyleClass().contains("task-done")));
    }

    @Test
    void formatResponse_literalMarkersAndLineBreaks_preservesContents() {
        String message = "Example [D][X] stays literal\r\n"
                + "1.[T][ ] read about [E][X] and underscores ___\r\n";

        List<Text> segments = MessageTextFormatter.formatResponse(message);

        assertEquals(message, joinText(segments));
        assertFalse(segments.stream().anyMatch(text -> text.getStyleClass().contains("task-done")));
        assertFalse(segments.stream().anyMatch(text -> text.getStyleClass().contains("task-event")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Plain reply\nwith another line", "[Q][X] unknown type", "[T][?] invalid status"})
    void formatResponse_noTaskLines_preservesPlainTextWithoutTaskStyles(String message) {
        List<Text> segments = MessageTextFormatter.formatResponse(message);

        assertEquals(message, joinText(segments));
        assertFalse(segments.stream().flatMap(text -> text.getStyleClass().stream())
                .anyMatch(style -> style.startsWith("task-")));
    }

    @Test
    void formatResponse_completedTaskWithLiteralMarker_changesOnlyStatus() {
        String message = "12.[D][X] read about [T][X] (by: Sunday)\r\n";

        List<Text> segments = MessageTextFormatter.formatResponse(message);

        assertEquals("12.[D][\u2713] read about [T][X] (by: Sunday)\r\n", joinText(segments));
        assertEquals(1, segments.stream().filter(text -> text.getStyleClass().contains("task-done")).count());
        assertFalse(segments.stream().anyMatch(text -> text.getStyleClass().contains("task-todo")));
    }

    @Test
    void formatResponse_snoozeConfirmation_stylesBothSnapshots() {
        String message = new Ui().formatTaskSnoozed("[E][X] meeting (from: Monday to: Tuesday)",
                "[E][X] meeting (from: Wednesday to: Thursday)");

        List<Text> segments = MessageTextFormatter.formatResponse(message);

        assertEquals(message.replace("[X]", "[\u2713]"), joinText(segments));
        assertEquals(2, segments.stream().filter(text -> text.getStyleClass().contains("task-done")).count());
        assertTrue(segments.stream().anyMatch(text -> text.getText().contains("Wednesday to: Thursday")
                && text.getStyleClass().contains("task-event")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-02-30", "29/2/2025", "31 Apr 2026", "2026-13-01", "24:00", "13pm"})
    void formatResponse_invalidTiming_staysRedThroughMarkUnmarkListAndReload(String timing) throws Exception {
        Task task = TaskParser.parseDeadline("deadline report /by " + timing);
        TaskList tasks = new TaskList();
        tasks.addTask(task);
        Ui ui = new Ui();
        assertInvalidValues(ui.formatTaskAdded(task, tasks.size()), List.of(timing));

        task.mark();
        assertInvalidValues(ui.formatTaskMarked(task.display()), List.of(timing));
        assertInvalidValues(ui.formatTaskList(tasks), List.of(timing));
        task.unmark();
        assertInvalidValues(ui.formatTaskUnmarked(task.display()), List.of(timing));
        assertInvalidValues(ui.formatTaskList(tasks), List.of(timing));

        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        storage.save(tasks);
        assertInvalidValues(ui.formatTaskList(storage.load()), List.of(timing));
    }

    @Test
    void formatResponse_eventWithOneImpossibleDate_colorsOnlyThatDate() throws Exception {
        Task event = TaskParser.parseEvent("event meeting /from 2026-02-30 0900 /to 2026-03-01 1000");
        Ui ui = new Ui();

        assertInvalidValues(ui.formatTaskAdded(event, 1), List.of("2026-02-30"));
        event.mark();
        assertInvalidValues(ui.formatTaskMarked(event.display()), List.of("2026-02-30"));
        event.unmark();
        assertInvalidValues(ui.formatTaskUnmarked(event.display()), List.of("2026-02-30"));
    }

    @Test
    void formatResponse_eventWithTwoImpossibleDates_colorsBothDates() throws Exception {
        Task event = TaskParser.parseEvent("event meeting /from 31 Apr 2026 /to 31 Jun 2026");
        assertInvalidValues(new Ui().formatTaskAdded(event, 1), List.of("31 Apr 2026", "31 Jun 2026"));
    }

    @Test
    void formatResponse_validAndFreeFormTiming_doesNotColorDatesInDescriptions() throws Exception {
        for (String timing : List.of("2024-02-29", "28/2/2026", "30 Apr 2026", "12am", "Sunday", "next week")) {
            Task task = TaskParser.parseDeadline("deadline review 2026-02-30 /by " + timing);
            assertInvalidValues(new Ui().formatTaskAdded(task, 1), List.of());
        }
        Task todo = new Todo("review (by: 2026-02-30)");
        assertInvalidValues(new Ui().formatTaskAdded(todo, 1), List.of());
    }

    @Test
    void formatResponse_correctedTiming_removesWarningFromNewSnapshot() throws Exception {
        TaskList tasks = new TaskList();
        tasks.addTask(TaskParser.parseDeadline("deadline report /by 2026-02-30"));
        SnoozeResult result = tasks.snoozeDeadline(1, "1 Mar 2026");
        Ui ui = new Ui();

        assertInvalidValues(ui.formatTaskSnoozed(result.oldDisplay(), result.newDisplay()), List.of("2026-02-30"));
        assertInvalidValues(ui.formatTaskList(tasks), List.of());
    }

    private void assertInvalidValues(String message, List<String> expectedValues) {
        List<Text> segments = MessageTextFormatter.formatResponse(message);
        assertEquals(message.replace("[X]", "[\u2713]"), joinText(segments));
        assertEquals(expectedValues, segments.stream()
                .filter(text -> text.getStyleClass().contains("task-invalid-date"))
                .map(Text::getText).toList());
    }

    private String joinText(List<Text> segments) {
        return segments.stream().map(Text::getText).collect(Collectors.joining());
    }
}
