package orangutan.chatlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ItemTypeTest {
    @Test
    public void todoIconTest() {
        assertEquals("[T]", ItemType.TODO.getIcon());
    }

    @Test
    public void deadlineIconTest() {
        assertEquals("[D]", ItemType.DEADLINE.getIcon());
    }

    @Test
    public void eventIconTest() {
        assertEquals("[E]", ItemType.EVENT.getIcon());
    }

    @Test
    public void parseTodoIconTest() {
        assertEquals(ItemType.TODO, ItemType.getTypeFromIcon("[T]"));
    }

    @Test
    public void parseDeadlineIconTest() {
        assertEquals(ItemType.DEADLINE, ItemType.getTypeFromIcon("[D]"));
    }

    @Test
    public void parseEventIconTest() {
        assertEquals(ItemType.EVENT, ItemType.getTypeFromIcon("[E]"));
    }

    @Test
    public void getTypeFromIcon_unknownIcon_returnsNull() {
        assertNull(ItemType.getTypeFromIcon("[?]"));
    }
}
