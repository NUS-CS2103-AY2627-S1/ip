package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import orangutan.OrangutanException;
import orangutan.Storage;
import orangutan.chatlist.ChatList;
import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;

public class SortCommandTest {
    private ChatList list;
    private Context context;
    private ListItem completed;
    private ListItem uncompleted;

    @BeforeEach
    public void setUp() {
        completed = new ListItem(ItemType.TODO, "alpha", true);
        uncompleted = new ListItem(ItemType.TODO, "zebra", false);
        list = new ChatList();
        list.addItem(completed);
        list.addItem(uncompleted);
        context = new Context(list, true, Path.of("orangutan.txt"), new Storage());
    }

    @Test
    public void run_completion_sortsListAndReturnsList() throws OrangutanException {
        String result = new SortCommand("completion").run(context);

        assertEquals(SortMethod.COMPLETION, list.getSortMethod());
        assertEquals(List.of(uncompleted, completed), list.toStream().toList());
        assertEquals("Your list shall henceforth be ordered by completion.\n"
                + "1. [T] [ ] zebra\n2. [T] [X] alpha", result);
    }

    @Test
    public void run_mixedCaseMethod_acceptsMethodCaseInsensitively() throws OrangutanException {
        new SortCommand("CoMpLeTiOn").run(context);

        assertEquals(SortMethod.COMPLETION, list.getSortMethod());
    }

    @Test
    public void run_none_disablesSortingForFutureAdditions() throws OrangutanException {
        new SortCommand("completion").run(context);

        String result = new SortCommand("none").run(context);
        ListItem newItem = new ListItem(ItemType.TODO, "aardvark", false);
        list.addItem(newItem);

        assertEquals("Any additions to your list shall henceforth take its place at the end of the list.", result);
        assertEquals(SortMethod.NONE, list.getSortMethod());
        assertEquals(List.of(uncompleted, completed, newItem), list.toStream().toList());
    }

    @Test
    public void run_unknownMethod_throwsWithoutChangingSortMethod() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                new SortCommand("alphabetical").run(context));

        assertEquals("Alas! This sorting method is unknown to me.\n\n"
                        + "Pray choose one of the sorting methods I have learned: none, completion, datetime.",
                exception.toString());
        assertEquals(SortMethod.NONE, list.getSortMethod());
    }
}
