package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import orangutan.OrangutanException;
import orangutan.Storage;
import orangutan.chatlist.ChatList;
import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;

public class IndexCommandTest {
    private ChatList list;
    private Context context;
    private ListItem first;
    private ListItem second;

    @BeforeEach
    public void setUp() {
        first = new ListItem(ItemType.TODO, "first", false);
        second = new ListItem(ItemType.TODO, "second", true);
        list = new ChatList();
        list.addItem(first);
        list.addItem(second);
        context = new Context(list, true, Path.of("orangutan.txt"), new Storage());
    }

    @Test
    public void run_deleteValidIndex_removesAndReturnsItem() throws OrangutanException {
        String result = new DeleteCommand("1").run(context);

        assertEquals("The following task hath been purged from our records:\n [T] [ ] first", result);
        assertEquals(1, list.getLength());
        assertEquals(second, list.toStream().findFirst().orElseThrow());
    }

    @Test
    public void run_markValidIndex_marksAndReturnsItem() throws OrangutanException {
        String result = new MarkCommand("1").run(context);

        assertEquals("My compliments, thou hast completed a task:\n [T] [X] first", result);
        assertTrue(first.isCompleted());
    }

    @Test
    public void run_unmarkValidIndex_unmarksAndReturnsItem() throws OrangutanException {
        String result = new UnmarkCommand("2").run(context);

        assertEquals("Take heed, this task hath not yet been completed:\n [T] [ ] second", result);
        assertFalse(second.isCompleted());
    }

    @Test
    public void run_nonNumericIndex_throwsWithoutChangingList() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                new DeleteCommand("one").run(context));

        assertEquals("Alas! That is no number known to me.\n\n"
                + "Please input an integer between 1 and 2 (inclusive).", exception.toString());
        assertEquals(2, list.getLength());
    }

    @Test
    public void run_indexBelowRange_throwsWithoutChangingList() {
        assertThrows(OrangutanException.class, () -> new MarkCommand("0").run(context));

        assertFalse(first.isCompleted());
        assertEquals(2, list.getLength());
    }

    @Test
    public void run_indexAboveRange_throwsWithoutChangingList() {
        assertThrows(OrangutanException.class, () -> new UnmarkCommand("3").run(context));

        assertTrue(second.isCompleted());
        assertEquals(2, list.getLength());
    }

    @Test
    public void run_indexCommandWithEmptyList_throwsOrangutanException() {
        Context emptyContext = new Context(new ChatList(), true, Path.of("orangutan.txt"), new Storage());

        assertThrows(OrangutanException.class, () -> new DeleteCommand("1").run(emptyContext));
        assertThrows(OrangutanException.class, () -> new MarkCommand("1").run(emptyContext));
        assertThrows(OrangutanException.class, () -> new UnmarkCommand("1").run(emptyContext));
    }
}
