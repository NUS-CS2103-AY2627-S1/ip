package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import orangutan.chatlist.ItemType;
import orangutan.chatlist.ListItem;

public class SortMethodTest {
    @Test
    public void noneComparator_anyItems_returnsZero() {
        ListItem first = new ListItem(ItemType.TODO, "first", false);
        ListItem second = new ListItem(ItemType.DEADLINE, "second", true, "20260901 1700");

        assertEquals(0, SortMethod.NONE.getComparator().compare(first, second));
    }

    @Test
    public void completionComparator_differentCompletion_ordersUncompletedFirst() {
        ListItem uncompleted = new ListItem(ItemType.TODO, "zebra", false);
        ListItem completed = new ListItem(ItemType.TODO, "alpha", true);

        assertTrue(SortMethod.COMPLETION.getComparator().compare(uncompleted, completed) < 0);
    }

    @Test
    public void completionComparator_sameCompletion_ordersByTitle() {
        ListItem alpha = new ListItem(ItemType.TODO, "alpha", false);
        ListItem zebra = new ListItem(ItemType.TODO, "zebra", false);

        assertTrue(SortMethod.COMPLETION.getComparator().compare(alpha, zebra) < 0);
    }

    @Test
    public void dateTimeComparator_differentTimes_ordersChronologically() {
        ListItem earlier = new ListItem(ItemType.EVENT, "event", false, "20260901 0900", "20260901 1000");
        ListItem later = new ListItem(ItemType.DEADLINE, "deadline", false, "20260901 1700");

        assertTrue(SortMethod.DATETIME.getComparator().compare(earlier, later) < 0);
    }

    @Test
    public void dateTimeComparator_todoAndTimedItem_ordersTodoFirst() {
        ListItem todo = new ListItem(ItemType.TODO, "todo", false);
        ListItem deadline = new ListItem(ItemType.DEADLINE, "deadline", false, "20260901 1700");

        assertTrue(SortMethod.DATETIME.getComparator().compare(todo, deadline) < 0);
    }
}
