package types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import recordbase.exceptions.RecordException;
import recordbase.types.ListItem;
import recordbase.types.RecordList;
import recordbase.types.ToDoItem;

public class ListTest {

    @Test
    void deleteItem_removesCorrectItemAndUpdatesList() {
        RecordList list = new RecordList();

        list.addToDoItem("Read book");
        list.addDeadlineItem(
                "Submit report",
                LocalDateTime.of(2026, 9, 1, 23, 59));
        list.addEventItem(
                "Team meeting",
                LocalDateTime.of(2026, 9, 2, 10, 0),
                LocalDateTime.of(2026, 9, 2, 11, 0));

        String deletedItem = list.deleteItem(1);

        assertTrue(deletedItem.contains("Due: 1 Sep 2026, 11:59 PM"));

        assertEquals(2, list.getItems().size());
        assertEquals("[T] [ ] Read book [Priority: Medium (3)]", list.getItem(0).toString());
        assertTrue(list.getItem(1).toString().contains(
                "From: 2 Sep 2026, 10:00 AM | To: 2 Sep 2026, 11:00 AM | Duration: 1 hour"));
    }

    @Test
    void deleteItem_withInvalidIndex_throwsRecordException() {
        RecordList list = new RecordList();
        assertThrows(RecordException.class, () -> list.deleteItem(-1));
        assertThrows(RecordException.class, () -> list.deleteItem(0));
        assertThrows(RecordException.class, () -> list.deleteItem(1));

        list.addToDoItem("Read book");

        assertThrows(RecordException.class, () -> list.deleteItem(-1));
        assertThrows(RecordException.class, () -> list.deleteItem(1));
    }

    @Test
    void deleteItem_withInvalidIndex2_throwsRecordException() {
        RecordList list = new RecordList();
        list.addToDoItem("Only item");

        RecordException negativeIndexException = assertThrows(RecordException.class, () -> list.deleteItem(-1));

        RecordException tooLargeIndexException = assertThrows(RecordException.class, () -> list
                .deleteItem(1));

        assertEquals(
                "That track number is not on your list.",
                negativeIndexException.getMessage());

        assertEquals(
                "That track number is not on your list.",
                tooLargeIndexException.getMessage());
    }

    @Test
    void addItem_addsItemAndReturnsCorrectIndex() {
        RecordList list = new RecordList();
        ListItem item = new ToDoItem("Generic task");

        int index = list.addItem(item);

        assertEquals(0, index);
        assertEquals(item, list.getItem(0));
        assertEquals(1, list.getItems().size());
    }

    @Test
    void addItem_returnsSequentialIndexes() {
        RecordList list = new RecordList();

        assertEquals(0, list.addItem(new ToDoItem("First")));
        assertEquals(1, list.addItem(new ToDoItem("Second")));
        assertEquals(2, list.addItem(new ToDoItem("Third")));

        assertEquals(3, list.getItems().size());
    }

    @Test
    void addToDoItem_excessivelyLongDescription_rejected() {
        RecordList list = new RecordList();
        String description = "x".repeat(RecordList.MAX_DESCRIPTION_LENGTH + 1);

        assertThrows(RecordException.class, () -> list.addToDoItem(description));
        assertTrue(list.getItems().isEmpty());
    }

    @Test
    void addToDoItem_addsToDoItemAndReturnsIndex() {
        RecordList list = new RecordList();

        int index = list.addToDoItem("Read book");

        assertEquals(0, index);
        assertEquals("[T] [ ] Read book [Priority: Medium (3)]", list.getItem(index).toString());
        assertEquals(1, list.getItems().size());
    }

    @Test
    void addDeadlineItem_addsDeadlineItemAndReturnsIndex() {
        RecordList list = new RecordList();
        LocalDateTime deadline = LocalDateTime.of(2026, 9, 1, 23, 59);

        int index = list.addDeadlineItem("Submit report", deadline);

        assertEquals(0, index);
        assertTrue(list.getItem(index).toString().contains("Due: 1 Sep 2026, 11:59 PM"));
        assertEquals(1, list.getItems().size());
    }

    @Test
    void addEventItem_addsEventItemAndReturnsIndex() {
        RecordList list = new RecordList();
        LocalDateTime from = LocalDateTime.of(2026, 9, 2, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 2, 11, 0);

        int index = list.addEventItem("Team meeting", from, to);

        assertEquals(0, index);
        assertTrue(list.getItem(index).toString().contains(
                "From: 2 Sep 2026, 10:00 AM | To: 2 Sep 2026, 11:00 AM | Duration: 1 hour"));
        assertEquals(1, list.getItems().size());
    }

    @Test
    void deleteItem_removesCorrectItemAndShiftsRemainingItems() {
        RecordList list = new RecordList();

        list.addToDoItem("First");
        list.addToDoItem("Second");
        list.addToDoItem("Third");

        String deletedItem = list.deleteItem(1);

        assertEquals("[T] [ ] Second [Priority: Medium (3)]", deletedItem);
        assertEquals(2, list.getItems().size());
        assertEquals("[T] [ ] First [Priority: Medium (3)]", list.getItem(0).toString());
        assertEquals("[T] [ ] Third [Priority: Medium (3)]", list.getItem(1).toString());
    }

    @Test
    void deleteItem_canDeleteFirstAndLastItems() {
        RecordList list = new RecordList();

        list.addToDoItem("First");
        list.addToDoItem("Second");
        list.addToDoItem("Third");

        assertEquals("[T] [ ] First [Priority: Medium (3)]", list.deleteItem(0));
        assertEquals("[T] [ ] Third [Priority: Medium (3)]", list.deleteItem(1));

        assertEquals(1, list.getItems().size());
        assertEquals("[T] [ ] Second [Priority: Medium (3)]", list.getItem(0).toString());
    }

    @Test
    void setListItemDone_marksItemAsDone() {
        RecordList list = new RecordList();
        list.addToDoItem("Complete assignment");

        list.setListItemDone(0);

        assertEquals("[T] [X] Complete assignment [Priority: Medium (3)]", list.getItem(0).toString());
    }

    @Test
    void setListItemNotDone_marksPreviouslyDoneItemAsNotDone() {
        RecordList list = new RecordList();
        list.addToDoItem("Complete assignment");

        list.setListItemDone(0);
        list.setListItemNotDone(0);

        assertEquals("[T] [ ] Complete assignment [Priority: Medium (3)]",
                list.getItem(0).toString());
    }

    @Test
    void setListItemDone_withInvalidIndex_throwsRecordException() {
        RecordList list = new RecordList();
        list.addToDoItem("Task");

        RecordException negativeIndexException = assertThrows(RecordException.class, () -> list
                .setListItemDone(-1));

        RecordException tooLargeIndexException = assertThrows(RecordException.class, () -> list
                .setListItemDone(1));

        assertEquals(
                "I can't mark that track — it is not on your list.",
                negativeIndexException.getMessage());

        assertEquals(
                "I can't mark that track — it is not on your list.",
                tooLargeIndexException.getMessage());
    }

    @Test
    void setListItemNotDone_withInvalidIndex_throwsRecordException() {
        RecordList list = new RecordList();
        list.addToDoItem("Task");

        RecordException negativeIndexException = assertThrows(RecordException.class, () -> list
                .setListItemNotDone(-1));

        RecordException tooLargeIndexException = assertThrows(RecordException.class, () -> list
                .setListItemNotDone(1));

        assertEquals(
                "I can't unmark that track — it is not on your list.",
                negativeIndexException.getMessage());

        assertEquals(
                "I can't unmark that track — it is not on your list.",
                tooLargeIndexException.getMessage());
    }

    @Test
    void getItem_returnsNullForInvalidIndex() {
        RecordList list = new RecordList();
        list.addToDoItem("Task");

        assertEquals(null, list.getItem(-1));
        assertEquals(null, list.getItem(1));
    }

    @Test
    void getItems_returnsAllItemsInInsertionOrder() {
        RecordList list = new RecordList();

        list.addToDoItem("First");
        list.addDeadlineItem(
                "Second",
                LocalDateTime.of(2026, 9, 1, 12, 0));
        list.addEventItem(
                "Third",
                LocalDateTime.of(2026, 9, 2, 10, 0),
                LocalDateTime.of(2026, 9, 2, 11, 0));

        List<ListItem> items = list.getItems();

        assertEquals(3, items.size());
        assertEquals("[T] [ ] First [Priority: Medium (3)]", items.get(0).toString());
        assertTrue(items.get(1).toString().contains("Due: 1 Sep 2026, 12:00 PM"));
        assertTrue(items.get(2).toString().contains(
                "From: 2 Sep 2026, 10:00 AM | To: 2 Sep 2026, 11:00 AM | Duration: 1 hour"));
    }

    @Test
    void getItems_preventsModificationOfInternalList() {
        RecordList list = new RecordList();
        list.addToDoItem("Task");

        List<ListItem> items = list.getItems();

        assertThrows(UnsupportedOperationException.class, () -> items.add(new ToDoItem("Another task")));
        assertEquals(1, list.getItems().size());
    }

    @Test
    void toString_returnsMessageForEmptyList() {
        RecordList list = new RecordList();

        assertEquals("The record is quiet — no tasks yet.", list.toString());
    }

    @Test
    void toString_returnsNumberedItemsWithNewlines() {
        RecordList list = new RecordList();

        list.addToDoItem("First");
        list.addToDoItem("Second");

        assertEquals(
                "1. [T] [ ] First [Priority: Medium (3)]\n"
                + "2. [T] [ ] Second [Priority: Medium (3)]\n",
                list.toString());
    }
}
