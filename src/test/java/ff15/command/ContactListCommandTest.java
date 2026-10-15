package ff15.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.Contact;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Tests that {@code contact list} shows every contact, numbered, under its header. */
public class ContactListCommandTest {

    @TempDir
    Path tempDir;

    private PrintStream realOut;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        realOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.resolve("t.txt").toString(), tempDir.resolve("c.txt").toString());
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_emptyList_printsOnlyTheHeader() {
        new ContactListCommand().execute(new TaskList(), new ContactList(), ui, storage);
        assertEquals("Here are the contacts in your list:", ui.drainTranscript());
    }

    @Test
    public void execute_withContacts_listsThemNumbered() {
        ContactList contacts = new ContactList();
        contacts.add(new Contact("John", "", ""), new Contact("Mary", "91234567", ""));

        new ContactListCommand().execute(new TaskList(), contacts, ui, storage);

        String[] lines = ui.drainTranscript().split(System.lineSeparator());
        assertEquals("1.John", lines[1]);
        assertEquals("2.Mary (phone: 91234567)", lines[2]);
    }
}
