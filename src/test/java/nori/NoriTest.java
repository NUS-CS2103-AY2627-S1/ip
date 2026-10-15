package nori;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NoriTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getWelcomeMessage_returnsInlineWelcome() {
        Nori nori = new Nori(temporaryDirectory.resolve("nori.txt"));

        assertEquals("Hello! I'm Nori. What can I do for you?", nori.getWelcomeMessage());
    }

    @Test
    void getResponse_helpCommand_returnsCommandSummary() {
        Nori nori = new Nori(temporaryDirectory.resolve("nori.txt"));

        assertEquals("Nori command guide\n"
                + "\n"
                + "ADD TASKS\n"
                + "  • todo DESCRIPTION\n"
                + "  • deadline DESCRIPTION /by DATE_TIME\n"
                + "  • event DESCRIPTION /from START /to END\n"
                + "\n"
                + "MANAGE TASKS\n"
                + "  • list - Show all tasks\n"
                + "  • mark TASK_NUMBER - Mark as done\n"
                + "  • unmark TASK_NUMBER - Mark as not done\n"
                + "  • delete TASK_NUMBER - Remove a task\n"
                + "  • find KEYWORD - Search descriptions\n"
                + "\n"
                + "GENERAL\n"
                + "  • help - Show this guide\n"
                + "  • bye - Exit Nori\n"
                + "\n"
                + "DATE_TIME\n"
                + "  yyyy-MM-dd HHmm  or  d/M/yyyy HHmm", nori.getResponse("help"));
    }

    @Test
    void getResponse_addAndListTask_returnsExpectedResponses() {
        Nori nori = new Nori(temporaryDirectory.resolve("nori.txt"));

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.", nori.getResponse("todo read book"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book", nori.getResponse("list"));
    }

    @Test
    void getResponse_unknownCommand_returnsErrorResponse() {
        Nori nori = new Nori(temporaryDirectory.resolve("nori.txt"));

        assertEquals("OOPS!!! I don't know that command.", nori.getResponse("hello"));
    }
}
