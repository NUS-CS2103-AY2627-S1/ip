package orangutan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import orangutan.OrangutanException;
import orangutan.Storage;
import orangutan.chatlist.ChatList;

public class TaskCommandTest {
    private ChatList list;
    private Context context;

    @BeforeEach
    public void setUp() {
        list = new ChatList();
        context = new Context(list, true, Path.of("orangutan.txt"), new Storage());
    }

    @Test
    public void run_todoCommand_addsTodoAndReturnsMessage() {
        String result = new TodoCommand("read book", false).run(context);

        assertEquals("Hark! A task hath been added to your list:\n [T] [ ] read book", result);
        assertEquals("[T],false,read book", list.toStream().findFirst().orElseThrow().toFile());
    }

    @Test
    public void run_completedTodoCommand_addsCompletedTodo() {
        new TodoCommand("read book", true).run(context);

        assertEquals("[T],true,read book", list.toStream().findFirst().orElseThrow().toFile());
    }

    @Test
    public void run_deadlineCommand_addsDeadlineAndReturnsMessage() throws OrangutanException {
        String result = new DeadlineCommand("submit report", "20260901 1700", false).run(context);

        assertEquals("Hark! A deadline hath been added to your list:\n"
                + " [D] [ ] submit report (by 2026-09-01 17:00)", result);
        assertEquals("[D],false,submit report,20260901 1700",
                list.toStream().findFirst().orElseThrow().toFile());
    }

    @Test
    public void run_deadlineWithInvalidDate_throwsWithoutAddingItem() {
        OrangutanException exception = assertThrows(OrangutanException.class, () ->
                new DeadlineCommand("submit report", "tomorrow", false).run(context));

        assertTrue(exception.toString().startsWith("Alas! I do not comprehend the dates and times"));
        assertEquals(0, list.getLength());
    }

    @Test
    public void run_eventCommand_addsEventAndReturnsMessage() throws OrangutanException {
        String result = new EventCommand("lecture", "20260901 0900", "20260901 1100", false).run(context);

        assertEquals("Hark! An event hath been added to your list:\n"
                + " [E] [ ] lecture (from 2026-09-01 09:00 to 2026-09-01 11:00)", result);
        assertEquals("[E],false,lecture,20260901 0900,20260901 1100",
                list.toStream().findFirst().orElseThrow().toFile());
    }

    @Test
    public void run_eventWithInvalidStart_throwsWithoutAddingItem() {
        assertThrows(OrangutanException.class, () ->
                new EventCommand("lecture", "morning", "20260901 1100", false).run(context));

        assertEquals(0, list.getLength());
    }

    @Test
    public void run_eventWithInvalidEnd_throwsWithoutAddingItem() {
        assertThrows(OrangutanException.class, () ->
                new EventCommand("lecture", "20260901 0900", "noon", false).run(context));

        assertEquals(0, list.getLength());
    }
}
