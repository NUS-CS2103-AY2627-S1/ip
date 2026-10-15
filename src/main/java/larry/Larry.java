package larry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the Larry task management application.
 */
public class Larry {
    private static final String SAVE_FILE_PATH = "data/larry.txt";
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Creates a Larry task manager and loads previously saved tasks.
     */
    public Larry() {
        loadTasks();
    }

    private static class LarryException extends Exception {
        public LarryException(String message) {
            super(message);
        }
    }

    private static class EmptyDescriptionException extends LarryException {
        public EmptyDescriptionException(String taskType) {
            super(taskType + " needs a description.");
        }
    }

    private static class UnknownCommandException extends LarryException {
        public UnknownCommandException() {
            super("I don't recognise that command.");
        }
    }

    private static class InvalidTaskNumberException extends LarryException {
        public InvalidTaskNumberException() {
            super("That task number doesn't exist.");
        }
    }

    private static class InvalidFormatException extends LarryException {
        public InvalidFormatException(String message) {
            super(message);
        }
    }

    /**
     * Returns Larry's response to a user command.
     *
     * @param input User command.
     * @return Larry's response.
     */
    public String getResponse(String input) {
        if (input.equals("bye")) {
            return "Bye. Hope to see you again soon!";
        }

        try {
            return handleCommand(input);
        } catch (LarryException e) {
            return "OOPS!!! " + e.getMessage();
        }
    }

    private String handleCommand(String input) throws LarryException {
        if (input.equals("help")) {
            return getHelpMessage();
        } else if (input.equals("list")) {
            return getTaskList();
        } else if (input.equals("todo")) {
            throw new EmptyDescriptionException("todo");
        } else if (input.startsWith("todo ")) {
            return addTodo(input);
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            return addDeadline(input);
        } else if (input.equals("event") || input.startsWith("event ")) {
            return addEvent(input);
        } else if (input.equals("mark")) {
            throw new InvalidTaskNumberException();
        } else if (input.equals("unmark")) {
            throw new InvalidTaskNumberException();
        } else if (input.equals("delete")) {
            throw new InvalidTaskNumberException();
        } else if (input.equals("find")) {
            throw new InvalidFormatException("Please specify a keyword to find.");
        } else if (input.startsWith("mark ")) {
            return markTask(input);
        } else if (input.startsWith("unmark ")) {
            return unmarkTask(input);
        } else if (input.startsWith("delete ")) {
            return deleteTask(input);
        } else if (input.startsWith("find ")) {
            return findTasks(input);
        } else {
            throw new UnknownCommandException();
        }
    }

    private String getHelpMessage() {
        return "Here are the commands you can use:"
                + System.lineSeparator() + "list"
                + System.lineSeparator() + "todo <description>"
                + System.lineSeparator() + "deadline <description> /by <yyyy-mm-dd>"
                + System.lineSeparator() + "event <description> /from <start> /to <end>"
                + System.lineSeparator() + "mark <task number>"
                + System.lineSeparator() + "unmark <task number>"
                + System.lineSeparator() + "delete <task number>"
                + System.lineSeparator() + "find <keyword>"
                + System.lineSeparator() + "bye";
    }

    private String getTaskList() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append(".")
                    .append(tasks.get(i));
        }

        return response.toString();
    }

    private String addTodo(String input) throws EmptyDescriptionException {
        String description = input.substring(5).trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo");
        }

        Task task = new Todo(description);
        return addTask(task);
    }

    private String addDeadline(String input) throws LarryException {
        int byIndex = input.indexOf(" /by ");

        if (byIndex == -1) {
            throw new InvalidFormatException(
                    "Use: deadline <description> /by <time>");
        }

        String description = input.substring(9, byIndex).trim();
        String by = input.substring(byIndex + 5).trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }

        if (by.isEmpty()) {
            throw new InvalidFormatException(
                    "Please specify when the deadline is due after /by.");
        }

        try {
            Task task = new Deadline(description, by);
            return addTask(task);
        } catch (DateTimeParseException e) {
            throw new InvalidFormatException(
                    "Please use a valid date in yyyy-mm-dd format.");
        }
    }

    private String addEvent(String input) throws LarryException {
        int fromIndex = input.indexOf(" /from ");
        int toIndex = input.indexOf(" /to ");

        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new InvalidFormatException(
                    "Use: event <description> /from <start> /to <end>");
        }

        String description = input.substring(6, fromIndex).trim();
        String from = input.substring(fromIndex + 7, toIndex).trim();
        String to = input.substring(toIndex + 5).trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }

        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidFormatException(
                    "Please specify both the event start and end times.");
        }

        Task task = new Event(description, from, to);
        return addTask(task);
    }

    private String markTask(String input) throws InvalidTaskNumberException {
        int index = parseTaskIndex(input.substring(5), tasks.size());
        assert index >= 0 && index < tasks.size();
        tasks.get(index).markAsDone();
        saveTasks();

        return "Nice! I've marked this task as done:"
                + System.lineSeparator() + tasks.get(index);
    }

    private String unmarkTask(String input) throws InvalidTaskNumberException {
        int index = parseTaskIndex(input.substring(7), tasks.size());
        assert index >= 0 && index < tasks.size();
        tasks.get(index).markAsNotDone();
        saveTasks();

        return "OK, I've marked this task as not done yet:"
                + System.lineSeparator() + tasks.get(index);
    }

    private String deleteTask(String input) throws InvalidTaskNumberException {
        int index = parseTaskIndex(input.substring(7), tasks.size());
        assert index >= 0 && index < tasks.size();
        Task removedTask = tasks.remove(index);
        saveTasks();

        return "Noted. I've removed this task:"
                + System.lineSeparator() + "  " + removedTask
                + System.lineSeparator() + "Now you have " + tasks.size()
                + " tasks in the list.";
    }

    /**
     * Saves all current tasks to disk.
     */
    private void saveTasks() {
        try {
            File file = new File(SAVE_FILE_PATH);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(serializeTask(task) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not save tasks.");
        }
    }

    /**
     * Loads previously saved tasks from disk.
     * Invalid records are skipped instead of preventing Larry from starting.
     */
    private void loadTasks() {
        Path path = Path.of(SAVE_FILE_PATH);

        if (!Files.exists(path)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                Task task = deserializeTask(line);

                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load tasks.");
        }
    }

    /**
     * Converts a task into a record suitable for storage.
     *
     * @param task Task to serialize.
     * @return Serialized task record.
     */
    private String serializeTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String description = escape(task.getDescription());

        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + description
                    + " | " + deadline.getBy();
        }

        if (task instanceof Event event) {
            return "E | " + status + " | " + description
                    + " | " + escape(event.getFrom())
                    + " | " + escape(event.getTo());
        }

        return "T | " + status + " | " + description;
    }

    /**
     * Reconstructs a task from a stored record.
     *
     * @param line Stored task record.
     * @return Reconstructed task, or null if the record is invalid.
     */
    private Task deserializeTask(String line) {
        try {
            String[] parts = line.split(" \\| ", -1);

            if (parts.length < 3) {
                return null;
            }

            String type = parts[0];
            boolean isDone = parts[1].equals("1");
            String description = unescape(parts[2]);
            Task task;

            switch (type) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    if (parts.length != 4) {
                        return null;
                    }
                    task = new Deadline(description, parts[3]);
                    break;
                case "E":
                    if (parts.length != 5) {
                        return null;
                    }
                    task = new Event(
                            description,
                            unescape(parts[3]),
                            unescape(parts[4]));
                    break;
                default:
                    return null;
            }

            if (isDone) {
                task.markAsDone();
            }

            return task;
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Escapes characters that could interfere with the storage delimiter.
     *
     * @param text Text to escape.
     * @return Escaped text.
     */
    private String escape(String text) {
        return text.replace("%", "%25").replace("|", "%7C");
    }

    /**
     * Restores text escaped for storage.
     *
     * @param text Escaped text.
     * @return Original text.
     */
    private String unescape(String text) {
        return text.replace("%7C", "|").replace("%25", "%");
    }

    private static int parseTaskIndex(String numberText, int taskCount)
            throws InvalidTaskNumberException {
        try {
            int taskNumber = Integer.parseInt(numberText.trim());
            int index = taskNumber - 1;

            if (index < 0 || index >= taskCount) {
                throw new InvalidTaskNumberException();
            }

            return index;
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException();
        }
    }

    private String addTask(Task task) {
        tasks.add(task);
        saveTasks();
        return getTaskAddedResponse(task);
    }

    private String getTaskAddedResponse(Task task) {
        return "Got it. I've added this task:"
                + System.lineSeparator() + "  " + task
                + System.lineSeparator() + "Now you have " + tasks.size()
                + " tasks in the list.";
    }

    private String findTasks(String input) throws InvalidFormatException {
        String keyword = input.substring(5).trim();

        if (keyword.isEmpty()) {
            throw new InvalidFormatException("Please specify a keyword to find.");
        }
        StringBuilder response = new StringBuilder(
                "Here are the matching tasks in your list:");

        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();

        for (int i = 0; i < matchingTasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append(".")
                    .append(matchingTasks.get(i));
        }

        return response.toString();
    }
}
