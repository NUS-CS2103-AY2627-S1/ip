package friday;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import friday.contact.Contact;
import friday.contact.ContactList;
import friday.parser.ContactParser;
import friday.parser.Parser;
import friday.storage.ContactStorage;
import friday.storage.Storage;
import friday.task.Task;
import friday.task.TaskList;
import friday.ui.Ui;

/** Friday's command engine. It owns task and contact state while exposing text responses for any UI. */
public class Friday {
    private static final String INVALID_TASK_NUMBER_MESSAGE = "The task number is invalid.";
    private static final Path DATA_FILE = Path.of("data", "friday.txt");

    private final Storage storage;
    private final ContactStorage contactStorage;
    private final Ui ui;
    private TaskList tasks;
    private ContactList contacts;
    private boolean isContactSavingEnabled;
    private boolean hasContactLoadingError;
    private boolean isSavingEnabled;
    private boolean hasLoadingError;
    private boolean hasExited;
    private Response.Severity responseSeverity = Response.Severity.NORMAL;

    /** Loads Friday's default task and contact data files. */
    public Friday() {
        this(DATA_FILE);
    }

    /**
     * Loads tasks from the supplied path and contacts from its sibling contacts.txt file.
     * Keeps the session usable if either saved collection cannot be read.
     *
     * @param dataFile location of Friday's persistent task file.
     */
    public Friday(Path dataFile) {
        this(dataFile, dataFile.resolveSibling("contacts.txt"));
    }

    /**
     * Loads independent task and contact files, isolating failures in either collection.
     *
     * @param dataFile location of the task file.
     * @param contactFile location of the contact file.
     */
    public Friday(Path dataFile, Path contactFile) {
        storage = new Storage(dataFile);
        contactStorage = new ContactStorage(contactFile);
        ui = new Ui();
        tasks = new TaskList();
        isSavingEnabled = true;
        contacts = new ContactList();
        isContactSavingEnabled = true;
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | SecurityException e) {
            isSavingEnabled = false;
            hasLoadingError = true;
        }
        try {
            contacts = new ContactList(contactStorage.load());
        } catch (IOException | SecurityException e) {
            isContactSavingEnabled = false;
            hasContactLoadingError = true;
        }
    }

    /** Runs the console interface while reusing the response API used by the JavaFX GUI. */
    public static void main(String[] args) {
        Friday friday = new Friday();
        System.out.print(friday.getWelcomeMessage());
        while (friday.ui.hasNextCommand() && !friday.hasExited) {
            System.out.print(friday.getResponse(friday.ui.readCommand()));
        }
    }

    /**
     * Returns Friday's initial welcome and any storage warning as display-ready text.
     *
     * @return the welcome response for a user interface.
     */
    public synchronized String getWelcomeMessage() {
        return getWelcomeResponse().text();
    }

    /** Returns the welcome text with warning metadata when saved data could not be loaded. */
    public synchronized Response getWelcomeResponse() {
        return captureResponse(() -> {
            ui.showWelcome();
            if (hasLoadingError) {
                responseSeverity = Response.Severity.WARNING;
                ui.showLoadingError();
            }
            if (hasContactLoadingError) {
                responseSeverity = Response.Severity.WARNING;
                ui.showContactLoadingError();
            }
            ui.showLine();
        });
    }

    /**
     * Processes one user command and returns all resulting output for a user interface to display.
     *
     * @param command unmodified command text from the user.
     * @return Friday's response to the command.
     */
    public synchronized String getResponse(String command) {
        return getCommandResponse(command).text();
    }

    /**
     * Processes a command once and returns text plus presentation metadata.
     *
     * @param command command text supplied by the user.
     * @return the command output and its severity.
     */
    public synchronized Response getCommandResponse(String command) {
        return captureResponse(() -> processCommand(command));
    }

    /**
     * Returns whether the user has entered {@code bye}; UIs can disable further input if desired.
     *
     * @return true after Friday has processed a bye command.
     */
    public boolean hasExited() {
        return hasExited;
    }

    /** Executes a command through the existing parser, task list, storage, and presentation classes. */
    private void processCommand(String command) {
        ui.showLine();
        try {
            boolean hasChanged = dispatchCommand(command);
            if (hasChanged) {
                saveTasks();
            }
        } catch (IllegalArgumentException e) {
            responseSeverity = Response.Severity.ERROR;
            ui.showError(e.getMessage());
        }
    }

    /** Routes a command and reports whether task storage needs updating. */
    private boolean dispatchCommand(String command) {
        switch (Parser.parseCommandType(command)) {
            case BYE -> endSession();
            case HELLO -> ui.showGreeting();
            case THANKS -> ui.showThanks();
            case HELP -> ui.showHelp();
            case CONTACT -> processContactCommand(command);
            case TODO, DEADLINE, EVENT -> {
                return addTask(command);
            }
            case DELETE -> {
                return deleteTask(command);
            }
            case MARK -> {
                return markTask(command);
            }
            case UNMARK -> {
                return unmarkTask(command);
            }
            case ON -> listTasksOn(Parser.parseDate(command));
            case FIND -> listMatchingTasks(Parser.parseFindKeyword(command));
            case LIST -> showTaskList();
        }
        return false;
    }

    /** Shows the farewell and marks the session as ended. */
    private void endSession() {
        ui.showGoodbye();
        hasExited = true;
    }

    /** Adds and presents a validated task, reporting that persistence is needed. */
    private boolean addTask(String command) {
        int taskCountBeforeAdd = tasks.size();
        Task task = Parser.parseTask(command);
        tasks.add(task);
        assert tasks.size() == taskCountBeforeAdd + 1
                : "Adding a task must increase the task count by one.";
        ui.showTaskAdded(task, tasks.size());
        return true;
    }

    /** Deletes and presents an existing task, reporting that persistence is needed. */
    private boolean deleteTask(String command) {
        int taskNumber = requireExistingTaskNumber(command);
        int taskCountBeforeDelete = tasks.size();
        Task removedTask = tasks.delete(taskNumber);
        assert tasks.size() == taskCountBeforeDelete - 1
                : "Deleting an existing task must decrease the task count by one.";
        ui.showTaskDeleted(removedTask, tasks.size());
        return true;
    }

    /** Marks a task complete and reports whether its status changed. */
    private boolean markTask(String command) {
        int taskNumber = requireExistingTaskNumber(command);
        boolean hasChanged = tasks.mark(taskNumber);
        ui.showTaskMarked(tasks.get(taskNumber));
        return hasChanged;
    }

    /** Reopens a task and distinguishes a change from an already-incomplete task. */
    private boolean unmarkTask(String command) {
        int taskNumber = requireExistingTaskNumber(command);
        boolean hasChanged = tasks.unmark(taskNumber);
        if (hasChanged) {
            ui.showTaskUnmarked(tasks.get(taskNumber));
        } else {
            ui.showAlreadyUnmarked();
        }
        return hasChanged;
    }

    /** Executes contact operations and saves only after a successful contact mutation. */
    private void processContactCommand(String command) {
        switch (ContactParser.parseCommandType(command)) {
            case ADD -> addContact(command);
            case LIST -> showContacts();
            case FIND -> showMatchingContacts(command);
            case DELETE -> deleteContact(command);
        }
    }

    /** Adds a parsed contact, presents it, and persists the changed directory. */
    private void addContact(String command) {
        Contact contact = ContactParser.parseContact(command);
        contacts.add(contact);
        ui.showContactAdded(contact, contacts.size());
        saveContacts();
    }

    /** Shows all contacts, or an empty-directory message when there are none. */
    private void showContacts() {
        if (contacts.size() == 0) {
            ui.showError("Your contact directory is empty.");
            return;
        }
        ui.showContactListHeader(false);
        for (int number = 1; number <= contacts.size(); number++) {
            ui.showNumberedContact(number, contacts.get(number));
        }
    }

    /** Shows contacts matching the supplied find command without changing the directory. */
    private void showMatchingContacts(String command) {
        List<Integer> matches = contacts.findContactNumbersContaining(ContactParser.parseFindKeyword(command));
        if (matches.isEmpty()) {
            ui.showError("Contact scan complete. No matches found.");
            return;
        }
        ui.showContactListHeader(true);
        for (int number : matches) {
            ui.showNumberedContact(number, contacts.get(number));
        }
    }

    /** Deletes the selected contact, presents it, and persists the changed directory. */
    private void deleteContact(String command) {
        Contact removed = contacts.delete(ContactParser.parseContactNumber(command));
        ui.showContactDeleted(removed, contacts.size());
        saveContacts();
    }

    /** Keeps contact recovery independent from task saving and retries after ordinary write failures. */
    private void saveContacts() {
        if (!isContactSavingEnabled) {
            responseSeverity = Response.Severity.WARNING;
            ui.showContactSavingDisabled();
            return;
        }
        try {
            contactStorage.save(contacts.toList());
        } catch (IOException | SecurityException e) {
            responseSeverity = Response.Severity.WARNING;
            ui.showContactSavingError();
        }
    }

    /** Captures the existing console UI output so it can be rendered in a GUI without duplicating messages. */
    private Response captureResponse(Runnable action) {
        responseSeverity = Response.Severity.NORMAL;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8)) {
            System.setOut(responseOutput);
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return new Response(capturedOutput.toString(StandardCharsets.UTF_8), responseSeverity);
    }

    /** Checks parsed task numbers against the current list; the parser does not depend on list state. */
    private int requireExistingTaskNumber(String command) {
        int taskNumber = Parser.parseTaskNumber(command);
        if (!tasks.isValidTaskNumber(taskNumber)) {
            throw new IllegalArgumentException(INVALID_TASK_NUMBER_MESSAGE);
        }
        return taskNumber;
    }

    /** Shows all tasks with their original numbers. */
    private void showTaskList() {
        ui.showTaskListHeader();
        for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
            ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
        }
    }

    /** Shows dated tasks using their original list numbers without modifying or saving the list. */
    private void listTasksOn(LocalDate date) {
        ui.showDateHeader(date);
        List<Integer> matches = tasks.findTaskNumbersOn(date);
        if (matches.isEmpty()) {
            ui.showNoTasksOnDate();
        }
        for (int taskNumber : matches) {
            ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
        }
    }

    /** Shows keyword matches using original list numbers without modifying or saving the list. */
    private void listMatchingTasks(String keyword) {
        ui.showFindHeader();
        List<Integer> matches = tasks.findTaskNumbersContaining(keyword);
        if (matches.isEmpty()) {
            ui.showNoMatchingTasks();
        }
        for (int taskNumber : matches) {
            ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
        }
    }

    /** Reports storage failures without terminating the command session or discarding in-memory tasks. */
    private void saveTasks() {
        if (!isSavingEnabled) {
            responseSeverity = Response.Severity.WARNING;
            ui.showSavingDisabled();
            return;
        }
        try {
            storage.save(tasks.toList());
        } catch (IOException | SecurityException e) {
            responseSeverity = Response.Severity.WARNING;
            ui.showSavingError();
        }
    }
}
