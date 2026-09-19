package gary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gary.ui.ResponseType;

class GaryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_taskLifecycle_returnsUpdatedTaskDetails() {
        Gary gary = new Gary(temporaryDirectory.resolve("data").resolve("gary.txt"));
        String separator = System.lineSeparator();

        assertEquals("Got it. I've added this task:" + separator
                + "  [T][ ] read book" + separator
                + "Now you have 1 tasks in the list.", gary.getResponse("todo read book"));
        assertEquals("Nice! I've marked this task as done:" + separator
                + "  [T][X] read book", gary.getResponse("mark 1"));
        assertEquals("Here are the matching tasks in your list:" + separator
                + "1.[T][X] read book", gary.getResponse("find BOOK"));
        assertEquals("OK, I've marked this task as not done yet:" + separator
                + "  [T][ ] read book", gary.getResponse("unmark 1"));
        assertEquals("Noted. I've removed this task:" + separator
                + "  [T][ ] read book" + separator
                + "Now you have 0 tasks in the list.", gary.getResponse("delete 1"));
    }

    @Test
    void getResponse_invalidAndExitCommands_returnsMessages() {
        Gary gary = new Gary(temporaryDirectory.resolve("gary.txt"));

        assertEquals("Meow? The description of a todo cannot be empty.", gary.getResponse("todo"));
        assertEquals("Meow... I don't recognize that command. Type help to see the available commands.",
                gary.getResponse("blah"));
        assertEquals("Meow! Sea you again soon!", gary.getResponse("bye"));
    }

    @Test
    void getResponse_helpCommand_returnsCommandGuide() {
        Gary gary = new Gary(temporaryDirectory.resolve("gary.txt"));
        String separator = System.lineSeparator();

        assertEquals("Here are the available commands:" + separator
                + "  - list : Views all tasks" + separator
                + "  - todo <description> : Adds a todo task" + separator
                + "  - deadline <description> /by <date> : Adds a deadline task" + separator
                + "  - event <description> /from <start date> /to <end date> : Adds an event task" + separator
                + "  - mark <index> : Marks a task as completed" + separator
                + "  - unmark <index> : Marks a task as not completed" + separator
                + "  - delete <index> : Deletes a task from the list" + separator
                + "  - find <keyword> : Finds tasks by description" + separator
                + "  - contact add <name> /phone <phone> /email <email> : Adds a contact" + separator
                + "  - contact list : Views all contacts" + separator
                + "  - contact find <keyword> : Finds contacts by name, phone, or email" + separator
                + "  - contact delete <index> : Deletes a contact" + separator
                + "  - help : Shows this command guide" + separator
                + "  - bye : Ends the conversation" + separator
                + "  - Date format: YYYY-MM-DD (e.g. 2026-09-30)", gary.getResponse("help"));
    }

    @Test
    void getChatResponse_normalErrorAndFarewellCommands_returnsPresentationTypes() {
        Gary gary = new Gary(temporaryDirectory.resolve("gary.txt"));

        assertEquals(ResponseType.NORMAL, gary.getChatResponse("todo read book").type());
        assertEquals(ResponseType.HELP, gary.getChatResponse("help").type());
        assertEquals(ResponseType.ERROR, gary.getChatResponse("unknown").type());
        assertEquals(ResponseType.FAREWELL, gary.getChatResponse("bye").type());
    }

    @Test
    void getResponse_newInstance_loadsPersistedTasks() {
        Path filePath = temporaryDirectory.resolve("data").resolve("gary.txt");
        Gary firstGary = new Gary(filePath);
        String separator = System.lineSeparator();

        firstGary.getResponse("deadline return book /by 2019-12-02");
        firstGary.getResponse("mark 1");
        Gary secondGary = new Gary(filePath);

        assertEquals("Here are the tasks in your list:" + separator
                + "1.[D][X] return book (by: 2019-12-02)", secondGary.getResponse("list"));
    }

    @Test
    void getResponse_dataFilesMissing_reportsEmptyListsAndRemainsUsable() {
        Path filePath = temporaryDirectory.resolve("missing").resolve("gary.txt");
        Gary gary = new Gary(filePath);

        assertEquals("Your task list is empty. Add one with todo, deadline, or event.",
                gary.getResponse("list"));
        assertEquals("Your contact list is empty. Add one with contact add.",
                gary.getResponse("contact list"));
        assertEquals("Got it. I've added this task:" + System.lineSeparator()
                + "  [T][ ] first task" + System.lineSeparator()
                + "Now you have 1 tasks in the list.", gary.getResponse("todo first task"));
    }

    @Test
    void getResponse_contactLifecycle_persistsUpdatedContacts() {
        Path taskFilePath = temporaryDirectory.resolve("data").resolve("gary.txt");
        Gary firstGary = new Gary(taskFilePath);
        String separator = System.lineSeparator();

        assertEquals("Got it. I've added this contact:" + separator
                + "  Alice Tan (Phone: 91234567, Email: alice@example.com)" + separator
                + "Now you have 1 contact.", firstGary.getResponse(
                        "contact add Alice Tan /phone 91234567 /email alice@example.com"));
        assertEquals("Here are the matching contacts:" + separator
                + "1. Alice Tan (Phone: 91234567, Email: alice@example.com)",
                firstGary.getResponse("contact find ALICE"));

        Gary secondGary = new Gary(taskFilePath);
        assertEquals("Here are your contacts:" + separator
                + "1. Alice Tan (Phone: 91234567, Email: alice@example.com)",
                secondGary.getResponse("contact list"));
        assertEquals("Noted. I've removed this contact:" + separator
                + "  Alice Tan (Phone: 91234567, Email: alice@example.com)" + separator
                + "Now you have 0 contacts.", secondGary.getResponse("contact delete 1"));
    }
}
