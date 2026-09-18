package myriad.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import myriad.MyriadException;

/**
 * Tests for {@link TaskType}, which pairs each kind of task with the code
 * that marks it in the data file. The codes themselves are part of the save
 * format, so they are pinned exactly: changing one would make every
 * existing data file unreadable.
 */
public class TaskTypeTest {

    @Test
    public void getCode_everyType_matchesSaveFormatLetter() {
        assertEquals("T", TaskType.TODO.getCode());
        assertEquals("D", TaskType.DEADLINE.getCode());
        assertEquals("E", TaskType.EVENT.getCode());
    }

    @Test
    public void fromCode_everyTypesOwnCode_thatTypeReturned() {
        for (TaskType type : TaskType.values()) {
            assertEquals(Optional.of(type), TaskType.fromCode(type.getCode()), type.toString());
        }
    }

    @Test
    public void fromCode_unknownOrMalformedCode_emptyReturned() {
        // Codes are matched exactly: no case folding and no trimming.
        String[] codes = {"X", "t", " T", "T ", "", "TODO"};
        for (String code : codes) {
            assertTrue(TaskType.fromCode(code).isEmpty(), "code \"" + code + "\"");
        }
    }

    @Test
    public void getType_eachTaskSubclass_matchingTypeReturned() throws MyriadException {
        TaskDateTime date = TaskDateTime.parse("2019-12-02");
        assertEquals(TaskType.TODO, new ToDo("read book").getType());
        assertEquals(TaskType.DEADLINE, new Deadline("return book", date).getType());
        assertEquals(TaskType.EVENT, new Event("conference", date, date).getType());
    }

    @Test
    public void toSaveFormat_eachTaskSubclass_startsWithItsTypeCode() throws MyriadException {
        TaskDateTime date = TaskDateTime.parse("2019-12-02");
        Task[] tasks = {new ToDo("read book"), new Deadline("return book", date), new Event("conference", date, date)};
        for (Task task : tasks) {
            assertTrue(task.toSaveFormat().startsWith(task.getType().getCode() + " | "), task.toSaveFormat());
        }
    }
}
