package meowmeow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import meowmeow.MeowmeowException;
import meowmeow.task.TaskPriority;

/**
 * Tests for {@link PriorityFlag#extractFrom(String)} - the scan that pulls
 * an optional "/p" or "/priority" flag out of an add command's arguments,
 * wherever it appears, and rejoins whatever text is left.
 *
 * <p>Worth testing on its own because the scan has several subtle
 * requirements: "/p" must never match inside "/priority", a slash embedded
 * in other text (like a date) must never be mistaken for a marker, and the
 * remainder must rejoin without a doubled space regardless of where the flag
 * sat.
 */
public class PriorityFlagTest {

    // ---- no flag ----

    @Test
    public void extractFrom_noFlag_priorityNoneAndArgumentsUnchanged() throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom("read book");

        assertEquals(TaskPriority.NONE, flag.priority());
        assertEquals("read book", flag.remainingArguments());
    }

    @Test
    public void extractFrom_dateLikeSlash_notMistakenForFlag() throws MeowmeowException {
        // "2/12/2019" has slashes, but none is preceded by whitespace, so it
        // must never be read as a "/p" or "/priority" marker.
        PriorityFlag flag = PriorityFlag.extractFrom("report /by 2/12/2019");

        assertEquals(TaskPriority.NONE, flag.priority());
        assertEquals("report /by 2/12/2019", flag.remainingArguments());
    }

    // ---- flag position ----

    @Test
    public void extractFrom_flagLast_priorityFoundAndRemoved() throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom("read book /p high");

        assertEquals(TaskPriority.HIGH, flag.priority());
        assertEquals("read book", flag.remainingArguments());
    }

    @Test
    public void extractFrom_flagFirst_remainderIsWhatFollows() throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom("/p high read book");

        assertEquals(TaskPriority.HIGH, flag.priority());
        assertEquals("read book", flag.remainingArguments());
    }

    @Test
    public void extractFrom_flagBetweenOtherMarkers_remainderRejoinsWithoutDoubleSpace() throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom("report /p high /by 2/12/2019");

        assertEquals(TaskPriority.HIGH, flag.priority());
        assertEquals("report /by 2/12/2019", flag.remainingArguments());
    }

    // ---- both marker spellings ----

    @Test
    public void extractFrom_longMarker_alsoRecognised() throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom("read book /priority low");

        assertEquals(TaskPriority.LOW, flag.priority());
        assertEquals("read book", flag.remainingArguments());
    }

    @Test
    public void extractFrom_markerIsCaseInsensitive() throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom("read book /P HIGH");

        assertEquals(TaskPriority.HIGH, flag.priority());
        assertEquals("read book", flag.remainingArguments());
    }

    @Test
    public void extractFrom_shortMarkerNeverMatchesInsideLongMarker() throws MeowmeowException {
        // If "/p" matched as a prefix of "/priority", this would be
        // misread as the flag "/p" with value "riority" and leftover "low".
        PriorityFlag flag = PriorityFlag.extractFrom("read book /priority low");

        assertEquals("read book", flag.remainingArguments());
    }

    // ---- errors ----

    @Test
    public void extractFrom_bareFlagAtEnd_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> PriorityFlag.extractFrom("read book /p"));
    }

    @Test
    public void extractFrom_flagImmediatelyFollowedByAnotherMarker_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> PriorityFlag.extractFrom("report /p /by 2/12/2019"));
    }

    @Test
    public void extractFrom_unknownLevel_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> PriorityFlag.extractFrom("read book /p urgent"));
    }

    @Test
    public void extractFrom_twoShortMarkers_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> PriorityFlag.extractFrom("read book /p high /p low"));
    }

    @Test
    public void extractFrom_shortThenLongMarker_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> PriorityFlag.extractFrom("read book /p high /priority low"));
    }

    // ---- parseLevel ----

    @Test
    public void parseLevel_recognisedToken_returnsMatchingPriority() throws MeowmeowException {
        assertEquals(TaskPriority.NONE, PriorityFlag.parseLevel("none"));
        assertEquals(TaskPriority.HIGH, PriorityFlag.parseLevel("h"));
    }

    @Test
    public void parseLevel_unrecognisedToken_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> PriorityFlag.parseLevel("urgent"));
    }
}
