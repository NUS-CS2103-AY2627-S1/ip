package oreo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests tag commands through Oreo's command-processing interface. */
class OreoTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_tagCommands_returnsExpectedMessagesAndGroupedTags() {
        Oreo oreo = new Oreo(temporaryDirectory.resolve("Oreo.txt").toString());

        assertEquals("Got it. I've added this task:" + System.lineSeparator()
                + "  [T][ ] watch a movie #fun" + System.lineSeparator()
                + "Now you have 1 tasks in the list.", oreo.getResponse("todo watch a movie #fun"));
        assertEquals("Got it. I've added these tags to this task:" + System.lineSeparator()
                + "  [T][ ] watch a movie #fun #weekend", oreo.getResponse("tag 1 #weekend"));
        assertEquals("Here are the tags and their tasks:" + System.lineSeparator()
                + "#fun" + System.lineSeparator()
                + "  1.[T][ ] watch a movie #fun #weekend" + System.lineSeparator()
                + "#weekend" + System.lineSeparator()
                + "  1.[T][ ] watch a movie #fun #weekend", oreo.getResponse("list tags"));
        assertEquals("Got it. I've removed these tags from this task:" + System.lineSeparator()
                + "  [T][ ] watch a movie #weekend", oreo.getResponse("untag 1 #fun"));
    }

    @Test
    void getResponse_invalidTagCommand_returnsUsageMessage() {
        Oreo oreo = new Oreo(temporaryDirectory.resolve("Oreo.txt").toString());

        assertEquals("Use: untag <task number> <#tag>...", oreo.getResponse("untag1 #fun"));
    }

    @Test
    void getResponse_tagUpdate_tagsPersistWithoutWaitingForExit() throws IOException {
        Path storageFile = temporaryDirectory.resolve("Oreo.txt");
        Oreo oreo = new Oreo(storageFile.toString());

        oreo.getResponse("todo watch a movie");
        oreo.getResponse("tag 1 #fun");

        assertEquals("T |   | watch a movie | tags: #fun" + System.lineSeparator(),
                Files.readString(storageFile));
        Oreo reloadedOreo = new Oreo(storageFile.toString());
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] watch a movie #fun", reloadedOreo.getResponse("list"));
    }

    @Test
    void getResponse_invalidCommandsAndDuplicates_returnsErrorsWithoutChangingTasks() {
        Oreo oreo = new Oreo(temporaryDirectory.resolve("Oreo.txt").toString());

        assertEquals("Use: todo <description> [#tag]...", oreo.getResponse("todo"));
        assertEquals("Unknown command. Try todo, list, find, mark, or bye.", oreo.getResponse("remind me"));
        assertEquals("Got it. I've added this task:" + System.lineSeparator()
                + "  [T][ ] buy milk" + System.lineSeparator()
                + "Now you have 1 tasks in the list.", oreo.getResponse("  todo   buy milk  "));
        assertEquals("An identical task already exists in the list.", oreo.getResponse("todo buy milk"));
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] buy milk", oreo.getResponse("list"));
    }

    @Test
    void getResponse_invalidEventDateRange_returnsError() {
        Oreo oreo = new Oreo(temporaryDirectory.resolve("Oreo.txt").toString());

        assertEquals("An event must end after it starts.",
                oreo.getResponse("event review /from 2026-09-16 /to 2026-09-16"));
        assertEquals("Use: event <description> /from <year>-<month>-<date> "
                        + "/to <year>-<month>-<date> [#tag]...",
                oreo.getResponse("event review /from 2026-02-30 /to 2026-03-01"));
    }

    @Test
    void getResponse_lifecycle_persists() throws IOException {
        Path storageFile = temporaryDirectory.resolve("Oreo.txt");
        Oreo oreo = new Oreo(storageFile.toString());

        oreo.getResponse("todo read book");
        assertEquals("Nice! I've marked this task as done:" + System.lineSeparator() + "  [X] read book",
                oreo.getResponse("mark 1"));
        assertEquals("OK, I've marked this task as not done yet:" + System.lineSeparator() + "  [ ] read book",
                oreo.getResponse("unmark 1"));
        assertEquals("Here are the matching tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] read book", oreo.getResponse("find BOOK"));
        assertEquals("Noted. I've removed this task:" + System.lineSeparator() + "[T][ ] read book"
                + System.lineSeparator() + "Now you have 0 tasks in the list.", oreo.getResponse("delete 1"));
        assertEquals("Bye. Hope to see you again soon!", oreo.getResponse("bye"));
        assertEquals("", Files.readString(storageFile));
        assertEquals("Oreo has already said goodbye. Please restart the app to continue.", oreo.getResponse("list"));
    }

    @Test
    void getResponse_invalidArguments_setsErrorStateAndReturnsGuidance() {
        Oreo oreo = new Oreo(temporaryDirectory.resolve("Oreo.txt").toString());

        assertEquals("Please enter a command.", oreo.getResponse("   "));
        assertEquals("Use: list tags.", oreo.getResponse("list tags now"));
        assertEquals("Use: find <keyword>.", oreo.getResponse("find"));
        assertEquals("Please provide a task number to mark.", oreo.getResponse("mark"));
        assertEquals("Please provide a task number to delete.", oreo.getResponse("delete one"));
        assertEquals("Please provide a valid task number.", oreo.getResponse("unmark 1"));
        assertTrue(oreo.wasLastResponseAnError());
    }
}
