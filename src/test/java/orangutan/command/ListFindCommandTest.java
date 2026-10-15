package orangutan.command;

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

public class ListFindCommandTest {
    private ChatList list;
    private Context context;

    @BeforeEach
    public void setUp() {
        list = new ChatList();
        context = new Context(list, true, Path.of("orangutan.txt"), new Storage());
    }

    @Test
    public void run_listWithEmptyList_returnsEmptyMessage() {
        assertEquals("The list contains no undertakings.", new ListCommand().run(context));
    }

    @Test
    public void run_listWithItems_returnsEnumeratedItems() {
        list.addItem(new ListItem(ItemType.TODO, "read book", false));
        list.addItem(new ListItem(ItemType.DEADLINE, "submit report", true, "20260901 1700"));

        String result = new ListCommand().run(context);

        assertEquals("Herein lie the undertakings in your list:\n"
                + "1. [T] [ ] read book\n"
                + "2. [D] [X] submit report (by 2026-09-01 17:00)", result);
    }

    @Test
    public void run_findWithEmptyList_throwsOrangutanException() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                new FindCommand("book").run(context));

        assertEquals("Alas! There is naught to find.\n\nPray add some items to the list first.",
                exception.toString());
    }

    @Test
    public void run_findWithNoMatches_returnsNotFoundMessage() throws OrangutanException {
        list.addItem(new ListItem(ItemType.TODO, "read book", false));

        assertEquals("The item you requested could not be found. Perchance it is not in our list?",
                new FindCommand("lecture").run(context));
    }

    @Test
    public void run_findWithDifferentlyCasedQuery_returnsMatchingItems() throws OrangutanException {
        list.addItem(new ListItem(ItemType.TODO, "Read book", false));
        list.addItem(new ListItem(ItemType.TODO, "write code", false));

        assertEquals("herein lie the items thou seekest:\n1. [T] [ ] Read book",
                new FindCommand("read").run(context));
    }
}
