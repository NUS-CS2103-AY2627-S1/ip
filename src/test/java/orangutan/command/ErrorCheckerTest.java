package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import orangutan.OrangutanException;
import orangutan.Storage;
import orangutan.chatlist.ChatList;
import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;

public class ErrorCheckerTest {
    private ChatList list;
    private Context context;

    @BeforeEach
    public void setUp() {
        list = new ChatList();
        context = new Context(list, true, Path.of("orangutan.txt"), new Storage());
    }

    @Test
    public void checkEmptyList_emptyList_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkEmptyList(context, "mark"));

        assertEquals("Alas! There is naught to mark.\n\nPray add some items to the list first.",
                exception.toString());
    }

    @Test
    public void checkEmptyList_nonEmptyList_doesNotThrow() {
        list.addItem(new ListItem(ItemType.TODO, "read book", false));

        assertDoesNotThrow(() -> ErrorChecker.checkEmptyList(context, "mark"));
    }

    @Test
    public void checkIndexOutOfBounds_indexAtBoundaries_doesNotThrow() {
        list.addItem(new ListItem(ItemType.TODO, "first", false));
        list.addItem(new ListItem(ItemType.TODO, "second", false));

        assertDoesNotThrow(() -> ErrorChecker.checkIndexOutOfBounds(context, 1));
        assertDoesNotThrow(() -> ErrorChecker.checkIndexOutOfBounds(context, 2));
    }

    @Test
    public void checkIndexOutOfBounds_invalidIndex_throwsOrangutanException() {
        list.addItem(new ListItem(ItemType.TODO, "read book", false));

        assertThrows(OrangutanException.class, () -> ErrorChecker.checkIndexOutOfBounds(context, 0));
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkIndexOutOfBounds(context, 2));
        assertEquals("Alas! That number appeareth not in your list.\n\n"
                        + "Pray choose an index between 1 and 1 (inclusive).",
                exception.toString());
    }

    @Test
    public void checkMissingTitle_missingTitle_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkMissingTitle(new String[]{"todo"}));

        assertEquals("Alas! The todo hath been given no name.\n\nPray provide the todo name.", exception.toString());
    }

    @Test
    public void checkMissingIndex_missingIndex_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkMissingIndex(context, new String[]{"delete"}));

        assertEquals("Alas! I know not which item to delete.\n\n"
                        + "Pray follow the delete command with an integer from 1 to 0, inclusive.",
                exception.toString());
    }

    @Test
    public void checkMissingFindKeyword_missingKeyword_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkMissingFindKeyword(new String[]{"find"}));

        assertEquals("Alas! I know not what to find.\n\nPray include the words you wish to find in the list.",
                exception.toString());
    }

    @Test
    public void checkMissingSortMethod_missingMethod_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkMissingSortMethod(new String[]{"sort"}));

        assertEquals("Alas! Thou hast not chosen a sorting method.\n\n"
                        + "Pray choose one of the sorting methods I have learned: none, completion, datetime.",
                exception.toString());
    }

    @Test
    public void checkMissingDeadlineParam_missingParameter_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkMissingDeadlineParam(new String[0]));

        assertEquals("Alas! The deadline's details remain incomplete.\n\n"
                        + "Pray include /by, along with the due date and time.",
                exception.toString());
    }

    @Test
    public void checkMissingEventParam_missingParameter_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                ErrorChecker.checkMissingEventParam(new String[]{"20260901 0900"}));

        assertEquals("Alas! The event's details remain incomplete.\n\n"
                        + "pray include /from and /to in your message, along with the start and end dates and times.",
                exception.toString());
    }

    @Test
    public void missingValueChecks_valuesPresent_doNotThrow() {
        assertDoesNotThrow(() -> ErrorChecker.checkMissingTitle(new String[]{"todo", "read book"}));
        assertDoesNotThrow(() -> ErrorChecker.checkMissingIndex(context, new String[]{"delete", "1"}));
        assertDoesNotThrow(() -> ErrorChecker.checkMissingFindKeyword(new String[]{"find", "book"}));
        assertDoesNotThrow(() -> ErrorChecker.checkMissingSortMethod(new String[]{"sort", "none"}));
        assertDoesNotThrow(() -> ErrorChecker.checkMissingDeadlineParam(new String[]{"20260901 1700"}));
        assertDoesNotThrow(() -> ErrorChecker.checkMissingEventParam(
                new String[]{"20260901 0900", "20260901 1100"}));
    }
}
