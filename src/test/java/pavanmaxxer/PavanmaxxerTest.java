package pavanmaxxer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PavanmaxxerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addDuplicate_warnsButAddsAndPersistsTask() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(dataFile);
        pavanmaxxer.getResponse("todo read book");

        assertEquals("This task already exists in your list, but I've added it again:\n"
                        + "  [T][ ] READ BOOK\n"
                        + "Now you have 2 tasks in the list.",
                pavanmaxxer.getResponse("todo READ BOOK"));

        Pavanmaxxer reloadedPavanmaxxer = new Pavanmaxxer(dataFile);
        assertEquals("1.[T][ ] read book\n2.[T][ ] READ BOOK",
                reloadedPavanmaxxer.getResponse("list"));
    }

    @Test
    void getResponse_addThenList_returnsGuiReadyMessages() {
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(
                temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Got it. I've added this task:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 1 task in the list.",
                pavanmaxxer.getResponse("todo read book"));
        assertEquals("1.[T][ ] read book", pavanmaxxer.getResponse("list"));
    }

    @Test
    void getResponse_unknownCommand_returnsErrorMessage() {
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(
                temporaryDirectory.resolve("tasks.txt"));

        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-(",
                pavanmaxxer.getResponse("hello"));
    }

    @Test
    void getResponse_listWithNoTasks_returnsEmptyState() {
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(
                temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Your task list is empty.",
                pavanmaxxer.getResponse("list"));
    }

    @Test
    void getResponse_findWithNoMatches_returnsEmptyState() {
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(
                temporaryDirectory.resolve("tasks.txt"));
        pavanmaxxer.getResponse("todo read book");

        assertEquals("No tasks matched \"milk\".",
                pavanmaxxer.getResponse("find milk"));
    }

    @Test
    void getResponse_listWithArguments_returnsUnknownCommandError() {
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(
                temporaryDirectory.resolve("tasks.txt"));

        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-(",
                pavanmaxxer.getResponse("list extra"));
    }

    @Test
    void getResponse_mutatingCommands_updateAndPersistTaskList() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(dataFile);
        pavanmaxxer.getResponse("todo read book");
        pavanmaxxer.getResponse("deadline submit report /by 2026-09-18");
        pavanmaxxer.getResponse("event class /from 2pm /to 4pm");

        assertEquals("Nice! I've marked this task as done:\n"
                        + "  [D][X] submit report (by: Sep 18 2026)",
                pavanmaxxer.getResponse("mark 2"));
        assertEquals("OK, I've marked this task as not done yet:\n"
                        + "  [D][ ] submit report (by: Sep 18 2026)",
                pavanmaxxer.getResponse("unmark 2"));
        assertEquals("Here are the matching tasks in your list:\n"
                        + "1.[E][ ] class (from: 2pm to: 4pm)",
                pavanmaxxer.getResponse("find class"));
        assertEquals("Noted. I've removed this task:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 2 tasks in the list.",
                pavanmaxxer.getResponse("delete 1"));

        Pavanmaxxer reloadedPavanmaxxer = new Pavanmaxxer(dataFile);
        assertEquals("1.[D][ ] submit report (by: Sep 18 2026)\n"
                        + "2.[E][ ] class (from: 2pm to: 4pm)",
                reloadedPavanmaxxer.getResponse("list"));
    }

    @Test
    void getResponse_markOutOfRange_returnsErrorWithoutChangingTasks() {
        Pavanmaxxer pavanmaxxer = new Pavanmaxxer(
                temporaryDirectory.resolve("tasks.txt"));
        pavanmaxxer.getResponse("todo read book");

        assertEquals("OOPS!!! That task number does not exist.",
                pavanmaxxer.getResponse("mark 2"));
        assertEquals("1.[T][ ] read book", pavanmaxxer.getResponse("list"));
    }
}
