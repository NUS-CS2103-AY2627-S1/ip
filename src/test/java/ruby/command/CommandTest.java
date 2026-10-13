package ruby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ruby.RubyException;
import ruby.contact.Contact;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.Deadline;
import ruby.task.Event;
import ruby.task.TaskList;

class CommandTest {

    @TempDir
    Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("ruby.txt").toString());
    }

    @Test
    void execute_taskChangingCommands_updateListAndSave() throws Exception {
        TaskList tasks = new TaskList();
        ContactList contacts = new ContactList();
        Storage storage = storage();

        assertEquals("Added to the collection:\n  [T][ ] read book\nThat's 1 task on your plate.",
                new TodoCommand("read book").execute(tasks, contacts, storage));
        assertEquals("Added to the collection:\n  [D][ ] return book (by: Aug 28 2026, 18:00)\n"
                        + "That's 2 tasks on your plate.",
                new DeadlineCommand(new Deadline("return book", LocalDateTime.of(2026, 8, 28, 18, 0)))
                        .execute(tasks, contacts, storage));
        assertEquals("Added to the collection:\n  [E][ ] meeting (from: Aug 29 2026 to: Aug 30 2026)\n"
                        + "That's 3 tasks on your plate.",
                new EventCommand(new Event("meeting", LocalDateTime.of(2026, 8, 29, 0, 0),
                        LocalDateTime.of(2026, 8, 30, 0, 0))).execute(tasks, contacts, storage));
        assertEquals("Done — consider it polished:\n  [D][X] return book (by: Aug 28 2026, 18:00)",
                new MarkCommand(1).execute(tasks, contacts, storage));
        assertEquals("Undone — brilliance takes time:\n  [D][ ] return book (by: Aug 28 2026, 18:00)",
                new UnmarkCommand(1).execute(tasks, contacts, storage));
        assertEquals("Removed — gone without a trace:\n  [T][ ] read book\nThat leaves 2 tasks on your plate.",
                new DeleteCommand(0).execute(tasks, contacts, storage));
        assertEquals("D | 0 | return book | 2026-08-28T18:00\nE | 0 | meeting | 2026-08-29T00:00 | 2026-08-30T00:00",
                Files.readString(tempDir.resolve("ruby.txt")));
    }

    @Test
    void execute_readOnlyCommands_returnListFindAndFarewell() throws RubyException {
        TaskList tasks = new TaskList();
        tasks.addItem(new ruby.task.Todo("read book"));
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("Jane", "", "", ""));

        assertEquals("Here's everything on your plate:\n1. [T][ ] read book",
                new ListCommand().execute(tasks, contacts, storage()));
        assertEquals("Found them — I never miss:\n1. [T][ ] read book",
                new FindCommand("book").execute(tasks, contacts, storage()));
        assertEquals("Your circle, as requested:\n1. Jane",
                new ListContactsCommand().execute(tasks, contacts, storage()));
        assertEquals("Bye. Your tasks are in precious hands.",
                new ExitCommand().execute(tasks, contacts, storage()));
    }

    @Test
    void execute_deleteContactCommand_removesAndSavesContact() throws Exception {
        TaskList tasks = new TaskList();
        ContactList contacts = new ContactList();
        contacts.addContact(new Contact("Jane", "", "", ""));
        contacts.addContact(new Contact("John", "", "", ""));

        assertEquals("Removed from your circle:\n  Jane\nThat leaves 1 contact in your circle.",
                new DeleteContactCommand(0).execute(tasks, contacts, storage()));
        assertEquals("C | John |  |  | ", Files.readString(tempDir.resolve("ruby.txt")));
    }
}
