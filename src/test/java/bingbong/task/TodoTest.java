package bingbong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_normalInput_formattedCorrectly() {
        Todo todo = new Todo("buy eggs");
        assertEquals("[T] [ ] buy eggs", todo.toString());
    }

    @Test
    public void toString_markedAsCompleted_formattedCorrectly() {
        Todo todo = new Todo("buy eggs");
        todo.mark();
        assertEquals("[T] [X] buy eggs", todo.toString());
    }

    @Test
    public void formatToFile_normalInput_formattedCorrectly() {
        Todo todo = new Todo("buy eggs");
        assertEquals("T | 0 | buy eggs", todo.formatToFile());
    }

    @Test
    public void formatToFile_completedInput_formattedCorrectly() {
        Todo todo = new Todo("buy eggs");
        todo.mark();
        assertEquals("T | 1 | buy eggs", todo.formatToFile());
    }
}
