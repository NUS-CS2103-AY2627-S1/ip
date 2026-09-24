package aravii;

/**
 * Executes validated task commands shared by the text and graphical interfaces.
 */
public class CommandHandler {
    /**
     * Creates a stateless handler for task commands.
     */
    public CommandHandler() {
    }

    /**
     * Returns the command reference shown by the help command.
     *
     * @return The available command formats.
     */
    public String helpMessage() {
        return "Available commands:\n"
                + "todo <description>\n"
                + "deadline <description> /by <YYYY-MM-DD>\n"
                + "event <description> /from <YYYY-MM-DD HH:MM> /to <YYYY-MM-DD HH:MM>\n"
                + "list\nmark <number>\nunmark <number>\ndelete <number>\n"
                + "find <keyword>\nsort\nhelp\nbye";
    }

    /**
     * Executes a parsed command against the current task list.
     *
     * @param tasks The task list to query or change.
     * @param command The validated command.
     * @return The user-facing response.
     */
    public String execute(TaskList tasks, Parser.Command command) {
        return switch (command.name()) {
            case "help" -> helpMessage();
            case "list" -> formatList(tasks);
            case "sort" -> {
                tasks.sortByDescription();
                yield formatList(tasks);
        }
        case "find" -> {
            String result = tasks.formatMatching(command.arguments());
            yield result.isEmpty() ? "No matching tasks found." : result.stripTrailing();
        }
        case "todo", "deadline", "event" -> {
            Task task = Parser.parseTask(command);
            tasks.add(task);
            int count = tasks.size();
            yield "Added: " + task + "\nNow you have " + count
                    + (count == 1 ? " task" : " tasks") + " in the list.";
        }
        case "mark" -> {
            Task task = tasks.get(command.arguments());
            task.mark();
            yield "Marked: " + task;
        }
        case "unmark" -> {
            Task task = tasks.get(command.arguments());
            task.unmark();
            yield "Unmarked: " + task;
        }
        case "delete" -> "Deleted: " + tasks.remove(command.arguments());
        case "bye" -> "Bye. Hope to see you again soon!";
        default -> throw new IllegalArgumentException("Unsupported command.");
        };
    }

    private String formatList(TaskList tasks) {
        String result = tasks.formatAll();
        return result.isEmpty() ? "There are no tasks." : result.stripTrailing();
    }
}
