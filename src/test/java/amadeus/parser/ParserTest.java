package amadeus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import amadeus.AmadeusException;
import amadeus.task.Deadline;
import amadeus.task.Todo;

/**
 * Unit tests for {@link Parser#parseTaskIndex(String, int)}.
 * <p>
 * That method is worth testing because it does the 1-based to 0-based conversion
 * for "mark" and "unmark", and has to reject everything the user might type that
 * is not a valid task number.
 */
public class ParserTest {

    /**
     * The user counts from 1 and arrays count from 0, so the result must be one
     * less than the number typed. This is where an off-by-one error would show up.
     * <p>
     * parseTaskIndex declares AmadeusException (a checked exception), so the test
     * method has to declare it too, otherwise the code would not compile.
     */
    @Test
    void parseTaskIndex_firstTask_returnsZero() throws AmadeusException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", 3));
    }

    /** One past the end is the closest invalid number, so it is the one worth checking. */
    @Test
    void parseTaskIndex_numberAboveTaskCount_throwsAmadeusException() {
        assertThrows(AmadeusException.class, () -> Parser.parseTaskIndex("mark 4", 3));
    }

    /** Everything after "find" is one keyword, so a phrase is not split up. */
    @Test
    void parseKeyword_multiWordKeyword_returnsWholePhrase() throws AmadeusException {
        assertEquals("read book", Parser.parseKeyword("find read book"));
    }

    @Test
    void parseKeyword_missingKeyword_throwsAmadeusException() {
        assertThrows(AmadeusException.class, () -> Parser.parseKeyword("find"));
    }

    /**
     * Integer.parseInt reports bad input by throwing NumberFormatException, which
     * the parser must convert into an AmadeusException so the app can print a
     * friendly message instead of crashing.
     */
    @Test
    void parseTaskIndex_notANumber_throwsAmadeusException() {
        assertThrows(AmadeusException.class, () -> Parser.parseTaskIndex("mark two", 3));
    }

    /** A todo typed without any "#word" gets no tags. */
    @Test
    void parseTodo_noHashtag_hasNoTags() throws AmadeusException {
        assertTrue(Parser.parseTodo("todo read book").getTags().isEmpty());
    }

    /** The hashtag is pulled out of the description rather than left as part of it. */
    @Test
    void parseTodo_trailingHashtag_stripsTagFromDescription() throws AmadeusException {
        Todo todo = Parser.parseTodo("todo read book #fun");
        assertEquals("read book", todo.getDescription());
        assertEquals(List.of("#fun"), todo.getTags());
    }

    /** A tag can appear anywhere in the line, not only at the end. */
    @Test
    void parseTodo_leadingHashtag_stripsTagFromDescription() throws AmadeusException {
        Todo todo = Parser.parseTodo("todo #fun read book");
        assertEquals("read book", todo.getDescription());
        assertEquals(List.of("#fun"), todo.getTags());
    }

    @Test
    void parseTodo_multipleHashtags_collectsAllTags() throws AmadeusException {
        Todo todo = Parser.parseTodo("todo read book #fun #urgent");
        assertEquals("read book", todo.getDescription());
        assertEquals(List.of("#fun", "#urgent"), todo.getTags());
    }

    /** A tag can sit anywhere on the line, including inside the "/by" portion. */
    @Test
    void parseDeadline_hashtagAfterBy_stripsTagAndKeepsDate() throws AmadeusException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2019-06-06 #urgent");
        assertEquals("return book", deadline.getDescription());
        assertEquals(List.of("#urgent"), deadline.getTags());
    }
}
