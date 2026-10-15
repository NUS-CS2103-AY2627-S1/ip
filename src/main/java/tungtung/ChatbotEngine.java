package tungtung;

import java.io.IOException;
import java.util.ArrayList;

/** Executes Tung Tung commands for a user interface. */
public class ChatbotEngine {
    private final Parser parser = new Parser();
    private final Storage storage;
    private TaskList tasks;
    private String startupError;

    /** Creates an engine backed by the application's task file. */
    public ChatbotEngine() {
        this(new Storage("data/tungtung.txt"));
    }

    /** Creates an engine with supplied storage, allowing isolated persistence tests. */
    ChatbotEngine(Storage storage) {
        this.storage = storage;
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | SecurityException exception) {
            tasks = new TaskList();
            startupError = "OOPS!!! Unable to load saved tasks. Your saved file has not been changed. "
                    + "Back up and repair data/tungtung.txt, then restart. Details: " + exception.getMessage();
        }
    }

    /**
     * Executes a command and returns text suitable for displaying in a chat bubble.
     *
     * @param input command entered by the user.
     * @return response produced by Tung Tung.
     */
    public String execute(String input) {
        if (startupError != null) {
            return startupError;
        }
        if (input == null || input.contains("\n") || input.contains("\r")) {
            return "OOPS!!! Enter one command on a single line.";
        }
        ArrayList<Task> originalTasks = tasks.toArrayList();
        ArrayList<Boolean> originalStatuses = new ArrayList<>();
        for (Task task : originalTasks) {
            originalStatuses.add(task.isDone);
        }
        try {
            String command = input.trim();
            if (command.equals("list")) {
                return formatTasks(tasks.toArrayList(), "Here are your tasks:");
            }
            if (command.equals("find") || command.startsWith("find ")) {
                String keyword = command.substring(4).trim();
                if (keyword.isEmpty()) {
                    throw new TungTungException("Please provide a keyword to find.");
                }
                return formatTasks(tasks.find(keyword), "Here are the matching tasks:");
            }
            if (command.startsWith("sort ") || command.equals("sort")) {
                return sortTasks(command);
            }
            if (command.equals("mark") || command.equals("unmark")
                    || command.startsWith("mark ") || command.startsWith("unmark ")) {
                return updateStatus(command);
            }
            if (command.equals("delete") || command.startsWith("delete ")) {
                return deleteTask(command);
            }

            Task task = parser.parseTask(command);
            tasks.add(task);
            save();
            return "Got it! I've added this task:\n" + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        } catch (IOException | SecurityException exception) {
            // The list copy shares task objects, so completion flags also need restoring.
            for (int index = 0; index < originalTasks.size(); index++) {
                if (originalStatuses.get(index)) {
                    originalTasks.get(index).setDone();
                } else {
                    originalTasks.get(index).setUndone();
                }
            }
            tasks = new TaskList(originalTasks);
            return "OOPS!!! I could not save your tasks to disk. No changes were made. " + exception.getMessage();
        } catch (TungTungException exception) {
            return exception.getMessage().startsWith("OOPS!!!")
                    ? exception.getMessage() : "OOPS!!! " + exception.getMessage();
        }
    }

    /** Returns the startup failure message, or null when saved tasks loaded successfully. */
    public String getStartupError() {
        return startupError;
    }

    private String formatTasks(ArrayList<Task> selectedTasks, String heading) {
        if (selectedTasks.isEmpty()) {
            return heading + "\nThere are no tasks to show.";
        }
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < selectedTasks.size(); index++) {
            Task task = selectedTasks.get(index);
            response.append("\n").append(tasks.indexOf(task) + 1).append(". ").append(task);
        }
        return response.toString();
    }

    private String sortTasks(String command) throws TungTungException, IOException {
        if (command.equals("sort") || command.equals("sort by")) {
            throw new TungTungException("Please specify a sort order. Use: sort by deadline.");
        }
        if (!command.equals("sort by deadline")) {
            if (command.startsWith("sort by deadline ")) {
                throw new TungTungException("Descending sort is not supported. Use: sort by deadline.");
            }
            if (command.startsWith("sort by ")) {
                throw new TungTungException("Unsupported sort order. Use: sort by deadline.");
            }
            throw new TungTungException("Invalid sort syntax. Use: sort by deadline.");
        }
        if (tasks.size() == 0) {
            throw new TungTungException("Nothing to sort here!");
        }
        tasks.sortByDate();
        save();
        return formatTasks(tasks.toArrayList(), "Here are your tasks sorted by deadline:");
    }

    private String updateStatus(String command) throws TungTungException, IOException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new TungTungException("Please provide a valid task number.");
        }
        int index = taskIndex(parts[1]);
        Task task = tasks.get(index);
        if (parts[0].equals("mark")) {
            task.setDone();
        } else {
            task.setUndone();
        }
        save();
        return "Updated task:\n" + task;
    }

    private String deleteTask(String command) throws TungTungException, IOException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new TungTungException("Please provide a valid task number.");
        }
        Task removedTask = tasks.remove(taskIndex(parts[1]));
        save();
        return "Removed task:\n" + removedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private int taskIndex(String number) throws TungTungException {
        try {
            int taskNumber = Integer.parseInt(number);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new TungTungException("Please provide a valid task number.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new TungTungException("Please provide a valid task number.");
        }
    }

    private void save() throws IOException {
        storage.save(tasks.toArrayList());
    }
}
