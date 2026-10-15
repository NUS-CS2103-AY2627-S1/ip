package laby;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import laby.contact.ContactList;
import laby.task.TaskList;

/** Tests contact-related console output produced by {@link Ui}. */
class UiTest {

    @Test
    void getContacts_displaysNumberedContacts() {
        TaskList taskList = new TaskList(new ArrayList<>());
        ContactList contactList = new ContactList(new ArrayList<>());
        contactList.addContact("John Doe", "91234567", "john@example.com");

        assertEquals("Here are the tasks and contacts in your list.\n\nTasks:\n\nContacts:\n"
                        + "1.John Doe | Phone: 91234567 | Email: john@example.com\n",
                Ui.getItems(taskList, contactList));
    }

    @Test
    void contactMessages_displayContactAndCount() {
        String contact = "John Doe | Phone: 91234567 | Email: john@example.com";

        assertEquals("Laby has added the contact. Did Chief find a better assistant :(\n  " + contact + "\n",
                Ui.getAddContact(contact));
        assertEquals("Laby has deleted the contact. Seems like Laby is enough :D\n  " + contact + "\n",
                Ui.getDeleteContact(contact));
        assertEquals("There is a total of 1 contact in your list.\n",
                Ui.getNumberOfContacts(1));
    }

    @Test
    void taskMessages_displayCountsAndResponses() {
        assertEquals("There is a total of 0 task in your list.\n", Ui.getNumberOfTasks(0));
        assertEquals("There is a total of 2 tasks in your list.\n", Ui.getNumberOfTasks(2));
        assertEquals("Understood. Laby has marked the task as done.\n  [T][X] read book\n",
                Ui.getMarkTask("[T][X] read book"));
        assertEquals("Understood. Laby has marked the task as not done.\n  [T][ ] read book\n",
                Ui.getUnmarkTask("[T][ ] read book"));
        assertEquals("Laby has added the task. Make sure to rest, Chief :o\n  [T][ ] read book\n",
                Ui.getAddTask("[T][ ] read book"));
        assertEquals("Laby has deleted the task. Glad to see you resting ;)\n  [T][ ] read book\n",
                Ui.getDeleteTask("[T][ ] read book"));
    }

    @Test
    void commonMessages_includeExpectedDividersAndHeaders() {
        assertEquals("Goodbye. Switching to rest mode.\n", Ui.getExitMessage());
        assertEquals("Error detected... bad input\n", Ui.getError(new LabyException("bad input")));
        assertEquals("____________________________________________________________\n\nreply\n"
                        + "____________________________________________________________\n\n",
                Ui.addDivider("reply\n"));
    }
}
