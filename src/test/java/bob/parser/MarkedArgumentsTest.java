package bob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import bob.BobException;

/**
 * Tests {@link MarkedArguments}, which splits the text of a command at its markers.
 *
 * <p>Every command with markers reads its text through this class, so a mistake here
 * would show up in {@code deadline}, {@code event} and {@code edit} alike.
 */
public class MarkedArgumentsTest {

    @Test
    public void parse_noMarkers_wholeTextIsThePreamble() throws BobException {
        MarkedArguments marked = MarkedArguments.parse("read book");

        assertEquals("read book", marked.getPreamble());
        assertEquals(List.of(), marked.getKeywords());
    }

    @Test
    public void parse_emptyText_nothingFound() throws BobException {
        MarkedArguments marked = MarkedArguments.parse("");

        assertEquals("", marked.getPreamble());
        assertEquals(List.of(), marked.getKeywords());
    }

    @Test
    public void parse_markers_textSplitAtEachMarker() throws BobException {
        MarkedArguments marked =
                MarkedArguments.parse("project meeting /from 2026-12-02 1800 /to 2026-12-02 2000");

        assertEquals("project meeting", marked.getPreamble());
        assertEquals(Optional.of("2026-12-02 1800"), marked.getValue("/from"));
        assertEquals(Optional.of("2026-12-02 2000"), marked.getValue("/to"));
    }

    @Test
    public void parse_markersInAnyOrder_keptInTheOrderTyped() throws BobException {
        MarkedArguments marked = MarkedArguments.parse("/to 2026-12-03 /desc trip /from 2026-12-01");

        assertEquals(List.of("/to", "/desc", "/from"), marked.getKeywords());
        assertEquals(Optional.of("trip"), marked.getValue("/desc"));
    }

    @Test
    public void parse_markerTextInsideAWord_readAsOrdinaryText() throws BobException {
        // Only a whole word is a marker, so /tokyo is not /to and /byzantine is not /by.
        MarkedArguments marked = MarkedArguments.parse("study /byzantine a/by /bye /by 2026-12-02 /tokyo");

        assertEquals("study /byzantine a/by /bye", marked.getPreamble());
        assertEquals(List.of("/by"), marked.getKeywords());
        assertEquals(Optional.of("2026-12-02 /tokyo"), marked.getValue("/by"));
    }

    @Test
    public void parse_tabsAroundMarker_readLikeSpaces() throws BobException {
        MarkedArguments marked = MarkedArguments.parse("return book\t/by\t2026-12-02");

        assertEquals("return book", marked.getPreamble());
        assertEquals(Optional.of("2026-12-02"), marked.getValue("/by"));
    }

    @Test
    public void parse_markerWithNothingAfterIt_emptyValue() throws BobException {
        // Found, with an empty value, so that the command can say what is missing.
        assertEquals(Optional.of(""), MarkedArguments.parse("return book /by").getValue("/by"));
        assertEquals(Optional.of(""), MarkedArguments.parse("/from /to 2026-12-03").getValue("/from"));
    }

    @Test
    public void getValue_markerNotUsed_emptyOptional() throws BobException {
        MarkedArguments marked = MarkedArguments.parse("return book /by 2026-12-02");

        assertEquals(Optional.empty(), marked.getValue("/from"));
        assertFalse(marked.hasKeyword("/from"));
        assertTrue(marked.hasKeyword("/by"));
    }

    @Test
    public void parse_markerWrittenTwice_exceptionThrown() {
        // Keeping either value would be a guess at which one the user meant.
        BobException byException = assertThrows(BobException.class, () ->
                MarkedArguments.parse("return book /by 2026-12-02 /by 2026-12-03"));
        BobException descException = assertThrows(BobException.class, () ->
                MarkedArguments.parse("/desc a /desc b"));

        assertEquals("You wrote /by twice. Give just one.", byException.getMessage());
        assertEquals("You wrote /desc twice. Give just one.", descException.getMessage());
    }

    @Test
    public void parse_markerInWrongCase_exceptionSuggestsTheLowercaseMarker() {
        BobException upperException = assertThrows(BobException.class, () ->
                MarkedArguments.parse("return book /BY 2026-12-02"));
        BobException mixedException = assertThrows(BobException.class, () ->
                MarkedArguments.parse("/Desc read book"));

        assertEquals("Markers are lowercase. Did yu mean /by?", upperException.getMessage());
        assertEquals("Markers are lowercase. Did yu mean /desc?", mixedException.getMessage());
    }
}
