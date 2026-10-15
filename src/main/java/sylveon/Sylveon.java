package sylveon;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/** Runs the Sylveon chatbot application. */
public class Sylveon {
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";

    private final Storage storage;
    private final TaskList tasks;
    private ArrayList<Task> displayedTasks;

    /** Creates Sylveon and loads its saved tasks. */
    public Sylveon() {
        this("data/sylveon.txt");
    }

    /** Creates Sylveon using the specified storage file. */
    Sylveon(String filePath) {
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
        displayedTasks = new ArrayList<>(tasks.getTasks());
    }

    /**
     * Generates a response for a user's GUI message.
     *
     * @param input the user's message
     * @return Sylveon's response
     */
    public String getResponse(String input) {
        try {
            return processCommand(input);
        } catch (SylveonException e) {
            return e.getMessage();
        }
    }

    private String processCommand(String input) throws SylveonException {
        Parser parser = new Parser();
        String commandWord = parser.getCommandWord(input);
        String arguments = parser.getArguments(input);

        switch (commandWord) {
        case "":
            throw new SylveonException("Please enter a command. Try typing \"list\" or \"help\" 🙂");
        case "help":
            return "Commands: todo <description>, deadline <description> /by <yyyy-mm-dd>, "
                    + "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>, list, find <keyword>, "
                    + "mark <number>, unmark <number>, delete <number>, sort, and bye.";
        case "list":
            return formatTaskList();
        case "sort":
            return sortTasks(arguments);
        case "mark":
        case "unmark":
            return changeTaskStatus(commandWord, arguments);
        case "delete":
            return deleteTask(arguments);
        case "find":
            return findTasks(arguments);
        case "todo":
            return addTask(new Todo(arguments), input);
        case "deadline":
            return addDeadline(arguments, input);
        case "event":
            return addEvent(arguments, input);
        case "bye":
            return "Bye for now! Keep going—you've got this 💪";
        default:
            throw new SylveonException("Oops! I couldn't understand that command 😅 "
                    + "Try checking the format.");
        }
    }

    private String formatTaskList() {
        displayedTasks = new ArrayList<>(tasks.getTasks());
        if (tasks.isEmpty()) {
            return "Your task list is clear! Add something when you're ready 😊";
        }
        StringBuilder result = new StringBuilder("Here are your tasks:\n");
        for (int i = 0; i < tasks.size(); i++) {
            result.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return result.toString().trim();
    }

    private String changeTaskStatus(String command, String arguments) throws SylveonException {
        int taskNumber = parseTaskNumber(arguments, command, displayedTasks.size());
        Task task = displayedTasks.get(taskNumber - 1);
        assert task != null : "A valid task number must return a task";
        if (command.equals("mark")) {
            task.markAsDone();
            storage.save(tasks.getTasks());
            return "Great job! You finished a task 🎉\n" + task;
        }
        task.markAsNotDone();
        storage.save(tasks.getTasks());
        return "No problem! I've marked this task as not done yet 🙂\n" + task;
    }

    private String deleteTask(String arguments) throws SylveonException {
        int taskNumber = parseTaskNumber(arguments, "delete", displayedTasks.size());
        Task deletedTask = displayedTasks.get(taskNumber - 1);
        tasks.delete(deletedTask);
        displayedTasks.remove(deletedTask);
        assert deletedTask != null : "Deleting a valid task must return a task";
        storage.save(tasks.getTasks());
        return "Done! That task has been removed 🗑️:\n" + deletedTask
                + "\nNow you have " + tasks.size() + " tasks left!";
    }

    private String findTasks(String arguments) throws SylveonException {
        if (arguments.isEmpty()) {
            throw new SylveonException("Oops! Please provide a keyword after find 😅");
        }
        ArrayList<Task> matchingTasks = tasks.find(arguments);
        if (matchingTasks.isEmpty()) {
            return "There are no matching tasks.";
        }
        StringBuilder result = new StringBuilder("Here are the matching tasks:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            result.append(i + 1).append(". ").append(matchingTasks.get(i)).append("\n");
        }
        return result.toString().trim();
    }

    private String addTask(Task task, String input) throws SylveonException {
        if (task.getDescription().isEmpty()) {
            throw new SylveonException("Error! Remember to add a description :)");
        }
        tasks.add(task);
        displayedTasks = new ArrayList<>(tasks.getTasks());
        assert tasks.get(tasks.size() - 1) == task : "Added task must be stored at the end of the list";
        storage.save(tasks.getTasks());
        return "All set! I added that task to your list ✨\n" + input;
    }

    private String sortTasks(String arguments) throws SylveonException {
        if (arguments.isEmpty()) {
            displayedTasks = tasks.sortAlphabetically();
            storage.save(tasks.getTasks());
            return formatSortedTasks("Sorted tasks alphabetically:");
        }
        if (arguments.contains(" ")) {
            throw new SylveonException("Error! Sort commands accept exactly one task type.");
        }
        if (!arguments.equals("deadline") && !arguments.equals("event") && !arguments.equals("todo")) {
            throw new SylveonException("Error! Invalid sort type. Use deadline, event, or todo.");
        }

        displayedTasks = tasks.sortByType(arguments);
        storage.save(tasks.getTasks());
        String order = arguments.equals("todo") ? "alphabetically" : "by date (earliest first)";
        return formatSortedTasks("Sorted " + arguments + "s " + order + ":");
    }

    private String formatSortedTasks(String header) {
        StringBuilder result = new StringBuilder(header);
        if (displayedTasks.isEmpty()) {
            return result.append("\nThere are no matching tasks.").toString();
        }
        for (int i = 0; i < displayedTasks.size(); i++) {
            result.append("\n").append(i + 1).append(". ").append(displayedTasks.get(i));
        }
        return result.toString();
    }

    private String addDeadline(String arguments, String input) throws SylveonException {
        int index = arguments.indexOf(DEADLINE_SEPARATOR);
        if (index == -1) {
            throw new SylveonException("Error! A deadline has to be written like this: "
                    + "deadline <description> /by <date>");
        }
        String description = arguments.substring(0, index).trim();
        String dateText = arguments.substring(index + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty() || dateText.isEmpty()) {
            throw new SylveonException("Error! A deadline needs a description and date :)");
        }
        try {
            LocalDate date = LocalDate.parse(dateText);
            if (date.isBefore(LocalDate.now())) {
                throw new SylveonException("Error! A deadline cannot be in the past :)");
            }
            return addTask(new Deadline(description, date), input);
        } catch (DateTimeParseException e) {
            throw new SylveonException("Error! Please use the date format yyyy-mm-dd :)");
        }
    }

    private String addEvent(String arguments, String input) throws SylveonException {
        int fromIndex = arguments.indexOf(EVENT_START_SEPARATOR);
        int toIndex = arguments.indexOf(EVENT_END_SEPARATOR);
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new SylveonException("Error! An event must be written like this: "
                    + "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String fromText = arguments.substring(fromIndex + EVENT_START_SEPARATOR.length(), toIndex).trim();
        String toText = arguments.substring(toIndex + EVENT_END_SEPARATOR.length()).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new SylveonException("Error! An event needs a description and dates in yyyy-mm-dd format :)");
        }
        try {
            LocalDate from = LocalDate.parse(fromText);
            LocalDate to = LocalDate.parse(toText);
            if (to.isBefore(from)) {
                throw new SylveonException("Oops! An event cannot end before it starts 😅");
            }
            if (from.isBefore(LocalDate.now())) {
                throw new SylveonException("Error! An event cannot start in the past :)");
            }
            return addTask(new Event(description, from, to), input);
        } catch (DateTimeParseException e) {
            throw new SylveonException("Error! Event dates must use the format yyyy-mm-dd :)");
        }
    }

    /** Converts a user-provided task number into a zero-based list position. */
    private static int parseTaskNumber(String arguments, String command, int taskCount)
            throws SylveonException {
        if (arguments.isEmpty()) {
            throw new SylveonException("Error! Please provide a task number after " + command + " :)");
        }
        try {
            int taskNumber = Integer.parseInt(arguments);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new SylveonException("Uh oh.. That task number does not exist :(");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new SylveonException("Error! The task number has to be a whole number!");
        }
    }

    /** Starts the chatbot and processes commands until the user exits. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        runCommandLoop(ui, new Sylveon());
        ui.showBye();
    }

    /** Processes console commands until the user enters the exit command. */
    private static void runCommandLoop(Ui ui, Sylveon sylveon) {
        Parser parser = new Parser();
        while (true) {
            String command = ui.readCommand();
            String commandWord = parser.getCommandWord(command);
            if (commandWord.equals("bye")) {
                break;
            }
            System.out.println(sylveon.getResponse(command));
        }
    }
}
