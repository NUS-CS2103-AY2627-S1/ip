package sage.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import sage.task.Deadline;
import sage.task.Event;
import sage.task.Note;
import sage.task.Task;
import sage.task.TaskType;
import sage.task.Todo;

class TaskCodecTest {
    @Test
    void parse_invalidSavedDateValues_rejectsEveryTimeField() {
        for (String value : List.of("12345", "0000-01-01", "25pm", "???")) {
            for (String record : List.of("D | 0 | report | " + value,
                    "E | 0 | meeting | " + value + " | Sunday",
                    "E | 0 | meeting | Sunday | " + value)) {
                assertThrows(IllegalArgumentException.class, () -> TaskCodec.parse(record), record);
            }
        }
    }

    @Test
    void serialize_allTaskTypes_preservesExactRecordFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", TaskCodec.serialize(todo));
        assertEquals("T | 0 | plain task", TaskCodec.serialize(new Task("plain task", TaskType.TODO)));
        assertEquals("D | 0 | submit | 2025-02-14T00:00:00",
                TaskCodec.serialize(new Deadline("submit", "2025-02-14")));
        assertEquals("E | 0 | meet | 2025-02-14T09:00:00 | 2025-02-14T10:30:00",
                TaskCodec.serialize(new Event("meet", "2025-02-14 09:00", "2025-02-14 10:30")));
    }

    @Test
    void serialize_unparsedTimes_preservesOriginalText() {
        assertEquals("D | 0 | submit | Sunday", TaskCodec.serialize(new Deadline("submit", "Sunday")));
        assertEquals("E | 0 | meet | Monday | Tuesday",
                TaskCodec.serialize(new Event("meet", "Monday", "Tuesday")));
    }

    @Test
    void parse_whitespaceAndCompletionFlag_restoresTask() {
        Task task = TaskCodec.parse(" E | 1 | meet | Monday | Tuesday ");
        Event event = assertInstanceOf(Event.class, task);
        assertEquals("meet", event.getDescription());
        assertEquals("Monday", event.getFromText());
        assertEquals("Tuesday", event.getToText());
        assertEquals("X", event.getStatusIcon());
    }

    @Test
    void parse_invalidRecords_rejectsMissingFieldsAndUnknownValues() {
        for (String line : List.of("T | 0", "T | 0 | ", "D | 0 | submit", "E | 0 | meet | Monday",
                "Q | 0 | unknown", "T | 2 | read")) {
            assertThrows(IllegalArgumentException.class, () -> TaskCodec.parse(line), line);
        }
    }

    @Test
    void parse_emptyTimeFieldsOrImpossibleDates_rejectsCorruptRecords() {
        for (String record : List.of("D | 0 | submit | ", "E | 0 | meet | | ", "D | 0 | submit | 2026-02-30",
                "E | 0 | meet | 2026-09-18 | 2026-09-18", "E | 0 | meet | 2026-09-19 | 2026-09-18",
                "N | 1 | impossible status", "T | 0 | task | extra", "D | 0 | task | Sunday | extra")) {
            assertThrows(IllegalArgumentException.class, () -> TaskCodec.parse(record), record);
        }
    }

    @Test
    void serialize_specialCharacters_roundTripsEveryTaskType() {
        String description = "电影 | C:\\notes\\new\nline\rreturn";
        for (Task task : List.of(new Todo(description), new Note(description),
                new Deadline(description, "Sunday | afternoon\\later"),
                new Event(description, "Monday | afternoon", "Tuesday\\later"))) {
            String serialized = TaskCodec.serialize(task);
            Task parsed = TaskCodec.parse(serialized);
            assertEquals(task.toString(), parsed.toString());
            assertEquals(description, parsed.getDescription());
        }
    }

    @Test
    void serialize_eachSpecialCharacterIndependently_usesEscapedFormatWithoutDataLoss() {
        for (String text : List.of("left | right", "folder\\file", "first\nsecond", "first\rsecond")) {
            String record = TaskCodec.serialize(new Todo(text));

            assertTrue(record.startsWith("V2 | "), text);
            assertEquals(text, TaskCodec.parse(record).getDescription());
            assertEquals(1, record.lines().count());
        }
    }

    @Test
    void parse_numericStartAndNaturalLanguageEnd_keepsOriginalEndText() {
        Event event = assertInstanceOf(Event.class,
                TaskCodec.parse("E | 0 | meeting | 2026-09-18T14:00:00 | tomorrow"));

        assertEquals(14, event.getFrom().getHour());
        assertEquals("tomorrow", event.getToText());
        assertEquals("E | 0 | meeting | 2026-09-18T14:00:00 | tomorrow", TaskCodec.serialize(event));
    }

    @Test
    void parse_legacyPathsAndNotePipes_preservesLiteralEscapes() {
        assertEquals("C:\\notes\\new", TaskCodec.parse("T | 0 | C:\\notes\\new").getDescription());
        assertEquals("Movie | path C:\\notes\\new",
                TaskCodec.parse("N | 0 | Movie | path C:\\notes\\new").getDescription());
    }

    @Test
    void parse_malformedEscapes_rejectsRecords() {
        for (String record : List.of("V2 | T | 0 | bad\\", "V2 | T | 0 | bad\\q")) {
            assertThrows(IllegalArgumentException.class, () -> TaskCodec.parse(record));
        }
    }
}
