package oreo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import oreo.exception.OreoException;
import oreo.task.Deadline;
import oreo.task.Event;
import oreo.task.Todo;

/** Tests tag-related command parsing. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTodo_inlineTags_tagsAreSeparateFromDescriptionAndNormalized() {
        Todo todo = parser.parseTodo("todo watch a movie #Fun #weekend");

        assertEquals("watch a movie", todo.getDescription());
        assertEquals(List.of("#fun", "#weekend"), todo.getTags());
    }

    @Test
    void parseDeadlineAndEvent_inlineTags_tagsAreAppliedAfterDateArguments() {
        Deadline deadline = parser.parseDeadline("deadline submit report /by 2026-09-30 #school");
        Event event = parser.parseEvent("event team meeting /from 2026-09-15 /to 2026-09-16 #project");

        assertEquals(List.of("#school"), deadline.getTags());
        assertEquals(List.of("#project"), event.getTags());
    }

    @Test
    void parseTagUpdate_multipleTags_returnsTaskNumberAndNormalizedTags() {
        Parser.TagUpdate tagUpdate = parser.parseTagUpdate("tag 3 #Fun #weekend", "tag");

        assertEquals(3, tagUpdate.taskNumber());
        assertEquals(List.of("#fun", "#weekend"), tagUpdate.tags());
    }

    @Test
    void parseTodo_tagFollowedByOrdinaryWord_invalidTagSequenceRejected() {
        assertThrows(OreoException.class, () -> parser.parseTodo("todo watch #fun movie"));
    }

    @Test
    void parseTagUpdate_tagWithSpace_invalidTagRejected() {
        assertThrows(OreoException.class, () -> parser.parseTagUpdate("tag 1 #fun day", "tag"));
    }

    @Test
    void parseCommand_recognizesEverySupportedCommandAndUnknownInput() {
        assertEquals(Parser.Command.BYE, parser.parseCommand("bye"));
        assertEquals(Parser.Command.LIST, parser.parseCommand("list"));
        assertEquals(Parser.Command.LIST_TAGS, parser.parseCommand("list tags extra"));
        assertEquals(Parser.Command.FIND, parser.parseCommand("find book"));
        assertEquals(Parser.Command.MARK, parser.parseCommand("mark 1"));
        assertEquals(Parser.Command.UNMARK, parser.parseCommand("unmark 1"));
        assertEquals(Parser.Command.TODO, parser.parseCommand("todo read"));
        assertEquals(Parser.Command.DEADLINE, parser.parseCommand("deadline read /by 2026-01-01"));
        assertEquals(Parser.Command.EVENT, parser.parseCommand("event read /from 2026-01-01 /to 2026-01-02"));
        assertEquals(Parser.Command.DELETE, parser.parseCommand("delete 1"));
        assertEquals(Parser.Command.TAG, parser.parseCommand("tagged"));
        assertEquals(Parser.Command.UNTAG, parser.parseCommand("untagged"));
        assertEquals(Parser.Command.UNKNOWN, parser.parseCommand("remind"));
    }

    @Test
    void parseFindAndListTags_validateRequiredAndExactArguments() {
        assertEquals("book", parser.parseFindKeyword("find book"));
        assertThrows(OreoException.class, () -> parser.parseFindKeyword("find"));
        assertThrows(OreoException.class, () -> parser.parseFindKeyword("list"));
        parser.parseListTags("list tags");
        assertThrows(OreoException.class, () -> parser.parseListTags("list tags today"));
    }

    @Test
    void parseTodoAndDates_rejectMissingDuplicatedOrInvalidParameters() {
        assertThrows(OreoException.class, () -> parser.parseTodo("todo"));
        assertThrows(OreoException.class, () -> parser.parseTodo("todo #tag"));
        assertThrows(OreoException.class, () -> parser.parseDeadline("deadline report /by 2026-02-30"));
        assertThrows(OreoException.class, () -> parser.parseDeadline("deadline report /by 2026-09-01 /by 2026-09-02"));
        assertThrows(OreoException.class, () -> parser.parseEvent(
                "event review /from 2026-09-01 /to 2026-09-02 /to 2026-09-03"));
        assertThrows(OreoException.class, () -> parser.parseEvent("event review /from 2026-09-03 /to 2026-09-02"));
    }

    @Test
    void parseTagUpdate_rejectsMissingNonNumericAndInvalidTags() {
        assertThrows(OreoException.class, () -> parser.parseTagUpdate("tag", "tag"));
        assertThrows(OreoException.class, () -> parser.parseTagUpdate("tag one #work", "tag"));
        assertThrows(OreoException.class, () -> parser.parseTagUpdate("untag 1 #bad!", "untag"));
    }
}
