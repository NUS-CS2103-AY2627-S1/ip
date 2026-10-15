package hu9o.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the two methods a {@link ToDoTask} shapes: {@code toString} (the list
 * rendering) and {@code compressionString} (the save-file record). Both are
 * inherited from {@link Task} but depend on {@code ToDoTask}'s empty
 * {@code specificationSuffix}, so they are exercised here through a real
 * {@code ToDoTask}.
 */
public class ToDoTaskTest {

    @Test
    public void toString_unmarked_showsTypeTagAndEmptyStatusBox() {
        assertEquals("[T][ ] read book", new ToDoTask("read book").toString());
    }

    @Test
    public void toString_afterMark_showsCrossInStatusBox() {
        ToDoTask task = new ToDoTask("read book");
        task.mark();

        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void compressionString_unmarked_producesPipeDelimitedRecord() {
        assertEquals("T| |read book|", new ToDoTask("read book").compressionString());
    }

    @Test
    public void compressionString_afterMark_recordsDoneFlag() {
        ToDoTask task = new ToDoTask("read book");
        task.mark();

        assertEquals("T|X|read book|", task.compressionString());
    }
}
