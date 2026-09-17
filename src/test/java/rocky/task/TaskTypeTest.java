package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests for looking up a {@link TaskType} by its command keyword or its save-file icon. */
public class TaskTypeTest {

    @Test
    public void fromKeyword_knownKeywords_matchingTypeReturned() {
        assertEquals(TaskType.TODO, TaskType.fromKeyword("todo"));
        assertEquals(TaskType.DEADLINE, TaskType.fromKeyword("deadline"));
        assertEquals(TaskType.EVENT, TaskType.fromKeyword("event"));
    }

    @Test
    public void fromKeyword_unknownWord_nullReturned() {
        assertNull(TaskType.fromKeyword("list"));
        assertNull(TaskType.fromKeyword("todos"));
        assertNull(TaskType.fromKeyword(""));
    }

    @Test
    public void fromKeyword_differentCase_nullReturned() {
        // Commands are case-sensitive, so "Todo" is not the todo command.
        assertNull(TaskType.fromKeyword("Todo"));
        assertNull(TaskType.fromKeyword("DEADLINE"));
    }

    @Test
    public void fromKeyword_null_nullReturned() {
        assertNull(TaskType.fromKeyword(null));
    }

    @Test
    public void fromIcon_knownIcons_matchingTypeReturned() {
        assertEquals(TaskType.TODO, TaskType.fromIcon("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromIcon("D"));
        assertEquals(TaskType.EVENT, TaskType.fromIcon("E"));
    }

    @Test
    public void fromIcon_unknownOrLowercaseIcon_nullReturned() {
        assertNull(TaskType.fromIcon("X"));
        assertNull(TaskType.fromIcon("t"));
        assertNull(TaskType.fromIcon(""));
        assertNull(TaskType.fromIcon(null));
    }

    @Test
    public void keywordAndIcon_everyType_lookUpBackToSameType() {
        // Guards against a new TaskType whose keyword or icon clashes with an existing one.
        for (TaskType type : TaskType.values()) {
            assertEquals(type, TaskType.fromKeyword(type.getKeyword()));
            assertEquals(type, TaskType.fromIcon(type.getIcon()));
        }
    }
}
