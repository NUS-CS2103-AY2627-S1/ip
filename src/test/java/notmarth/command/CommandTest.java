package notmarth.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import notmarth.model.Contact;
import notmarth.model.ContactList;
import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.TaskList;
import notmarth.model.ToDo;
import notmarth.storage.Storage;
import notmarth.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests executable command behavior and command-side persistence. */
class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that task mutation commands update state, output, and the archive. */
    @Test
    void mutationCommands_validInputs_updateStateAndArchive() throws Exception {
        Path archive = temporaryDirectory.resolve("plan.txt");
        Storage storage = new Storage(archive.toString());
        TaskList tasks = new TaskList(List.of(), 5);
        ContactList contacts = new ContactList();
        Ui ui = new Ui();

        new AddCommand(new ToDo("first")).execute(tasks, contacts, ui, storage);
        new MarkCommand(1).execute(tasks, contacts, ui, storage);
        new UnmarkCommand(1).execute(tasks, contacts, ui, storage);
        new DeleteCommand(1).execute(tasks, contacts, ui, storage);

        assertTrue(tasks.isEmpty());
        assertTrue(Files.exists(archive));
        assertTrue(Files.readString(archive).startsWith("# NotMarth battle plan v1"));
    }

    /** Verifies that contacts are added, searched, listed, and deleted independently of tasks. */
    @Test
    void contactCommands_validInputs_updateContactStateAndArchive() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("contacts.txt").toString());
        TaskList tasks = new TaskList(List.of(), 5);
        ContactList contacts = new ContactList();
        Ui ui = new Ui();
        Contact contact = new Contact("Mrs Tan", "81234567", "Engage Road");

        new AddContactCommand(contact).execute(tasks, contacts, ui, storage);
        new FindContactCommand("tan").execute(tasks, contacts, ui, storage);
        new ListContactsCommand().execute(tasks, contacts, ui, storage);
        new DeleteContactCommand(1).execute(tasks, contacts, ui, storage);

        assertEquals(0, contacts.size());
        assertTrue(Files.readString(temporaryDirectory.resolve("contacts.txt")).contains(
                "# NotMarth battle plan v1"));
    }

    /** Verifies that search and date commands render matching and empty results. */
    @Test
    void queryCommands_matchingAndEmptyResults_renderExpectedMessages() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new ToDo("Read book"),
                new Deadline("Return book", LocalDate.of(2019, 10, 15)),
                new Event("Planning", LocalDate.of(2019, 10, 14), LocalDate.of(2019, 10, 16))), 5);
        ContactList contacts = new ContactList();
        Ui ui = new Ui();
        Storage storage = new Storage(temporaryDirectory.resolve("query.txt").toString());

        String output = captureOutput(() -> {
            new ListCommand().execute(tasks, contacts, ui, storage);
            new FindCommand("BOOK").execute(tasks, contacts, ui, storage);
            new OnCommand(LocalDate.of(2019, 10, 15)).execute(tasks, contacts, ui, storage);
            new FindCommand("dragon").execute(tasks, contacts, ui, storage);
            new OnCommand(LocalDate.of(2020, 1, 1)).execute(tasks, contacts, ui, storage);
        });

        assertTrue(output.contains("Here are your current mission orders:"));
        assertTrue(output.contains("1.[T][ ] Read book"));
        assertTrue(output.contains("2.[D][ ] Return book"));
        assertTrue(output.contains("3.[E][ ] Planning"));
        assertTrue(output.contains("No tasks in your list match \"dragon\"."));
        assertTrue(output.contains("No deadlines or events are scheduled for Jan 01 2020."));
    }

    /** Verifies that exit and Easter-egg commands have the correct control behavior and output. */
    @Test
    void controlCommands_exitAndSommie_reportExpectedBehavior() throws Exception {
        TaskList tasks = new TaskList(List.of(), 5);
        ContactList contacts = new ContactList();
        Ui ui = new Ui();
        Storage storage = new Storage(temporaryDirectory.resolve("control.txt").toString());

        ExitCommand exit = new ExitCommand();
        assertTrue(exit.isExit());
        assertFalse(new SommieCommand().isExit());
        String output = captureOutput(() -> {
            exit.execute(tasks, contacts, ui, storage);
            new SommieCommand().execute(tasks, contacts, ui, storage);
        });

        assertTrue(output.contains("Until we meet again."));
        assertTrue(output.contains("Sommie appears"));
    }

    /** Verifies that a failed save is reported without losing the in-memory task. */
    @Test
    void addCommand_saveFailure_keepsSessionStateAndReportsError() throws Exception {
        Path blockingPath = temporaryDirectory.resolve("blocking");
        Files.writeString(blockingPath, "not a directory");
        Storage storage = new Storage(blockingPath.resolve("plan.txt").toString());
        TaskList tasks = new TaskList(List.of(), 5);
        ContactList contacts = new ContactList();

        String output = captureOutput(() -> new AddCommand(new ToDo("survive save failure"))
                .execute(tasks, contacts, new Ui(), storage));

        assertEquals(1, tasks.size());
        assertTrue(output.contains("I couldn't save the battle plan to disk."));
        assertTrue(output.contains("survive save failure"));
    }

    /** Verifies that contact commands also survive a failed archive save. */
    @Test
    void contactCommands_saveFailure_keepSessionStateAndReportError() throws Exception {
        Path blockingPath = temporaryDirectory.resolve("contact-blocking");
        Files.writeString(blockingPath, "not a directory");
        Storage storage = new Storage(blockingPath.resolve("plan.txt").toString());
        TaskList tasks = new TaskList(List.of(), 5);
        Contact contact = new Contact("Mrs Tan", "81234567", "Engage Road");
        ContactList contacts = new ContactList(List.of(contact));
        Ui ui = new Ui();

        String output = captureOutput(() -> {
            new AddContactCommand(new Contact("Alear", "1", "Somniel"))
                    .execute(tasks, contacts, ui, storage);
            new DeleteContactCommand(1).execute(tasks, contacts, ui, storage);
        });

        assertEquals(1, contacts.size());
        assertTrue(output.contains("I couldn't save the battle plan to disk."));
    }

    /** Verifies that result counts use singular and plural nouns correctly. */
    @Test
    void countMessages_singularAndPluralCounts_useCorrectGrammar() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("counts.txt").toString());
        TaskList tasks = new TaskList(List.of(), 5);
        ContactList contacts = new ContactList();
        Ui ui = new Ui();

        String output = captureOutput(() -> {
            new AddCommand(new ToDo("first order")).execute(tasks, contacts, ui, storage);
            new AddCommand(new ToDo("second order")).execute(tasks, contacts, ui, storage);
            new AddContactCommand(new Contact("Vander", "1", "Somniel"))
                    .execute(tasks, contacts, ui, storage);
        });

        assertTrue(output.contains("Now you have 1 task in the list."));
        assertTrue(output.contains("Now you have 2 tasks in the list."));
        assertTrue(output.contains("Now you have 1 contact in the list."));
    }

    private String captureOutput(ThrowingAction action) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }
}
