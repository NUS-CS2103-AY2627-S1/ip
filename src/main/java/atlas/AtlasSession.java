package atlas;

import java.util.ArrayList;

import atlas.client.Client;
import atlas.client.ClientList;
import atlas.command.ClientCommand;
import atlas.command.Command;
import atlas.command.Parser;
import atlas.storage.Storage;
import atlas.task.Priority;
import atlas.task.Task;
import atlas.task.TaskList;

/**
 * Executes one user command at a time and reports Atlas's reply through an
 * output sink. Both the text interface and the GUI drive this class, so the
 * two front-ends behave identically.
 */
public class AtlasSession {

    /** Default location of the file used to persist Atlas tasks and clients. */
    public static final String DEFAULT_DATA_FILE = "./data/atlas.txt";
    /** Greeting shown when Atlas starts. */
    public static final String HELLO_MESSAGE = "Hello! I'm Atlas, your personal assistant.";
    /** Prompt shown after the greeting. */
    public static final String PROMPT_MESSAGE = "What can I do for you?";
    /** Farewell shown when the user exits. */
    public static final String GOODBYE_MESSAGE = "Goodbye. Atlas signing off. See you soon!";
    /** Message shown when a client delete command carries no number. */
    private static final String CLIENT_NUMBER_MISSING_MESSAGE =
            "Which client shall I release? Use: client delete <number>";
    /** Message shown when a client number does not name an existing client. */
    private static final String CLIENT_NUMBER_INVALID_MESSAGE =
            "No such client in the pantheon. Use: client delete <number>";
    /** Message shown when a priority number does not name an existing task. */
    private static final String PRIORITY_NUMBER_INVALID_MESSAGE =
            "No such task in the pantheon. Use: " + Parser.PRIORITY_SYNTAX;

    /**
     * Receives the messages Atlas produces while handling a command. A
     * "speak" message is a reply that forms its own speech bubble; a "print"
     * message is a plain line, used for task listings.
     */
    public interface Output {
        /** Reports a reply message that forms its own speech bubble. */
        void speak(String message);

        /** Reports a plain line of output, e.g. one task listing row. */
        void print(String text);

        /**
         * Reports a message that explains an error, so a front-end can show it
         * in a style that catches the user's attention. The default keeps every
         * existing sink working unchanged.
         *
         * @param message the error message to report.
         */
        default void speakError(String message) {
            speak(message);
        }
    }

    private final Storage storage;
    private final TaskList tasks;
    private final ClientList clients;
    private final Output output;

    /**
     * Creates a session that executes commands against the given lists.
     *
     * @param storage storage used to persist task-list and client-list changes.
     * @param tasks task list the session operates on.
     * @param clients client list the session operates on.
     * @param output sink that receives Atlas's replies.
     */
    public AtlasSession(Storage storage, TaskList tasks, ClientList clients, Output output) {
        this.storage = storage;
        this.tasks = tasks;
        this.clients = clients;
        this.output = output;
    }

    /**
     * Handles one user command line: parses it, mutates the task list or the
     * client list as needed, and reports the reply through this session's
     * output sink.
     *
     * @param rawLine the full command line typed by the user.
     */
    public void respond(String rawLine) {
        // Leading whitespace is insignificant, so " list" is still the list
        // command. Trailing whitespace keeps its meaning, because a marker's
        // closing space is what proves a value was left empty.
        String line = rawLine.stripLeading();
        try {
            Command cmd = Parser.parseCommand(line);
            if (cmd == null) {
                throw new AtlasException("The Oracle is silent on that word. "
                        + "Try: todo, deadline, event, list, mark, unmark, delete, find, priority, client, bye.");
            }
            dispatchTaskCommand(line, cmd);
        } catch (AtlasException e) {
            output.speakError(e.getMessage());
        }
    }

    /**
     * Runs the handler that belongs to one task command. Each handler keeps
     * one command's own work, so no single method carries the whole command
     * set.
     *
     * @param line the full command line typed by the user.
     * @param cmd the command parsed from that line.
     * @throws AtlasException if the command is malformed.
     */
    private void dispatchTaskCommand(String line, Command cmd) throws AtlasException {
        if (cmd == Command.BYE) {
            // Unreachable: callers exit before dispatching "bye".
            return;
        }
        switch (cmd) {
            case LIST -> listTasks();
            case MARK -> markTask(line, cmd);
            case UNMARK -> unmarkTask(line, cmd);
            case DELETE -> deleteTask(line, cmd);
            case FIND -> findTasks(line, cmd);
            case PRIORITY -> rankTask(line);
            case TODO, DEADLINE, EVENT -> addTask(line, cmd);
            case CLIENT -> respondToClient(line);
            default -> throw new AssertionError("Every command is handled above");
        }
    }

    /**
     * Reports every task, one line per task, or says the list is empty.
     */
    private void listTasks() {
        if (tasks.isEmpty()) {
            output.speak("Your list is empty.");
            return;
        }
        output.speak("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.print((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Marks one task as done.
     *
     * @param line the full command line typed by the user.
     * @param cmd the command whose task number is being parsed.
     * @throws AtlasException if the number is missing, is not a number, or
     *     falls outside the current task list.
     */
    private void markTask(String line, Command cmd) throws AtlasException {
        int index = parseTaskNumber(line, cmd, "Which labour is complete? Use: mark <number>");
        tasks.get(index - 1).markAsDone();
        saveAll();
        output.speak("Nice! I've marked this task as done:");
        assert index >= 1 && index <= tasks.size()
                : "the marked task must still be in the list";
        output.speak("  " + tasks.get(index - 1));
    }

    /**
     * Marks one task as not done yet.
     *
     * @param line the full command line typed by the user.
     * @param cmd the command whose task number is being parsed.
     * @throws AtlasException if the number is missing, is not a number, or
     *     falls outside the current task list.
     */
    private void unmarkTask(String line, Command cmd) throws AtlasException {
        int index = parseTaskNumber(line, cmd,
                "Which labour is not complete? Use: unmark <number>");
        tasks.get(index - 1).markAsNotDone();
        saveAll();
        output.speak("OK, I've marked this task as not done yet:");
        assert index >= 1 && index <= tasks.size()
                : "the unmarked task must still be in the list";
        output.speak("  " + tasks.get(index - 1));
    }

    /**
     * Removes one task from the list.
     *
     * @param line the full command line typed by the user.
     * @param cmd the command whose task number is being parsed.
     * @throws AtlasException if the number is missing, is not a number, or
     *     falls outside the current task list.
     */
    private void deleteTask(String line, Command cmd) throws AtlasException {
        int index = parseTaskNumber(line, cmd, "Which labour shall I release? Use: delete <number>");
        Task removed = tasks.remove(index - 1);
        assert removed != null : "a valid task number always yields a task";
        saveAll();
        output.speak("Got it. I've removed this task:");
        output.speak("  " + removed);
        speakTaskCount();
    }

    /**
     * Reports the tasks whose descriptions contain any of the keywords given.
     *
     * @param line the full command line typed by the user.
     * @param cmd the find command, whose keywords are being parsed.
     * @throws AtlasException if the keywords are malformed.
     */
    private void findTasks(String line, Command cmd) throws AtlasException {
        String[] keywords = Parser.parseKeywords(line, cmd);
        ArrayList<Task> matches = tasks.find(keywords);
        if (matches.isEmpty()) {
            output.speak("The Oracle found no matching tasks.");
            return;
        }
        output.speak("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            output.print((i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Sets, changes or clears the rank of one task.
     *
     * @param line the full command line typed by the user.
     * @throws AtlasException if the number is missing, is not a number, falls
     *     outside the current task list, or names no level Atlas knows.
     */
    private void rankTask(String line) throws AtlasException {
        int index = parsePriorityNumber(line);
        String level = Parser.parsePriorityLevel(line);
        Task ranked = tasks.get(index - 1);
        if (level.equals(Parser.PRIORITY_NONE_WORD)) {
            ranked.clearPriority();
            saveAll();
            output.speak("Noted. I've cleared this task's rank:");
        } else {
            ranked.setPriority(Priority.fromWord(level));
            saveAll();
            output.speak("Noted. I've ranked this task:");
        }
        output.speak("  " + ranked);
    }

    /**
     * Adds one new todo, deadline or event to the list.
     *
     * @param line the full command line typed by the user.
     * @param cmd the command that names the task type to build.
     * @throws AtlasException if the task does not match its command's format.
     */
    private void addTask(String line, Command cmd) throws AtlasException {
        Task added = Parser.parseTask(line, cmd);
        tasks.add(added);
        saveAll();
        output.speak("Got it. I've added this task:");
        output.speak("  " + added);
        speakTaskCount();
    }

    /**
     * Handles one client command line: add, list, find or delete.
     *
     * @param line the full command line typed by the user.
     * @throws AtlasException if the client command is malformed.
     */
    private void respondToClient(String line) throws AtlasException {
        ClientCommand sub = Parser.parseClientSubcommand(line);
        switch (sub) {
            case ADD -> addClient(line);
            case LIST -> listClients();
            case FIND -> findClients(line);
            case DELETE -> deleteClient(line);
            default -> throw new AssertionError("Every client command is handled above");
        }
    }

    /**
     * Adds one new client to the list.
     *
     * @param line the full command line typed by the user.
     * @throws AtlasException if the client command is malformed.
     */
    private void addClient(String line) throws AtlasException {
        Client client = Parser.parseClient(line);
        clients.add(client);
        saveAll();
        output.speak("Got it. I've added this client:");
        output.speak("  " + client);
        speakClientCount();
    }

    /**
     * Reports every client, one line per client, or says the list is empty.
     */
    private void listClients() {
        if (clients.isEmpty()) {
            output.speak("Your client list is empty.");
            return;
        }
        output.speak("Here are your clients:");
        for (int i = 0; i < clients.size(); i++) {
            output.print((i + 1) + "." + clients.get(i));
        }
    }

    /**
     * Reports the clients whose names or details contain every keyword given.
     *
     * @param line the full command line typed by the user.
     * @throws AtlasException if the keywords are malformed.
     */
    private void findClients(String line) throws AtlasException {
        String[] keywords = Parser.parseClientKeywords(line);
        ArrayList<Client> matches = clients.find(keywords);
        if (matches.isEmpty()) {
            output.speak("The Oracle found no matching clients.");
            return;
        }
        output.speak("Here are the matching clients:");
        for (int i = 0; i < matches.size(); i++) {
            output.print((i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Removes one client from the list.
     *
     * @param line the full command line typed by the user.
     * @throws AtlasException if the number is missing, is not a number, or
     *     falls outside the current client list.
     */
    private void deleteClient(String line) throws AtlasException {
        int index = parseClientNumber(line);
        Client removed = clients.remove(index - 1);
        assert removed != null : "a valid client number always yields a client";
        saveAll();
        output.speak("Got it. I've removed this client:");
        output.speak("  " + removed);
        speakClientCount();
    }

    /**
     * Returns the client number that follows a client delete command, after
     * checking that the number was supplied and that it refers to an existing
     * client.
     *
     * @param line the full command line typed by the user.
     * @return the validated 1-based client number.
     * @throws AtlasException if the number is missing, is not a number, or
     *     falls outside the current client list.
     */
    private int parseClientNumber(String line) throws AtlasException {
        String argument = Parser.parseClientDeleteArgument(line);
        if (argument.isEmpty()) {
            throw new AtlasException(CLIENT_NUMBER_MISSING_MESSAGE);
        }
        int index = Parser.parseNumber(argument);
        if (index < 1 || index > clients.size()) {
            throw new AtlasException(CLIENT_NUMBER_INVALID_MESSAGE);
        }
        return index;
    }

    /**
     * Returns the task number that follows a priority command, after checking
     * that it refers to an existing task. A command carrying no number at all
     * is reported by the parser.
     *
     * @param line the full command line typed by the user.
     * @return the validated 1-based task number.
     * @throws AtlasException if the number is missing, is not a number, or
     *     falls outside the current task list.
     */
    private int parsePriorityNumber(String line) throws AtlasException {
        int index = Parser.parsePriorityNumber(line);
        if (index < 1 || index > tasks.size()) {
            throw new AtlasException(PRIORITY_NUMBER_INVALID_MESSAGE);
        }
        return index;
    }

    /**
     * Returns the task number that follows a command, after checking that the
     * number was supplied and that it refers to an existing task.
     *
     * @param line the full command line typed by the user.
     * @param cmd the command whose task number is being parsed.
     * @param missingHint message to report when no number was supplied.
     * @return the validated 1-based task number.
     * @throws AtlasException if the number is missing, is not a number, or
     *     falls outside the current task list.
     */
    private int parseTaskNumber(String line, Command cmd, String missingHint) throws AtlasException {
        if (line.length() == cmd.getWord().length()) {
            throw new AtlasException(missingHint);
        }
        int index = Parser.parseIndex(line, cmd);
        if (index < 1 || index > tasks.size()) {
            throw new AtlasException("No such task in the pantheon. Use: " + cmd.getWord() + " <number>");
        }
        return index;
    }

    /**
     * Saves both lists, since tasks and clients share one storage file.
     *
     * @throws AtlasException if the file cannot be written.
     */
    private void saveAll() throws AtlasException {
        storage.save(tasks.all(), clients.all());
    }

    /**
     * Reports how many tasks the list holds, with the sentence Atlas uses
     * after a task is added or deleted.
     */
    private void speakTaskCount() {
        output.speak("Now you have " + tasks.size() + " task"
                + (tasks.size() == 1 ? "" : "s") + " in the list.");
    }

    /**
     * Reports how many clients the list holds, with the sentence Atlas uses
     * after a client is added or deleted.
     */
    private void speakClientCount() {
        output.speak("Now you have " + clients.size() + " client"
                + (clients.size() == 1 ? "" : "s") + " in the list.");
    }
}
