package allmind;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import allmind.model.Event;
import allmind.model.Task;
import allmind.model.TaskList;
import allmind.parser.InvalidCommandException;
import allmind.parser.MissingFieldException;
import allmind.parser.Parser;
import allmind.storage.Storage;
import allmind.ui.Ui;

/**
 * Starts the ALLMIND command-line chatbot and processes user commands.
 */
public class AllMind {

    private static final DateTimeFormatter CONFLICT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final Parser parser = new Parser();
    private final Storage storage = new Storage("tasks.txt");
    private TaskList tasks;
    private Event pendingEvent;

    /** Creates an AllMind instance and loads its saved tasks. */
    public AllMind() {
        try {
            tasks = new TaskList(storage.load(parser));
        } catch (IOException | IllegalArgumentException | InvalidCommandException e) {
            tasks = new TaskList();
        }
    }

    /**
     * Greets the user, stores entered tasks, and stops when the user enters {@code bye}.
     *
     * @param args command-line arguments; not used by this application
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("tasks.txt");
        Parser parser = new Parser();
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load(parser));
        } catch (IOException | IllegalArgumentException | InvalidCommandException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

        ui.showWelcome();

        String command;
        Event pendingEvent = null;
        while ((command = ui.readCommand()) != null) {

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            ui.showLine();
            
            try {
                if (pendingEvent != null) {
                    if (command.trim().equalsIgnoreCase("yes")) {
                        tasks.add(pendingEvent);
                        saveTasks(storage, tasks, parser, ui);
                        System.out.println("Understood. Your task has been added:");
                        System.out.println("  " + pendingEvent);
                        System.out.println("There are now " + tasks.size() + " tasks in the list.");
                        pendingEvent = null;
                    } else if (command.trim().equalsIgnoreCase("no")) {
                        System.out.println("The event was not added.");
                        pendingEvent = null;
                    } else {
                        System.out.println("Please enter yes to add the event or no to cancel.");
                    }
                    ui.showLine();
                    continue;
                }
                if (command.equals("list")) {
                    tasks.printTasks();
                } else if (command.equals("find") || command.startsWith("find ")) {
                    tasks.findTasks(command);
                } else if (command.startsWith("mark ")) {
                    tasks.markTask(command);
                    saveTasks(storage, tasks, parser, ui);
                } else if (command.startsWith("unmark ")) {
                    tasks.unmarkTask(command);
                    saveTasks(storage, tasks, parser, ui);
                } else if (command.startsWith("delete ")) {
                    tasks.deleteTask(command);
                    saveTasks(storage, tasks, parser, ui);
                } else {
                    Task task = parser.parseTask(command);
                    if (task instanceof Event event) {
                        List<TaskList.EventConflict> conflicts = tasks.findEventConflicts(event);
                        if (!conflicts.isEmpty()) {
                            System.out.println(formatConflictWarning(conflicts));
                            pendingEvent = event;
                            ui.showLine();
                            continue;
                        }
                    }
                    tasks.add(task);
                    saveTasks(storage, tasks, parser, ui);
                    System.out.println("Understood. Your task has been added:");
                    System.out.println("  " + task);
                    System.out.println("There are now " + tasks.size() + " tasks in the list.");
                }
            } catch (InvalidCommandException | MissingFieldException e) {
                System.out.println(e.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        try {
            if (input.equals("bye")) {
                pendingEvent = null;
                return "Goodbye!";
            }
            if (pendingEvent != null) {
                if (input.trim().equalsIgnoreCase("yes")) {
                    tasks.add(pendingEvent);
                    saveTasks(storage, tasks, parser, new Ui());
                    Task addedEvent = pendingEvent;
                    pendingEvent = null;
                    return "Understood. Your task has been added:\n  " + addedEvent;
                }
                if (input.trim().equalsIgnoreCase("no")) {
                    pendingEvent = null;
                    return "Understood. Your input was discarded.";
                }
                return "Apologies, a 'yes' or 'no' answer is required.";
            }
            if (input.equals("list")) {
                if (tasks.size() == 0) {
                    return "There are no tasks in your list.";
                }
                StringBuilder response = new StringBuilder("Affirmative. You have scheduled the following tasks:\n");
                for (int i = 0; i < tasks.asList().size(); i++) {
                    response.append(i + 1).append(". ").append(tasks.asList().get(i)).append("\n");
                }
                return response.toString().trim();
            }
            if (input.equals("find") || input.startsWith("find ")) {
                String keyword = input.substring("find".length()).trim();
                if (keyword.isEmpty()) {
                    throw new InvalidCommandException("You have not provided a keyword.");
                }
                StringBuilder response = new StringBuilder("Searching...\n I have found these related tasks:\n");
                for (int i = 0; i < tasks.asList().size(); i++) {
                    Task task = tasks.asList().get(i);
                    if (task.getDescription().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                        response.append(i + 1).append(". ").append(task).append("\n");
                    }
                }
                return response.toString().trim();
            }
            if (input.startsWith("mark ")) {
                tasks.markTask(input);
                saveTasks(storage, tasks, parser, new Ui());
                return "Affirmative. Task marked as done.";
            }
            if (input.startsWith("unmark ")) {
                tasks.unmarkTask(input);
                saveTasks(storage, tasks, parser, new Ui());
                return "Affirmative. Task marked as not done.";
            }
            if (input.startsWith("delete ")) {
                tasks.deleteTask(input);
                saveTasks(storage, tasks, parser, new Ui());
                return "Affirmative. Task deleted.";
            }

            Task task = parser.parseTask(input);
            if (task instanceof Event event) {
                List<TaskList.EventConflict> conflicts = tasks.findEventConflicts(event);
                if (!conflicts.isEmpty()) {
                    pendingEvent = event;
                    return formatConflictWarning(conflicts);
                }
            }
            tasks.add(task);
            saveTasks(storage, tasks, parser, new Ui());
            return "Understood. Your task has been added:\n  " + task;
        } catch (InvalidCommandException | MissingFieldException e) {
            return e.getMessage();
        }
    }

    /** Formats the warning and confirmation prompt for event conflicts. */
    private static String formatConflictWarning(List<TaskList.EventConflict> conflicts) {
        StringBuilder response = new StringBuilder("This event clashes with:");
        for (TaskList.EventConflict conflict : conflicts) {
            response.append("\n")
                    .append(conflict.taskNumber()).append(". ")
                    .append(conflict.description()).append(" (")
                    .append(conflict.overlapStart().format(CONFLICT_DATE_FORMAT)).append(" to ")
                    .append(conflict.overlapEnd().format(CONFLICT_DATE_FORMAT)).append(")");
        }
        return response.append("\nAre you sure you want to add this event? (yes/no)").toString();
    }

    /** Saves tasks and reports file errors through the UI. */
    private static void saveTasks(Storage storage, TaskList tasks, Parser parser, Ui ui) {
        try {
            storage.save(tasks.asList(), parser);
        } catch (IOException | IllegalArgumentException e) {
            ui.showSavingError();
        }
    }

}
