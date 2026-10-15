package duncan.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import duncan.gui.TaskLineParser.Kind;
import duncan.gui.TaskLineParser.Segment;
import duncan.task.Deadline;
import duncan.task.Event;
import duncan.task.Task;
import duncan.task.Todo;

/**
 * Feeds the parser the real {@code toString()} of every kind of task, so that
 * changing a task's display format breaks a test here instead of quietly
 * dropping the GUI's colouring.
 */
public class TaskLineParserTest {
    private static final String DONE_GLYPH = " ✓ ";
    private static final String NOT_DONE_GLYPH = " ○ ";

    private static Task done(Task task) {
        task.markAsDone();
        return task;
    }

    private static Deadline newDeadline() {
        return new Deadline("return book", LocalDate.of(2026, 6, 6));
    }

    private static Event newEvent() {
        return new Event("project meeting", LocalDate.of(2026, 8, 6), LocalDate.of(2026, 8, 8));
    }

    @Test
    public void parse_undoneTodo_typeStatusAndDescriptionSplit() {
        List<Segment> segments = TaskLineParser.parse("1." + new Todo("read book"));

        assertEquals(List.of(
                new Segment("1.", Kind.PLAIN),
                new Segment(" T ", Kind.TODO),
                new Segment(NOT_DONE_GLYPH, Kind.NOT_DONE),
                new Segment(" read book", Kind.PLAIN)),
                segments);
    }

    @Test
    public void parse_doneTodo_doneStatusShown() {
        List<Segment> segments = TaskLineParser.parse("2." + done(new Todo("buy milk")));

        assertEquals(List.of(
                new Segment("2.", Kind.PLAIN),
                new Segment(" T ", Kind.TODO),
                new Segment(DONE_GLYPH, Kind.DONE),
                new Segment(" buy milk", Kind.PLAIN)),
                segments);
    }

    @Test
    public void parse_undoneDeadline_datesSplitOff() {
        List<Segment> segments = TaskLineParser.parse("3." + newDeadline());

        assertEquals(List.of(
                new Segment("3.", Kind.PLAIN),
                new Segment(" D ", Kind.DEADLINE),
                new Segment(NOT_DONE_GLYPH, Kind.NOT_DONE),
                new Segment(" return book ", Kind.PLAIN),
                new Segment("(by: Jun 6 2026)", Kind.DATE)),
                segments);
    }

    @Test
    public void parse_doneDeadline_doneStatusAndDatesSplitOff() {
        List<Segment> segments = TaskLineParser.parse("4." + done(newDeadline()));

        assertEquals(List.of(
                new Segment("4.", Kind.PLAIN),
                new Segment(" D ", Kind.DEADLINE),
                new Segment(DONE_GLYPH, Kind.DONE),
                new Segment(" return book ", Kind.PLAIN),
                new Segment("(by: Jun 6 2026)", Kind.DATE)),
                segments);
    }

    @Test
    public void parse_undoneEvent_bothDatesInOnePiece() {
        List<Segment> segments = TaskLineParser.parse("5." + newEvent());

        assertEquals(List.of(
                new Segment("5.", Kind.PLAIN),
                new Segment(" E ", Kind.EVENT),
                new Segment(NOT_DONE_GLYPH, Kind.NOT_DONE),
                new Segment(" project meeting ", Kind.PLAIN),
                new Segment("(from: Aug 6 2026 to: Aug 8 2026)", Kind.DATE)),
                segments);
    }

    @Test
    public void parse_doneEvent_doneStatusAndDatesSplitOff() {
        List<Segment> segments = TaskLineParser.parse("6." + done(newEvent()));

        assertEquals(List.of(
                new Segment("6.", Kind.PLAIN),
                new Segment(" E ", Kind.EVENT),
                new Segment(DONE_GLYPH, Kind.DONE),
                new Segment(" project meeting ", Kind.PLAIN),
                new Segment("(from: Aug 6 2026 to: Aug 8 2026)", Kind.DATE)),
                segments);
    }

    @Test
    public void parse_indentedTaskFromAddedMessage_indentKeptAsPlain() {
        List<Segment> segments = TaskLineParser.parse("  " + new Todo("laundry"));

        assertEquals(List.of(
                new Segment("  ", Kind.PLAIN),
                new Segment(" T ", Kind.TODO),
                new Segment(NOT_DONE_GLYPH, Kind.NOT_DONE),
                new Segment(" laundry", Kind.PLAIN)),
                segments);
    }

    @Test
    public void parse_rescheduledTaskWithFromLabel_labelKeptAsPlain() {
        List<Segment> segments = TaskLineParser.parse("  from: " + newDeadline());

        assertEquals(new Segment("  from: ", Kind.PLAIN), segments.get(0));
        assertEquals(new Segment(" D ", Kind.DEADLINE), segments.get(1));
        assertEquals(new Segment("(by: Jun 6 2026)", Kind.DATE), segments.get(segments.size() - 1));
    }

    @Test
    public void parse_rescheduledTaskWithToLabel_labelKeptAsPlain() {
        List<Segment> segments = TaskLineParser.parse("  to:   " + newEvent());

        assertEquals(new Segment("  to:   ", Kind.PLAIN), segments.get(0));
        assertEquals(new Segment(" E ", Kind.EVENT), segments.get(1));
    }

    @Test
    public void parse_todoDescriptionLookingLikeDates_datesNotSplitOff() {
        List<Segment> segments = TaskLineParser.parse("1." + new Todo("plan (by: Jun 6 2026)"));

        assertEquals(new Segment(" plan (by: Jun 6 2026)", Kind.PLAIN), segments.get(segments.size() - 1));
    }

    @Test
    public void parse_ordinaryMessage_singlePlainPiece() {
        assertEquals(List.of(new Segment("Here are the tasks in your list:", Kind.PLAIN)),
                TaskLineParser.parse("Here are the tasks in your list:"));
    }

    @Test
    public void parse_errorMessage_singlePlainPiece() {
        assertEquals(List.of(new Segment("HEY! idk what's that supposed to be", Kind.PLAIN)),
                TaskLineParser.parse("HEY! idk what's that supposed to be"));
    }

    @Test
    public void parse_emptyLine_singleEmptyPlainPiece() {
        assertEquals(List.of(new Segment("", Kind.PLAIN)), TaskLineParser.parse(""));
    }
}
