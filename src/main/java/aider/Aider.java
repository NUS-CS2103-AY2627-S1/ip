package aider;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import aider.model.Task;
import aider.model.TaskList;
import aider.storage.Storage;

/** Coordinates the user interface, parser, task list, and storage. */
public class Aider {
    /** Default path used when Aider is launched from the project root. */
    private static final String DATA_FILE_PATH = "./data/aider.txt";

    /** Persists tasks between application runs. */
    private final Storage storage;

    /** Stores the tasks managed during the current run. */
    private final TaskList tasks;

    /** Displays responses and prompts to the user. */
    private final Ui ui;

    /** Converts user commands into task operations. */
    private final Parser parser;

    /** Creates Aider and loads any previously saved tasks. */
    public Aider(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (AiderException exception) {
            ui.showLoadingError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Runs the command loop until the user exits. */
    public void run() {
        ui.showWelcome();
        try (ui) {
            while (ui.hasNextCommand()) {
                String command = ui.readCommand();
                if (command.equals("bye")) {
                    ui.showGoodbye();
                    break;
                }
                if (command.isEmpty()) {
                    ui.showError("Please enter a command.");
                    ui.showSeparator();
                    continue;
                }

                try {
                    System.out.println(processCommand(command));
                } catch (AiderException exception) {
                    ui.showError(exception.getMessage());
                }
                ui.showSeparator();
            }
        }
    }

    /**
     * Processes one command and returns the text that should be shown to a user.
     * This method is shared by the command-line and graphical interfaces.
     *
     * @param command the command entered by the user
     * @return the command response
     * @throws AiderException if the command is invalid or changes cannot be saved
     */
    public String processCommand(String command) throws AiderException {
        command = normalizeCommand(command);

        if (command.equals("bye")) {
            return Personality.goodbyeMessage();
        }

        if (command.equals("list")) {
            if (tasks.isEmpty()) {
                return Personality.listHeading() + "\n  (no tasks yet)";
            }
            return IntStream.range(0, tasks.size())
                    .mapToObj(index -> "\n" + (index + 1) + "." + tasks.get(index))
                    .collect(Collectors.joining("", Personality.listHeading(), ""));
        }

        if (command.equals("schedule") || command.startsWith("schedule ")) {
            String dateOrTime = command.substring("schedule".length()).trim();
            if (dateOrTime.isEmpty()) {
                throw new AiderException("The schedule command needs a date or time.");
            }
            return formatTasks(Personality.scheduleHeading(dateOrTime),
                    tasks.scheduledOn(dateOrTime),
                    "no tasks scheduled for " + dateOrTime);
        }

        if (command.equals("find") || command.startsWith("find ")) {
            return formatTasks(Personality.findHeading(), tasks.find(command), "no matching tasks");
        }

        if (command.equals("on") || command.startsWith("on ")) {
            LocalDate date = parser.parseDate(command);
            return formatTasks(Personality.dateHeading(date), tasks.occurringOn(date),
                    "no deadlines or events");
        }

        boolean changed = false;
        String response;
        if (command.equals("mark") || command.startsWith("mark ")) {
            Task task = tasks.mark(command);
            response = Personality.markedTask(task);
            changed = true;
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            Task task = tasks.unmark(command);
            response = Personality.unmarkedTask(task);
            changed = true;
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            Task task = tasks.remove(tasks.indexOf(command, "delete"));
            response = Personality.deletedTask(task, tasks.size());
            changed = true;
        } else {
            Task task = parser.parseTask(command);
            assert task != null : "Parsing a task command must return a task";
            tasks.add(task);
            response = Personality.addedTask(task, tasks.size());
            changed = true;
        }

        if (changed) {
            try {
                storage.save(tasks.asList());
            } catch (AiderException exception) {
                throw new AiderException("Could not save the data file: " + exception.getMessage());
            }
        }
        return response;
    }

    /** Normalizes harmless whitespace differences before dispatching a command. */
    private static String normalizeCommand(String command) throws AiderException {
        if (command == null || command.isBlank()) {
            throw new AiderException("Please enter a command.");
        }
        return command.trim().replaceAll("\\s+", " ");
    }

    /** Formats a list response shared by search and date queries. */
    private static String formatTasks(String heading, java.util.ArrayList<Task> matchingTasks,
            String emptyMessage) {
        StringBuilder response = new StringBuilder(heading);
        if (matchingTasks.isEmpty()) {
            return response.append("\n  (").append(emptyMessage).append(")").toString();
        }
        for (int i = 0; i < matchingTasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(matchingTasks.get(i));
        }
        return response.toString();
    }

    /** Starts Aider with its project-relative data file. */
    public static void main(String[] args) {
        new Aider(DATA_FILE_PATH).run();
    }
}
