package thaisbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toStringNotDoneShowsTypeAndStatus() {
        Todo t = new Todo("read");
        assertEquals("[T][ ] read", t.toString());
    }

    @Test
    public void markAsDoneToFileStringShowsDoneFlag() {
        Todo t = new Todo("read");
        t.markAsDone();
        assertEquals("T | 1 | read", t.toFileString());
    }
}
