package friday;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests that the UI-neutral Friday response API preserves core chatbot behavior. */
class FridayTest {
    private static final String SEPARATOR = "____________________________________________________________\n";
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskCommandsSaveAndListTask() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));

        String addResponse = friday.getResponse("todo read GUI tutorial");
        String listResponse = friday.getResponse("list");

        assertTrue(addResponse.contains("Task logged."));
        assertTrue(listResponse.contains("1.[T][ ] read GUI tutorial"));
        assertTrue(temporaryDirectory.resolve("friday.txt").toFile().isFile());
    }

    @Test
    void getResponse_invalidCommandDoesNotPreventLaterCommands() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));

        String errorResponse = friday.getResponse("unknown");
        String addResponse = friday.getResponse("todo recover after error");

        assertTrue(errorResponse.contains("Command not recognized."));
        assertTrue(addResponse.contains("recover after error"));
        assertFalse(friday.hasExited());
    }

    @Test
    void getResponse_byeMarksSessionAsExited() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));

        String response = friday.getResponse("bye");

        assertTrue(response.contains("FRIDAY signing off. I'll be here when you return."));
        assertTrue(friday.hasExited());
    }

    @Test
    void getResponse_contactLifecycle_returnsExactResponsesAndPersistsDeletion() throws IOException {
        Path taskFile = temporaryDirectory.resolve("friday.txt");
        Friday friday = new Friday(taskFile);
        assertResponse("Your contact directory is empty.\n", friday.getResponse("contact list"));
        assertResponse("Contact scan complete. No matches found.\n", friday.getResponse("contact find Alice"));
        assertResponse("Contact logged. Here's the entry:\n"
                + "  Alice (phone: 91234567, email: alice@example.com)\n"
                + "Now you have 1 contact in the list.\n",
                friday.getResponse("contact add Alice /phone 91234567 /email alice@example.com"));
        assertResponse("Contact logged. Here's the entry:\n"
                + "  Bob (phone: -, email: bob@example.com)\n"
                + "Now you have 2 contacts in the list.\n",
                friday.getResponse("contact add Bob /email bob@example.com"));
        assertResponse("Contact scan complete. Here's what matches:\n"
                + "Use the number shown here with contact delete.\n"
                + "2.Bob (phone: -, email: bob@example.com)\n", friday.getResponse("contact find BOB@"));
        assertResponse("Contact removed from your directory:\n"
                + "  Alice (phone: 91234567, email: alice@example.com)\n"
                + "Now you have 1 contact in the list.\n", friday.getResponse("contact delete 1"));
        Friday restarted = new Friday(taskFile);
        assertResponse("Your contact directory:\n"
                + "Use the number shown here with contact delete.\n"
                + "1.Bob (phone: -, email: bob@example.com)\n", restarted.getResponse("contact list"));
        assertResponse("Contact removed from your directory:\n"
                + "  Bob (phone: -, email: bob@example.com)\n"
                + "Now you have 0 contacts in the list.\n", restarted.getResponse("contact delete 1"));
        assertEquals("", Files.readString(temporaryDirectory.resolve("contacts.txt")));
        assertFalse(Files.exists(taskFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {"contact add ALICE /phone 81234567", "contact delete 2", "contact delete 0",
            "contact add Bob /phone 12345678", "contact add Bob /email bad", "contact find", "contact list extra"})
    void getResponse_invalidContactCommand_preservesBothFilesAndMemory(String command) throws IOException {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        friday.getResponse("todo keep task");
        friday.getResponse("contact add Alice /phone 91234567");
        Path contacts = temporaryDirectory.resolve("contacts.txt");
        Path tasks = temporaryDirectory.resolve("friday.txt");
        byte[] originalContacts = Files.readAllBytes(contacts);
        byte[] originalTasks = Files.readAllBytes(tasks);
        String before = friday.getResponse("contact list");
        friday.getResponse(command);
        assertEquals(before, friday.getResponse("contact list"));
        assertArrayEquals(originalContacts, Files.readAllBytes(contacts));
        assertArrayEquals(originalTasks, Files.readAllBytes(tasks));
    }

    @Test
    void getResponse_readOnlyContactCommands_doNotAttemptSaving() throws IOException {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        friday.getResponse("contact add Alice /phone 91234567");
        Path file = temporaryDirectory.resolve("contacts.txt");
        Files.delete(file);
        Files.createDirectory(file);
        Files.writeString(file.resolve("blocker"), "keep");
        assertFalse(friday.getResponse("contact list").contains("Warning:"));
        assertFalse(friday.getResponse("contact find ALICE").contains("Warning:"));
        assertResponse("A contact with this name already exists.\n",
                friday.getResponse("contact add ALICE /phone 81234567"));
        assertEquals("keep", Files.readString(file.resolve("blocker")));
    }

    @Test
    void getResponse_corruptContactFile_disablesOnlyContactSaving() throws IOException {
        Path file = temporaryDirectory.resolve("contacts.txt");
        Files.writeString(file, "C|Alice|91234567|\ninvalid\n");
        byte[] original = Files.readAllBytes(file);
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        assertEquals(SEPARATOR + "FRIDAY online. Let's keep your day under control.\n"
                + "Warning: I couldn't load data/contacts.txt. Check the file and restart; "
                + "contact saving is disabled to protect existing data.\n" + SEPARATOR,
                friday.getWelcomeMessage().replace("\r\n", "\n"));
        assertResponse("Your contact directory is empty.\n", friday.getResponse("contact list"));
        assertResponse("Contact logged. Here's the entry:\n  Bob (phone: 81234567, email: -)\n"
                + "Now you have 1 contact in the list.\n"
                + "Warning: This contact change is only in memory; contact saving is disabled "
                + "until you fix the file and restart.\n", friday.getResponse("contact add Bob /phone 81234567"));
        friday.getResponse("todo still saved");
        assertEquals("T|0|still saved" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("friday.txt")));
        assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    void getResponse_corruptTaskFile_leavesContactSavingEnabled() throws IOException {
        Path taskFile = temporaryDirectory.resolve("friday.txt");
        Files.writeString(taskFile, "broken task data\n");
        Friday friday = new Friday(taskFile);
        friday.getResponse("contact add Alice /email alice@example.com");
        assertEquals("C|Alice||alice@example.com" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("contacts.txt")));
        assertEquals("broken task data\n", Files.readString(taskFile));
    }

    @Test
    void getResponse_contactSaveFailure_retainsMemoryAndRetriesFullSnapshot() throws IOException {
        Path taskFile = temporaryDirectory.resolve("friday.txt");
        Path contactFile = temporaryDirectory.resolve("contacts.txt");
        Friday friday = new Friday(taskFile);
        Files.createDirectory(contactFile);
        Path blocker = contactFile.resolve("blocker");
        Files.writeString(blocker, "keep");
        assertResponse("Contact logged. Here's the entry:\n  Alice (phone: 91234567, email: -)\n"
                + "Now you have 1 contact in the list.\n"
                + "Warning: I couldn't save data/contacts.txt. Your contact changes are only in memory; "
                + "check the folder and file permissions.\n", friday.getResponse("contact add Alice /phone 91234567"));
        assertTrue(friday.getResponse("contact list").contains("1.Alice"));
        Files.delete(blocker);
        Files.delete(contactFile);
        assertFalse(friday.getResponse("contact add Bob /phone 81234567").contains("Warning:"));
        assertEquals(2, Files.readAllLines(contactFile).size());
        assertTrue(new Friday(taskFile).getResponse("contact list").contains("2.Bob"));
    }

    @Test
    void constructor_explicitContactPath_usesIndependentFiles() throws IOException {
        Path tasks = temporaryDirectory.resolve("tasks/save.txt");
        Path contacts = temporaryDirectory.resolve("people/save.txt");
        Friday friday = new Friday(tasks, contacts);
        friday.getResponse("contact add Alice /phone 91234567");
        friday.getResponse("todo task");
        assertEquals("C|Alice|91234567|" + System.lineSeparator(), Files.readString(contacts));
        assertEquals("T|0|task" + System.lineSeparator(), Files.readString(tasks));
    }

    @Test
    void getResponse_help_includesEveryCommandAndDateFormat() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        assertResponse("""
                Command briefing:
                Tasks:
                  todo DESCRIPTION
                  deadline DESCRIPTION /by DEADLINE
                  event DESCRIPTION /from START /to END
                  list
                  find KEYWORD
                  on yyyy-MM-dd
                  mark TASK_NUMBER
                  unmark TASK_NUMBER
                  delete TASK_NUMBER
                Dates: yyyy-MM-dd, yyyy-MM-dd HH:mm, or d/M/yyyy HHmm
                Contacts:
                  contact add NAME /phone PHONE [/email EMAIL]
                  contact add NAME /email EMAIL
                  contact list
                  contact find KEYWORD
                  contact delete NUMBER
                Session: hello | thanks | help | bye
                """, friday.getResponse("help"));
    }

    @Test
    void getResponse_personalityCommands_useConsistentVoice() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        assertEquals(SEPARATOR + "FRIDAY online. Let's keep your day under control.\n" + SEPARATOR,
                friday.getWelcomeMessage().replace("\r\n", "\n"));
        assertResponse("At your service. What's the next task?\n", friday.getResponse("hello"));
        assertResponse("Anytime. Organization is rather my specialty.\n", friday.getResponse("thanks"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "todo", "mark 99", "deadline work /by invalid",
            "contact add", "contact delete 1"})
    void getCommandResponse_invalidInput_reportsErrorAndRecovers(String command) {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        String before = friday.getResponse("list");
        assertEquals(Response.Severity.ERROR, friday.getCommandResponse(command).severity());
        Response recovered = friday.getCommandResponse("list");
        assertEquals(Response.Severity.NORMAL, recovered.severity());
        assertEquals(before, recovered.text());
    }

    @ParameterizedTest
    @ValueSource(strings = {"list", "find missing", "contact list", "contact find missing", "help",
            "todo Warning: error is just task text"})
    void getCommandResponse_normalOutput_doesNotInferSeverityFromText(String command) {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        assertEquals(Response.Severity.NORMAL, friday.getCommandResponse(command).severity());
    }

    @Test
    void getCommandResponse_structuredAndStringApis_preserveOutputAndExecuteOnce() {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        assertEquals(friday.getWelcomeMessage(), friday.getWelcomeResponse().text());
        assertEquals(Response.Severity.NORMAL, friday.getWelcomeResponse().severity());
        Response added = friday.getCommandResponse("todo one task");
        assertTrue(added.text().contains("Now you have 1 task"));
        assertEquals(friday.getResponse("list"), friday.getCommandResponse("list").text());
        assertFalse(friday.getResponse("list").contains("2.[T]"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"friday.txt", "contacts.txt"})
    void getWelcomeResponse_corruptStorage_warnsAndProtectsOriginal(String fileName) throws IOException {
        Path file = temporaryDirectory.resolve(fileName);
        Files.writeString(file, "corrupt record");
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        assertEquals(Response.Severity.WARNING, friday.getWelcomeResponse().severity());
        String command = fileName.equals("friday.txt") ? "todo new task" : "contact add Alice /phone 91234567";
        assertEquals(Response.Severity.WARNING, friday.getCommandResponse(command).severity());
        assertEquals("corrupt record", Files.readString(file));
        assertEquals(Response.Severity.NORMAL, friday.getCommandResponse("list").severity());
    }

    @ParameterizedTest
    @ValueSource(strings = {"friday.txt", "contacts.txt"})
    void getCommandResponse_saveFailure_warnsAndRetries(String fileName) throws IOException {
        Friday friday = new Friday(temporaryDirectory.resolve("friday.txt"));
        Path file = temporaryDirectory.resolve(fileName);
        Files.createDirectory(file);
        Path blocker = file.resolve("blocker");
        Files.writeString(blocker, "keep");
        String first = fileName.equals("friday.txt") ? "todo first" : "contact add Alice /phone 91234567";
        String second = fileName.equals("friday.txt") ? "todo second" : "contact add Bob /phone 81234567";
        assertEquals(Response.Severity.WARNING, friday.getCommandResponse(first).severity());
        assertEquals("keep", Files.readString(blocker));
        Files.delete(blocker);
        Files.delete(file);
        assertEquals(Response.Severity.NORMAL, friday.getCommandResponse(second).severity());
        assertEquals(2, Files.readAllLines(file).size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark 1", "unmark 1"})
    void getCommandResponse_unchangedTaskStatus_doesNotRetryBlockedSave(String command) throws IOException {
        Path file = temporaryDirectory.resolve("friday.txt");
        Friday friday = new Friday(file);
        friday.getResponse("todo keep task");
        friday.getResponse(command);
        String before = friday.getResponse("list");
        Files.delete(file);
        Files.createDirectory(file);
        Path blocker = file.resolve("blocker");
        Files.writeString(blocker, "keep");

        Response response = friday.getCommandResponse(command);

        assertEquals(Response.Severity.NORMAL, response.severity());
        assertFalse(response.text().contains("Warning:"));
        assertEquals(before, friday.getResponse("list"));
        assertEquals("keep", Files.readString(blocker));
    }

    /** Compares the GUI-facing response while accommodating native Windows line endings. */
    private static void assertResponse(String body, String actual) {
        assertEquals(SEPARATOR + body, actual.replace("\r\n", "\n"));
    }
}
