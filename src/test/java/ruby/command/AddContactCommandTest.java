package ruby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

class AddContactCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void execute_validContact_addsContactAndReportsCount() throws RubyException {
        ContactList contacts = new ContactList();

        String response = new AddContactCommand("John Doe", "91234567", "john@example.com", "")
                .execute(new TaskList(), contacts, new Storage(tempDir.resolve("ruby.txt").toString()));

        assertEquals("Saved. I never forget a name:\n"
                + "  John Doe | 91234567 | john@example.com\n"
                + "That's 1 contact in your circle.", response);
    }

    @Test
    void execute_contactWithoutOptionalFields_addsContact() throws RubyException {
        ContactList contacts = new ContactList();

        String response = new AddContactCommand("Jane", "", "", "")
                .execute(new TaskList(), contacts, new Storage(tempDir.resolve("ruby.txt").toString()));

        assertEquals("Saved. I never forget a name:\n"
                + "  Jane\n"
                + "That's 1 contact in your circle.", response);
    }
}
