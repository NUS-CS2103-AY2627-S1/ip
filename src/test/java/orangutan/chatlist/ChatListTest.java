package orangutan.chatlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import orangutan.command.SortMethod;

public class ChatListTest {
    @Test
    public void toStreamTest() {
        ChatList emptyList = new ChatList();
        assertEquals(List.of(), emptyList.toStream().toList());

        ListItem todo = new ListItem(ItemType.TODO, "ab c", false);
        ListItem deadline = new ListItem(ItemType.DEADLINE, "ab c", false, "20260831 1900");
        ListItem event = new ListItem(ItemType.EVENT, "ab c", true, "20260831 1900", "20260831 2200");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo, deadline, event)));
        Stream<ListItem> testStream = test.toStream();

        assertEquals(Stream.of(todo, deadline, event).toList(), testStream.toList());
    }

    @Test
    public void addItemTest() {
        ListItem deadline = new ListItem(ItemType.DEADLINE, "ab c", false, "20260831 1900");
        ListItem event = new ListItem(ItemType.EVENT, "ab c", true, "20260831 1900", "20260831 2200");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(deadline)));

        test.addItem(event);

        assertEquals(2, test.getLength());
        assertEquals(List.of(deadline, event), test.toStream().toList());
        assertEquals("1. [D] [ ] ab c (by 2026-08-31 19:00)\n"
                        + "2. [E] [X] ab c (from 2026-08-31 19:00 to 2026-08-31 22:00)",
                test.toString());
    }

    @Test
    public void addItemToEmptyListTest() {
        ChatList test = new ChatList();
        ListItem todo = new ListItem(ItemType.TODO, "ab c", false);

        test.addItem(todo);

        assertEquals(1, test.getLength());
        assertEquals(List.of(todo), test.toStream().toList());
    }

    @Test
    public void deleteItemTest() {
        ListItem deadline = new ListItem(ItemType.DEADLINE, "ab c", false, "20260831 1900");
        ListItem event = new ListItem(ItemType.EVENT, "ab c", true, "20260831 1900", "20260831 2200");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(deadline, event)));

        String deletedItem = test.deleteItem(2);

        assertEquals(event.toString(), deletedItem);
        assertEquals(1, test.getLength());
        assertEquals(List.of(deadline), test.toStream().toList());
        assertEquals("1. [D] [ ] ab c (by 2026-08-31 19:00)", test.toString());
    }

    @Test
    public void deleteFirstItemTest() {
        ListItem first = new ListItem(ItemType.TODO, "first", false);
        ListItem second = new ListItem(ItemType.TODO, "second", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(first, second)));

        assertEquals(first.toString(), test.deleteItem(1));
        assertEquals(List.of(second), test.toStream().toList());
    }

    @Test
    public void markItemTest() {
        ListItem event = new ListItem(ItemType.EVENT, "ab c", false, "20260831 1900", "20260831 2200");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(event)));

        String markedItem = test.markItem(1);

        assertEquals(event.toString(), markedItem);
        assertTrue(event.isCompleted());
        assertEquals("1. [E] [X] ab c (from 2026-08-31 19:00 to 2026-08-31 22:00)", test.toString());
    }

    @Test
    public void markAlreadyCompletedItemTest() {
        ListItem todo = new ListItem(ItemType.TODO, "ab c", true);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo)));

        assertEquals("[T] [X] ab c", test.markItem(1));
        assertTrue(todo.isCompleted());
    }

    @Test
    public void unmarkItemTest() {
        ListItem event = new ListItem(ItemType.EVENT, "ab c", true, "20260831 1900", "20260831 2200");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(event)));

        String unmarkedItem = test.unmarkItem(1);

        assertEquals(event.toString(), unmarkedItem);
        assertFalse(event.isCompleted());
        assertEquals("1. [E] [ ] ab c (from 2026-08-31 19:00 to 2026-08-31 22:00)", test.toString());
    }

    @Test
    public void unmarkAlreadyUncompletedItemTest() {
        ListItem todo = new ListItem(ItemType.TODO, "ab c", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo)));

        assertEquals("[T] [ ] ab c", test.unmarkItem(1));
        assertFalse(todo.isCompleted());
    }

    @Test
    public void getLengthTest() {
        ListItem todo = new ListItem(ItemType.TODO, "ab c", false);
        ListItem deadline = new ListItem(ItemType.DEADLINE, "ab c", false, "20260831 1900");
        ListItem event = new ListItem(ItemType.EVENT, "ab c", true, "20260831 1900", "20260831 2200");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo, deadline, event)));

        assertEquals(3, test.getLength());

        test.deleteItem(2);

        assertEquals(2, test.getLength());
    }

    @Test
    public void getLengthForEmptyListTest() {
        ChatList test = new ChatList();

        assertEquals(0, test.getLength());
    }

    @Test
    public void findItemTest() {
        ListItem todo = new ListItem(ItemType.TODO, "ab c", false);
        ListItem deadline = new ListItem(ItemType.DEADLINE, "ab c", false, "20260831 1900");
        ListItem event = new ListItem(ItemType.EVENT, "ab c", true, "20260831 1900", "20260831 2200");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo, deadline, event)));
        ChatList results = test.findItem("b ");

        assertEquals(List.of(todo, deadline, event), results.toStream().toList());
    }

    @Test
    public void findItemReturnsOnlyMatchingItemsTest() {
        ListItem lunch = new ListItem(ItemType.TODO, "eat lunch", false);
        ListItem report = new ListItem(ItemType.TODO, "write report", false);
        ListItem dinner = new ListItem(ItemType.TODO, "eat dinner", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(lunch, report, dinner)));

        ChatList results = test.findItem("eat");

        assertEquals(List.of(lunch, dinner), results.toStream().toList());
    }

    @Test
    public void findItemReturnsEmptyListWhenThereAreNoMatchesTest() {
        ListItem todo = new ListItem(ItemType.TODO, "eat lunch", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo)));

        ChatList results = test.findItem("sleep");

        assertEquals(0, results.getLength());
    }

    @Test
    public void findItem_differentlyCasedQuery_returnsMatch() {
        ListItem todo = new ListItem(ItemType.TODO, "Read book", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo)));

        assertEquals(List.of(todo), test.findItem("read").toStream().toList());
    }

    @Test
    public void getSortMethodDefaultsToNoneTest() {
        ChatList test = new ChatList();

        assertEquals(SortMethod.NONE, test.getSortMethod());
    }

    @Test
    public void getSortMethodReturnsSelectedMethodTest() {
        ChatList test = new ChatList();

        test.setSorting(SortMethod.DATETIME);

        assertEquals(SortMethod.DATETIME, test.getSortMethod());
    }

    @Test
    public void setSortingByCompletionSortsUncompletedItemsFirstTest() {
        ListItem completed = new ListItem(ItemType.TODO, "completed", true);
        ListItem uncompleted = new ListItem(ItemType.TODO, "uncompleted", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(completed, uncompleted)));

        test.setSorting(SortMethod.COMPLETION);

        assertEquals(List.of(uncompleted, completed), test.toStream().toList());
    }

    @Test
    public void setSortingByCompletionUsesTitleAsTieBreakerTest() {
        ListItem zebra = new ListItem(ItemType.TODO, "zebra", false);
        ListItem alpha = new ListItem(ItemType.TODO, "alpha", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(zebra, alpha)));

        test.setSorting(SortMethod.COMPLETION);

        assertEquals(List.of(alpha, zebra), test.toStream().toList());
    }

    @Test
    public void setSortingByDateTimeSortsChronologicallyTest() {
        ListItem later = new ListItem(ItemType.DEADLINE, "later", false, "20260902 0900");
        ListItem earlier = new ListItem(ItemType.EVENT, "earlier", false, "20260901 0900", "20260901 1000");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(later, earlier)));

        test.setSorting(SortMethod.DATETIME);

        assertEquals(List.of(earlier, later), test.toStream().toList());
    }

    @Test
    public void addItem_sortingEnabled_insertsItemInSortedPosition() {
        ListItem later = new ListItem(ItemType.DEADLINE, "later", false, "20260902 0900");
        ListItem earlier = new ListItem(ItemType.DEADLINE, "earlier", false, "20260901 0900");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(later)));
        test.setSorting(SortMethod.DATETIME);

        test.addItem(earlier);

        assertEquals(List.of(earlier, later), test.toStream().toList());
    }

    @Test
    public void markItem_sortingByCompletion_reordersList() {
        ListItem first = new ListItem(ItemType.TODO, "alpha", false);
        ListItem second = new ListItem(ItemType.TODO, "beta", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(first, second)));
        test.setSorting(SortMethod.COMPLETION);

        test.markItem(1);

        assertEquals(List.of(second, first), test.toStream().toList());
    }

    @Test
    public void unmarkItem_sortingByCompletion_reordersList() {
        ListItem completed = new ListItem(ItemType.TODO, "alpha", true);
        ListItem uncompleted = new ListItem(ItemType.TODO, "beta", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(uncompleted, completed)));
        test.setSorting(SortMethod.COMPLETION);

        test.unmarkItem(2);

        assertEquals(List.of(completed, uncompleted), test.toStream().toList());
    }

    @Test
    public void setSortingNonePreservesInsertionOrderTest() {
        ListItem first = new ListItem(ItemType.TODO, "first", false);
        ListItem second = new ListItem(ItemType.TODO, "second", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(first, second)));

        test.setSorting(SortMethod.NONE);

        assertEquals(List.of(first, second), test.toStream().toList());
    }

    @Test
    public void toStringReturnsEmptyStringForEmptyListTest() {
        ChatList test = new ChatList();

        assertEquals("", test.toString());
    }

    @Test
    public void toStringReturnsItemWithoutListNumberSuffixTest() {
        ListItem todo = new ListItem(ItemType.TODO, "read book", false);
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo)));

        assertEquals("1. [T] [ ] read book", test.toString());
    }

    @Test
    public void toStringEnumeratesItemsOnSeparateLinesTest() {
        ListItem todo = new ListItem(ItemType.TODO, "read book", false);
        ListItem deadline = new ListItem(ItemType.DEADLINE, "submit report", true, "20260901 1700");
        ChatList test = new ChatList(new ArrayList<ListItem>(List.of(todo, deadline)));

        assertEquals("1. [T] [ ] read book\n2. [D] [X] submit report (by 2026-09-01 17:00)",
                test.toString());
    }
}
