package magnus.command;

import magnus.exception.CommandSyntaxException;
import magnus.task.TaskList;

/**
 * Displays tasks whose descriptions contain a specified query.
 */
public class FindCommand implements Command {
    private static final String USAGE_MESSAGE = "\tUsage: find <query>";

    private final TaskList tasks;

    /**
     * Creates a command that searches the specified task list.
     *
     * @param tasks The task list to search.
     */
    public FindCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Formats tasks whose descriptions contain the supplied query, ignoring case.
     *
     * @param args The command arguments, containing exactly one query at index 0.
     * @return A message containing tasks whose descriptions match the query.
     * @throws CommandSyntaxException If the query is missing, blank, or split into multiple fields.
     */
    @Override
    public String execute(String... args) throws CommandSyntaxException {
        if (args.length == 0 || args[0].isBlank()) {
            throw new CommandSyntaxException("\tThat move has no target - please give me a search query.\n"
                    + USAGE_MESSAGE);
        }

        if (args.length > 1) {
            throw new CommandSyntaxException("\tToo many targets in that move - the find command accepts one "
                    + "query.\n"
                    + USAGE_MESSAGE);
        }
        if (TaskArgumentsParser.containsFieldDelimiter(args[0])) {
            throw new CommandSyntaxException("\tThat move uses a reserved clock field - the find command "
                    + "accepts only a query.\n" + USAGE_MESSAGE);
        }

        String query = args[0].strip();
        TaskList matchingTasks = this.tasks.filterTasksByDescription(query);
        if (matchingTasks.getTaskCount() == 0) {
            return String.format("\tI searched the board, but no tasks match \"%s\".", query);
        }
        return String.format("\tI searched the board - here are the tasks matching \"%s\":%n%n%s",
                query, matchingTasks.formatTasks());
    }
}
