package wiz;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main application class for the Wiz magical task manager.
 */
public class Wiz {

    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .parseStrict()
            .appendPattern("uuuu-MM-dd HHmm")
            .toFormatter(Locale.US)
            .withResolverStyle(ResolverStyle.STRICT);

    private static final String DEFAULT_FILE_PATH =
            "." + File.separator + "data" + File.separator + "wiz.txt";
    private static final String DEFAULT_CLIENTS_FILE_PATH =
            "." + File.separator + "data" + File.separator + "clients.txt";

    private final Ui ui;
    private final Storage storage;
    private final ClientStorage clientStorage;
    private TaskList tasks;
    private ClientList clients;

    /**
     * Initializes the Wiz application with default storage file paths.
     */
    public Wiz() {
        this(DEFAULT_FILE_PATH, DEFAULT_CLIENTS_FILE_PATH);
    }

    /**
     * Initializes the Wiz application with the specified task storage file path.
     *
     * @param path The path to the file where tasks are saved.
     */
    public Wiz(String path) {
        this(path, path.endsWith(".txt")
                ? path.substring(0, path.length() - 4) + "_clients.txt"
                : path + "_clients.txt");
    }

    /**
     * Initializes the Wiz application with specified task and client storage paths.
     *
     * @param taskPath The path to the file where tasks are saved.
     * @param clientPath The path to the file where clients are saved.
     */
    public Wiz(String taskPath, String clientPath) {
        assert taskPath != null && !taskPath.isBlank() : "Task file path cannot be null or blank";
        assert clientPath != null && !clientPath.isBlank() : "Client file path cannot be null or blank";
        ui = new Ui();
        storage = new Storage(taskPath);
        clientStorage = new ClientStorage(clientPath);
        assert ui != null : "Ui instance should be initialized";
        assert storage != null : "Storage instance should be initialized";
        assert clientStorage != null : "ClientStorage instance should be initialized";

        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showError("Alas! I couldn't load your saved tasks from storage.");
            tasks = new TaskList();
        }

        try {
            clients = new ClientList(clientStorage.load());
        } catch (IOException e) {
            ui.showError("Alas! I couldn't load your saved clients from storage.");
            clients = new ClientList();
        }

        assert tasks != null : "TaskList should not be null after initialization";
        assert clients != null : "ClientList should not be null after initialization";
    }

    /**
     * Runs the main application loop, reading and processing user commands.
     */
    public void run() {
        assert ui != null : "Ui must be initialized before running";
        ui.showWelcome();

        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readCommand();
            String response = getResponse(input);
            ui.showMessage(response);

            if (Parser.getCommandWord(input).equals("bye")) {
                isRunning = false;
            }
        }

        ui.close();
    }

    /**
     * Checks whether a response string represents an error message.
     *
     * @param response The response text to check.
     * @return True if the response is an error message, false otherwise.
     */
    public static boolean isErrorResponse(String response) {
        if (response == null || response.isBlank()) {
            return false;
        }
        return response.startsWith("Alas!")
                || response.startsWith("Halt!")
                || response.startsWith("Oops!")
                || response.startsWith("Please enter a valid command");
    }

    /**
     * Counts the number of times a substring appears in the given text.
     *
     * @param text The string to search within.
     * @param target The substring to count.
     * @return The number of occurrences.
     */
    private static int countOccurrences(String text, String target) {
        if (text == null || target == null || target.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(target, index)) != -1) {
            count++;
            index += target.length();
        }
        return count;
    }

    /**
     * Parses a date-time string strictly using the format yyyy-MM-dd HHmm.
     *
     * @param dateTimeStr The date time string to parse.
     * @return The parsed LocalDateTime.
     * @throws WizException If the string is not a valid date/time format or valid calendar date.
     */
    private static LocalDateTime parseDateTime(String dateTimeStr) throws WizException {
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new WizException("Alas! Invalid date/time '" + dateTimeStr.trim()
                    + "'. Please use a valid calendar date in the format yyyy-MM-dd HHmm (e.g. 2026-10-31 1800).");
        }
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input The user input command.
     * @return The response message as a String.
     */
    public String getResponse(String input) {
        if (input == null || input.isBlank()) {
            return "Please enter a valid command, traveler! Type 'help' to see available spells.";
        }
        try {
            return executeCommand(input);
        } catch (WizException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "Alas! A magical mishap occurred while saving your data: " + e.getMessage();
        }
    }

    /**
     * Dispatches and executes the parsed command.
     *
     * @param input The raw user input command.
     * @return The result message after execution.
     * @throws WizException If command format or validation fails.
     * @throws IOException If saving tasks fails.
     */
    private String executeCommand(String input) throws WizException, IOException {
        assert input != null : "Input command cannot be null";
        String command = Parser.getCommandWord(input);
        String arguments = Parser.getArguments(input);

        switch (command) {
        case "bye":
            return handleBye();
        case "help":
            return handleHelp();
        case "list":
            return handleList();
        case "todo":
            return handleTodo(arguments);
        case "deadline":
            return handleDeadline(arguments);
        case "event":
            return handleEvent(arguments);
        case "mark":
            return handleMark(arguments);
        case "unmark":
            return handleUnmark(arguments);
        case "delete":
            return handleDelete(arguments);
        case "find":
            return handleFind(arguments);
        case "client":
            return handleClient(arguments);
        case "clients":
            return handleClients();
        case "deleteclient":
            return handleDeleteClient(arguments);
        case "findclient":
            return handleFindClient(arguments);
        default:
            throw new WizException("Alas! I do not recognize the spell '" + command
                    + "'. Type 'help' to see the list of known spells.");
        }
    }

    private String handleBye() {
        return "✨ Farewell, traveler! May your tasks be ever completed. See you again soon!";
    }

    private String handleHelp() {
        return "✨ Here are the incantations and spells you can cast:\n"
                + "• todo <desc> — Inscribe a todo task\n"
                + "• deadline <desc> /by <yyyy-MM-dd HHmm> — Inscribe a deadline\n"
                + "• event <desc> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm> — Record an event\n"
                + "• list — View all tasks in your spellbook\n"
                + "• mark <index> — Mark a task as completed\n"
                + "• unmark <index> — Mark a task as pending\n"
                + "• delete <index> — Banish a task from your spellbook\n"
                + "• find <keyword> — Scry for tasks containing keyword\n"
                + "• client <name> /phone <phone> /email <email> [/note <note>] — Register an ally\n"
                + "• clients — View all registered allies\n"
                + "• deleteclient <index> — Remove an ally from registry\n"
                + "• findclient <keyword> — Scry for allies containing keyword\n"
                + "• help — Show this spell guide\n"
                + "• bye — Close the spellbook and exit";
    }

    private String handleList() {
        if (tasks.size() == 0) {
            return "✨ Your spellbook is currently pristine and empty! No tasks inscribed yet.";
        }
        return "✨ Here are the tasks in your spellbook:\n"
                + IntStream.range(0, tasks.size())
                        .mapToObj(i -> (i + 1) + "." + tasks.get(i))
                        .collect(Collectors.joining("\n"));
    }

    private String handleTodo(String arguments) throws WizException, IOException {
        if (arguments.isBlank()) {
            throw new WizException("Alas! A todo incantation requires a description.\nUsage: todo <description>");
        }
        Task task = new ToDo(arguments.trim());
        if (tasks.hasDuplicate(task)) {
            throw new WizException("Halt! A todo task with the same description already exists in your spellbook:\n  " + task);
        }
        tasks.add(task);
        storage.save(tasks.getTasks());
        return "✨ By magical decree, I've inscribed this task into your spellbook:\n  " + task
                + "\nNow you have " + tasks.size() + " task" + (tasks.size() == 1 ? "" : "s") + " in the spellbook.";
    }

    private String handleDeadline(String arguments) throws WizException, IOException {
        if (arguments.isBlank()) {
            throw new WizException("Alas! A deadline requires a description and a due date.\nUsage: deadline <description> /by <yyyy-MM-dd HHmm>");
        }
        if (!arguments.contains("/by")) {
            throw new WizException("Alas! A deadline needs a '/by' tag to specify the due date.\nUsage: deadline <description> /by <yyyy-MM-dd HHmm>");
        }
        if (countOccurrences(arguments, "/by") > 1) {
            throw new WizException("Alas! The '/by' tag should only be specified once.");
        }
        int byIndex = arguments.indexOf("/by");
        String description = arguments.substring(0, byIndex).trim();
        String byString = arguments.substring(byIndex + 3).trim();

        if (description.isEmpty()) {
            throw new WizException("Alas! The deadline description cannot be empty.");
        }
        if (byString.isEmpty()) {
            throw new WizException("Alas! Please provide a date and time after '/by' (format: yyyy-MM-dd HHmm).");
        }

        LocalDateTime by = parseDateTime(byString);
        Task task = new Deadline(description, by);
        if (tasks.hasDuplicate(task)) {
            throw new WizException("Halt! A deadline with the same description and due time already exists in your spellbook:\n  " + task);
        }
        tasks.add(task);
        storage.save(tasks.getTasks());
        return "✨ The hourglass is set! I've inscribed this deadline into your spellbook:\n  " + task
                + "\nNow you have " + tasks.size() + " task" + (tasks.size() == 1 ? "" : "s") + " in the spellbook.";
    }

    private String handleEvent(String arguments) throws WizException, IOException {
        if (arguments.isBlank()) {
            throw new WizException("Alas! An event requires a description, start time, and end time.\nUsage: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
        }
        if (!arguments.contains("/from") || !arguments.contains("/to")) {
            throw new WizException("Alas! An event requires both '/from' and '/to' tags.\nUsage: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
        }
        if (countOccurrences(arguments, "/from") > 1) {
            throw new WizException("Alas! The '/from' tag should only be specified once.");
        }
        if (countOccurrences(arguments, "/to") > 1) {
            throw new WizException("Alas! The '/to' tag should only be specified once.");
        }

        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");

        String description;
        String fromString;
        String toString;

        if (fromIndex < toIndex) {
            description = arguments.substring(0, fromIndex).trim();
            fromString = arguments.substring(fromIndex + 5, toIndex).trim();
            toString = arguments.substring(toIndex + 3).trim();
        } else {
            description = arguments.substring(0, toIndex).trim();
            toString = arguments.substring(toIndex + 3, fromIndex).trim();
            fromString = arguments.substring(fromIndex + 5).trim();
        }

        if (description.isEmpty()) {
            throw new WizException("Alas! The event description cannot be empty.");
        }
        if (fromString.isEmpty()) {
            throw new WizException("Alas! Please provide a start date and time after '/from' (format: yyyy-MM-dd HHmm).");
        }
        if (toString.isEmpty()) {
            throw new WizException("Alas! Please provide an end date and time after '/to' (format: yyyy-MM-dd HHmm).");
        }

        LocalDateTime from = parseDateTime(fromString);
        LocalDateTime to = parseDateTime(toString);

        if (to.isBefore(from) || to.isEqual(from)) {
            throw new WizException("Alas! An event cannot end before or at the same time as it begins.\nStart: "
                    + fromString + ", End: " + toString);
        }

        Task task = new Event(description, from, to);
        if (tasks.hasDuplicate(task)) {
            throw new WizException("Halt! An event with the same description and time period already exists in your spellbook:\n  " + task);
        }
        tasks.add(task);
        storage.save(tasks.getTasks());
        return "✨ Marked on the celestial calendar! I've recorded this event:\n  " + task
                + "\nNow you have " + tasks.size() + " task" + (tasks.size() == 1 ? "" : "s") + " in the spellbook.";
    }

    private int parseIndex(String arguments, int maxCount, String entityName) throws WizException {
        if (arguments.isBlank()) {
            throw new WizException("Alas! Please specify a valid " + entityName + " number.");
        }
        try {
            int index = Integer.parseInt(arguments.trim()) - 1;
            if (index < 0 || index >= maxCount) {
                if (maxCount == 0) {
                    throw new WizException("Alas! Your " + entityName + " list is currently empty.");
                }
                throw new WizException("Alas! " + entityName.substring(0, 1).toUpperCase() + entityName.substring(1)
                        + " number " + (index + 1) + " does not exist. Please choose a number between 1 and " + maxCount + ".");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new WizException("Alas! '" + arguments.trim() + "' is not a valid number. Please provide a positive integer.");
        }
    }

    private String handleMark(String arguments) throws WizException, IOException {
        int index = parseIndex(arguments, tasks.size(), "task");
        tasks.get(index).markAsDone();
        storage.save(tasks.getTasks());
        return "✨ Splendid enchantment! I've marked this task as done:\n  " + tasks.get(index);
    }

    private String handleUnmark(String arguments) throws WizException, IOException {
        int index = parseIndex(arguments, tasks.size(), "task");
        tasks.get(index).markAsNotDone();
        storage.save(tasks.getTasks());
        return "✨ Spell reversed! I've marked this task as not done yet:\n  " + tasks.get(index);
    }

    private String handleDelete(String arguments) throws WizException, IOException {
        int index = parseIndex(arguments, tasks.size(), "task");
        Task removedTask = tasks.delete(index);
        storage.save(tasks.getTasks());
        return "✨ Poof! Vanished into the ether. I've removed this task:\n  " + removedTask
                + "\nNow you have " + tasks.size() + " task" + (tasks.size() == 1 ? "" : "s") + " in the spellbook.";
    }

    private String handleFind(String arguments) throws WizException {
        if (arguments.isBlank()) {
            throw new WizException("Alas! A find spell requires a keyword to search for.");
        }
        ArrayList<Task> matchingTasks = tasks.find(arguments.trim());
        if (matchingTasks.isEmpty()) {
            return "✨ The scrying crystal found no matching tasks for '" + arguments.trim() + "'.";
        }
        return "✨ The scrying crystal revealed these matching tasks in your spellbook:\n"
                + IntStream.range(0, matchingTasks.size())
                        .mapToObj(i -> (i + 1) + "." + matchingTasks.get(i))
                        .collect(Collectors.joining("\n"));
    }

    private String handleClient(String arguments) throws WizException, IOException {
        if (arguments.isBlank()) {
            throw new WizException("Alas! A client requires a name, phone, and email.\nUsage: client <name> /phone <phone> /email <email> [/note <note>]");
        }
        if (!arguments.contains("/phone") || !arguments.contains("/email")) {
            throw new WizException("Alas! Please provide both '/phone' and '/email' tags for the client.\nUsage: client <name> /phone <phone> /email <email> [/note <note>]");
        }
        if (countOccurrences(arguments, "/phone") > 1) {
            throw new WizException("Alas! The '/phone' tag should only be specified once.");
        }
        if (countOccurrences(arguments, "/email") > 1) {
            throw new WizException("Alas! The '/email' tag should only be specified once.");
        }
        if (countOccurrences(arguments, "/note") > 1) {
            throw new WizException("Alas! The '/note' tag should only be specified once.");
        }

        int phoneIndex = arguments.indexOf("/phone");
        int emailIndex = arguments.indexOf("/email");
        int noteIndex = arguments.indexOf("/note");

        String name;
        String phone;
        String email;
        String note = "";

        if (phoneIndex < emailIndex) {
            name = arguments.substring(0, phoneIndex).trim();
            phone = arguments.substring(phoneIndex + 6, emailIndex).trim();
            if (noteIndex != -1 && noteIndex > emailIndex) {
                email = arguments.substring(emailIndex + 6, noteIndex).trim();
                note = arguments.substring(noteIndex + 5).trim();
            } else {
                email = arguments.substring(emailIndex + 6).trim();
            }
        } else {
            name = arguments.substring(0, emailIndex).trim();
            email = arguments.substring(emailIndex + 6, phoneIndex).trim();
            if (noteIndex != -1 && noteIndex > phoneIndex) {
                phone = arguments.substring(phoneIndex + 6, noteIndex).trim();
                note = arguments.substring(noteIndex + 5).trim();
            } else {
                phone = arguments.substring(phoneIndex + 6).trim();
            }
        }

        if (name.isBlank() || phone.isBlank() || email.isBlank()) {
            throw new WizException("Alas! Client name, phone, and email cannot be empty.");
        }

        Client client = note.isEmpty()
                ? new Client(name, phone, email)
                : new Client(name, phone, email, note);

        if (clients.hasDuplicate(client)) {
            throw new WizException("Halt! An ally with the same name, phone, or email already exists in your registry:\n  " + client);
        }

        clients.add(client);
        clientStorage.save(clients.getClients());
        return "✨ A new ally has been inscribed in your guild registry:\n  " + client
                + "\nNow you have " + clients.size() + " ally" + (clients.size() == 1 ? "" : "ies") + " in the registry.";
    }

    private String handleClients() {
        if (clients.size() == 0) {
            return "✨ Your guild registry currently has no allies inscribed.";
        }
        return "✨ Here are the allies in your guild registry:\n"
                + IntStream.range(0, clients.size())
                        .mapToObj(i -> (i + 1) + "." + clients.get(i))
                        .collect(Collectors.joining("\n"));
    }

    private String handleDeleteClient(String arguments) throws WizException, IOException {
        int index = parseIndex(arguments, clients.size(), "client");
        Client removedClient = clients.delete(index);
        clientStorage.save(clients.getClients());
        return "✨ Ally removed from your guild registry:\n  " + removedClient
                + "\nNow you have " + clients.size() + " ally" + (clients.size() == 1 ? "" : "ies") + " in the registry.";
    }

    private String handleFindClient(String arguments) throws WizException {
        if (arguments.isBlank()) {
            throw new WizException("Alas! A findclient command requires a keyword to search for.");
        }
        ArrayList<Client> matchingClients = clients.find(arguments.trim());
        if (matchingClients.isEmpty()) {
            return "✨ No allies matching '" + arguments.trim() + "' were found in the guild registry.";
        }
        return "✨ Here are the matching allies in your guild registry:\n"
                + IntStream.range(0, matchingClients.size())
                        .mapToObj(i -> (i + 1) + "." + matchingClients.get(i))
                        .collect(Collectors.joining("\n"));
    }

    public static void main(String[] args) {
        String path =
                "." + File.separator
                        + "data" + File.separator
                        + "wiz.txt";

        new Wiz(path).run();
    }
}