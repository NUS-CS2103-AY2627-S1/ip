package surveyprogram.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests state transitions for a completable {@link Item}. */
class ItemTest {

    @Test
    void gettersAndToString_reflectDescriptionAndCompletionState() {
        Item item = new Item("write tests");
        assertEquals("write tests", item.getName());
        assertEquals("[ ] write tests", item.toString());

        item.setDone(true);
        assertTrue(item.isDone());
        assertEquals("[X] write tests", item.toString());
    }

    @Test
    void mark_incompleteItem_marksItemDone() {
        Item item = new Item("write tests");

        item.mark();

        assertTrue(item.isDone());
    }

    @Test
    void mark_alreadyCompletedItem_keepsItemDoneAndReportsDuplicate() {
        Item item = new Item("write tests");
        item.setDone(true);

        String output = item.mark();

        assertTrue(item.isDone());
        assertTrue(output.contains("BUT, IT WAS ALREADY DONE."));
    }

    @Test
    void undo_completedItem_marksItemIncomplete() {
        Item item = new Item("write tests");
        item.setDone(true);

        item.undo();

        assertFalse(item.isDone());
    }

    @Test
    void undo_incompleteItem_keepsItemIncompleteAndReportsInvalidState() {
        Item item = new Item("write tests");

        String output = item.undo();

        assertFalse(item.isDone());
        assertTrue(output.contains("BUT, YOU HAVEN'T DONE IT IN THE FIRST PLACE."));
    }
}
