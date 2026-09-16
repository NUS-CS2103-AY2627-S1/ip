import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Starts StanVard, displays its greeting, and manages an in-memory task list
 * until the user exits.
 */
public class StanVard {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final Storage storage;
    private final List<Task> tasks;

    /**
     * Creates a StanVard instance with tasks loaded from the default storage file.
     */
    public StanVard() {
        storage = new Storage(Storage.DEFAULT_FILE_PATH);
        tasks = loadTasks(storage);
    }

    /**
     * Represents the different commands supported by StanVard.
     */
    private enum CommandType {
        LIST("list"),
        MARK("mark"),
        UNMARK("unmark"),
        DELETE("delete"),
        FIND("find"),
        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event");

        private final String keyword;

        CommandType(String keyword) {
            this.keyword = keyword;
        }

        /**
         * Returns the keyword associated with this command type.
         *
         * @return command keyword
         */
        public String getKeyword() {
            return keyword;
        }

        /**
         * Determines the command type from the user's input.
         *
         * @param command trimmed command entered by the user
         * @return the corresponding command type
         * @throws StanVardException if the command is unknown
         */
        public static CommandType fromCommand(String command) throws StanVardException {
            return Arrays.stream(CommandType.values())
                    .filter(commandType -> command.equals(commandType.keyword)
                            || command.startsWith(commandType.keyword + " "))
                    .findFirst()
                    .orElseThrow(() -> new StanVardException(
                            "OOPS!!! I'm sorry, but I don't know what that means :-("));
        }
    }

    /**
     * Runs the chatbot's greeting, task command loop, and farewell sequence.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = " ____  _____    _    _   _ __     __ _    ____  ____  \n"
                + "/ ___||_   _|  / \\  | \\ | |\\ \\   / / / \\  |  _ \\|  _ \\ \n"
                + "\\___ \\  | |   / _ \\ |  \\| | \\ \\ / / / _ \\ | |_) | | | |\n"
                + " ___) | | |  / ___ \\| |\\  |  \\ V / / ___ \\|  _ <| |_| |\n"
                + "|____/  |_| /_/   \\_\\_| \\_|   \\_/ /_/   \\_\\_| \\_\\____/ \n";

        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hello! I'm StanVard, your friendly task companion.");
        System.out.println("What shall we tackle today?");
        System.out.println(SEPARATOR);

        StanVard stanVard = new StanVard();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();

            if (command.equals("bye")) {
                break;
            }

            System.out.println(SEPARATOR);

            try {
                stanVard.handleCommand(command);
            } catch (StanVardException exception) {
                System.out.println(exception.getMessage());
            }

            System.out.println(SEPARATOR);
        }

        System.out.println("Take care! Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }

    /**
     * Processes one command for a non-console client such as the JavaFX GUI.
     *
     * @param command command entered by the user
     * @return the response produced by StanVard
     */
    public synchronized String processCommand(String command) {
        if (command != null && command.trim().equals("bye")) {
            return "Take care! Bye. Hope to see you again soon!";
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true));
            handleCommand(command);
        } catch (StanVardException exception) {
            System.out.println(exception.getMessage());
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8).trim();
    }

    /**
     * Prints the confirmation shown after adding a task.
     *
     * @param task the added task
     * @param taskCount number of tasks currently stored
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("Nice! I've added this task:");
        System.out.println("  " + task);
        System.out.println("You're up to " + taskCount + " tasks now — nicely organized.");
    }

    /**
     * Processes a command using this instance's task list and storage.
     *
     * @param command command entered by the user
     * @throws StanVardException if the command is invalid
     */
    private void handleCommand(String command) throws StanVardException {
        handleCommand(command, tasks, storage);
    }

    /**
     * Validates and carries out one user command.
     *
     * @param command command entered by the user
     * @param tasks tasks currently stored by the chatbot
     * @param storage task storage used to persist list changes
     * @throws StanVardException if the command is invalid
     */
    private static void handleCommand(String command, List<Task> tasks, Storage storage)
            throws StanVardException {
        if (command == null) {
            throw new StanVardException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }

        String trimmedCommand = command.trim();
        CommandType commandType = CommandType.fromCommand(trimmedCommand);

        switch (commandType) {
            case LIST:
                ensureNoArguments(trimmedCommand, commandType.getKeyword());
                printTaskList(tasks);
                break;

            case MARK:
                int markIndex = parseTaskIndex(
                        trimmedCommand,
                        commandType.getKeyword(),
                        tasks
                );
                tasks.get(markIndex).markAsDone();
                saveTasks(tasks, storage);

                System.out.println("All set! I've marked this task as done:");
                System.out.println("  " + tasks.get(markIndex));
                break;

            case UNMARK:
                int unmarkIndex = parseTaskIndex(
                        trimmedCommand,
                        commandType.getKeyword(),
                        tasks
                );
                tasks.get(unmarkIndex).markAsNotDone();
                saveTasks(tasks, storage);

                System.out.println("No problem! I've marked this task as not done yet:");
                System.out.println("  " + tasks.get(unmarkIndex));
                break;

            case DELETE:
                int deleteIndex = parseTaskIndex(
                        trimmedCommand,
                        commandType.getKeyword(),
                        tasks
                );
                Task deletedTask = tasks.remove(deleteIndex);

                saveTasks(tasks, storage);
                printDeletedTask(deletedTask, tasks);
                break;

            case FIND:
                String keyword = trimmedCommand
                        .substring(commandType.getKeyword().length())
                        .trim();

                if (keyword.isEmpty()) {
                    throw new StanVardException(
                            "OOPS!!! The keyword to find cannot be empty."
                    );
                }

                printMatchingTasks(keyword, tasks);
                break;

            case TODO:
                String todoDescription = trimmedCommand
                        .substring(commandType.getKeyword().length())
                        .trim();

                if (todoDescription.isEmpty()) {
                    throw new StanVardException(
                            "OOPS!!! The description of a todo cannot be empty."
                    );
                }

                addTask(new Todo(todoDescription), tasks, storage);
                break;

            case DEADLINE:
                String deadlineDetails = trimmedCommand
                        .substring(commandType.getKeyword().length())
                        .trim();

                int byIndex = deadlineDetails.indexOf("/by");

                if (byIndex < 0) {
                    throw new StanVardException(
                            "OOPS!!! A deadline must include /by followed by a date."
                    );
                }

                String deadlineDescription = deadlineDetails
                        .substring(0, byIndex)
                        .trim();

                String by = deadlineDetails
                        .substring(byIndex + "/by".length())
                        .trim();

                if (deadlineDescription.isEmpty()) {
                    throw new StanVardException(
                            "OOPS!!! The description of a deadline cannot be empty."
                    );
                }

                if (by.isEmpty()) {
                    throw new StanVardException("OOPS!!! The deadline date cannot be empty.");
                }

                addTask(new Deadline(deadlineDescription, parseDeadlineDate(by)), tasks, storage);
                break;

            case EVENT:
                String eventDetails = trimmedCommand
                        .substring(commandType.getKeyword().length())
                        .trim();

                int fromIndex = eventDetails.indexOf("/from");
                int toIndex = eventDetails.indexOf("/to");

                if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
                    throw new StanVardException(
                            "OOPS!!! An event must include /from and /to times."
                    );
                }

                String eventDescription = eventDetails
                        .substring(0, fromIndex)
                        .trim();

                String from = eventDetails
                        .substring(fromIndex + "/from".length(), toIndex)
                        .trim();

                String to = eventDetails
                        .substring(toIndex + "/to".length())
                        .trim();

                if (eventDescription.isEmpty()) {
                    throw new StanVardException(
                            "OOPS!!! The description of an event cannot be empty."
                    );
                }

                if (from.isEmpty()) {
                    throw new StanVardException(
                            "OOPS!!! The event start time cannot be empty."
                    );
                }

                if (to.isEmpty()) {
                    throw new StanVardException(
                            "OOPS!!! The event end time cannot be empty."
                    );
                }

                addTask(new Event(eventDescription, from, to), tasks, storage);
                break;

            default:
                throw new StanVardException(
                        "OOPS!!! I'm sorry, but I don't know what that means :-("
                );
        }
    }

    /**
     * Converts a one-based task number into an array index after validation.
     *
     * @param command trimmed command entered by the user
     * @param keyword command keyword to remove
     * @param tasks tasks currently stored by the chatbot
     * @return the zero-based task array index
     * @throws StanVardException if the task number is missing, invalid, or out of range
     */
    private static int parseTaskIndex(
            String command,
            String keyword,
            List<Task> tasks) throws StanVardException {

        String numberText = command.substring(keyword.length()).trim();

        if (numberText.isEmpty()) {
            throw new StanVardException(
                    "OOPS!!! The task number to " + keyword + " cannot be empty."
            );
        }

        if (!numberText.matches("[0-9]+")) {
            throw new StanVardException(
                    "OOPS!!! The task number must be a positive integer."
            );
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new StanVardException(
                    "OOPS!!! The task number must be a positive integer."
            );
        }

        if (taskNumber <= 0) {
            throw new StanVardException(
                    "OOPS!!! The task number must be a positive integer."
            );
        }

        if (taskNumber > tasks.size()) {
            throw new StanVardException(
                    "OOPS!!! The task number is out of range."
            );
        }

        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size();
        return taskIndex;
    }

    /**
     * Rejects unexpected arguments for a command that takes none.
     *
     * @param command trimmed command entered by the user
     * @param keyword argument-free command keyword
     * @throws StanVardException if arguments were supplied
     */
    private static void ensureNoArguments(String command, String keyword) throws StanVardException {
        if (!command.equals(keyword)) {
            throw new StanVardException("OOPS!!! The " + keyword + " command does not accept arguments.");
        }
    }

    /**
     * Adds a validated task and displays its confirmation.
     *
     * @param task the task to add
     * @param tasks task storage list
     * @param storage task storage used to persist the added task
     */
    private static void addTask(Task task, List<Task> tasks, Storage storage)
            throws StanVardException {
        tasks.add(task);
        saveTasks(tasks, storage);
        printAddedTask(task, tasks.size());
    }

    /**
     * Converts a deadline date entered in the required ISO format into a {@link LocalDate}.
     *
     * @param dateText date entered after the {@code /by} marker
     * @return parsed deadline date
     * @throws StanVardException if the date is not a valid {@code yyyy-MM-dd} value
     */
    private static LocalDate parseDeadlineDate(String dateText) throws StanVardException {
        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new StanVardException("OOPS!!! The deadline date must be in yyyy-MM-dd format.");
        }
    }

    /**
     * Loads saved tasks, starting with an empty list if the data cannot be read.
     *
     * @param storage task storage to load from
     * @return the loaded task list, or an empty list after a read failure
     */
    private static List<Task> loadTasks(Storage storage) {
        try {
            Storage.LoadResult loadResult = storage.load();
            loadResult.getWarnings().forEach(System.out::println);
            return loadResult.getTasks();
        } catch (IOException exception) {
            System.out.println("OOPS!!! Unable to load saved tasks: " + exception.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves tasks and translates an I/O problem into a user-facing command error.
     *
     * @param tasks tasks to save
     * @param storage task storage to save to
     * @throws StanVardException if the task data cannot be written
     */
    private static void saveTasks(List<Task> tasks, Storage storage) throws StanVardException {
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            throw new StanVardException("OOPS!!! Unable to save tasks: " + exception.getMessage());
        }
    }

    /**
     * Prints the confirmation shown after deleting a task.
     *
     * @param task deleted task
     * @param tasks tasks currently stored after deletion
     */
    private static void printDeletedTask(Task task, List<Task> tasks) {
        System.out.println("Poof! I've removed this task:");
        System.out.println("  " + task);
        System.out.println("You're down to " + tasks.size() + " tasks now.");
    }

    /**
     * Prints all tasks in their stored order.
     *
     * @param tasks tasks currently stored by the chatbot
     */
    private static void printTaskList(List<Task> tasks) {
        System.out.println("Here are your tasks:");

        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + "." + tasks.get(index));
        }
    }

    /**
     * Prints tasks whose descriptions contain the given keyword, ignoring case.
     *
     * @param keyword text to search for in task descriptions
     * @param tasks tasks to search
     */
    private static void printMatchingTasks(String keyword, List<Task> tasks) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        System.out.println("Here are the tasks matching your search:");

        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();

        IntStream.range(0, matchingTasks.size())
                .forEach(index -> System.out.println((index + 1) + "." + matchingTasks.get(index)));

        if (matchingTasks.isEmpty()) {
            System.out.println("No matching tasks found.");
        }
    }
}
