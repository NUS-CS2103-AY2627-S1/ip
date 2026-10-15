package grug.command.parser;

import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Set;

import grug.command.AddTaskCommand;
import grug.task.DeadlineTask;
import grug.task.EventTask;
import grug.task.TaskPriority;
import grug.task.TodoTask;

/**
 * Contains parsing methods to parse the {@link AddTaskCommand}.
 */
final class AddTaskCommandParser {
    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link AddTaskCommand} command instance.
     * @throws GrugCommandParserException If something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static AddTaskCommand parseDeadlineCommand(String[] args)
            throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        Collection<String> requiredFlags = Set.of("/by");
        CommandParser.ParsedArgs parsedArgs = CommandParser.parseArgs(args);
        parsedArgs.validateFlags(Set.of("/by", "/priority"));

        if (parsedArgs.inputs().isEmpty() || !parsedArgs.hasNonEmptyFlagValues(requiredFlags)) {
            String usage = "deadline <details> /by <%s>".formatted(DeadlineTask.DATE_TIME_INPUT_PATTERN);
            throw new GrugCommandParserException.InvalidUsage(usage);
        }

        String details = String.join(" ", parsedArgs.inputs());
        String deadline = String.join(" ", parsedArgs.flagValues().get("/by"));
        TaskPriority priority = CommandParser.parseTaskPriorityFrom(parsedArgs, TaskPriority.DEFAULT);

        try {
            DeadlineTask deadlineTask = new DeadlineTask(details, deadline);
            deadlineTask.setPriority(priority);
            return new AddTaskCommand(deadlineTask);
        } catch (DateTimeParseException e) {
            String reason = "deadline must be a valid date in the format " + DeadlineTask.DATE_TIME_INPUT_PATTERN;
            throw new GrugCommandParserException.InvalidArgument("deadline", reason);
        }
    }

    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link AddTaskCommand} command instance.
     * @throws GrugCommandParserException If something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static AddTaskCommand parseEventCommand(String[] args) throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        Set<String> requiredFlags = Set.of("/from", "/to");
        CommandParser.ParsedArgs parsedArgs = CommandParser.parseArgs(args);
        parsedArgs.validateFlags(Set.of("/from", "/to", "/priority"));

        if (parsedArgs.inputs().isEmpty() || !parsedArgs.hasNonEmptyFlagValues(requiredFlags)) {
            String usage = "event <details> /from <from> /to <to>";
            throw new GrugCommandParserException.InvalidUsage(usage);
        }

        String details = String.join(" ", parsedArgs.inputs());
        String from = String.join(" ", parsedArgs.flagValues().get("/from"));
        String to = String.join(" ", parsedArgs.flagValues().get("/to"));
        TaskPriority priority = CommandParser.parseTaskPriorityFrom(parsedArgs, TaskPriority.DEFAULT);

        try {
            EventTask eventTask = new EventTask(details, from, to);
            eventTask.setPriority(priority);
            return new AddTaskCommand(eventTask);
        } catch (DateTimeParseException e) {
            String reason = "must be a valid date in the format " + EventTask.DATE_TIME_INPUT_PATTERN;
            throw new GrugCommandParserException.InvalidArgument("from / to", reason);
        } catch (IllegalArgumentException e) {
            String reason = "from date/time `%s` must not occur after to date/time `%s`".formatted(from, to);
            throw new GrugCommandParserException.InvalidArgument("from / to", reason);
        }
    }

    /**
     * Parses the argument list `args`.
     *
     * @param args The list of arguments read from the input, including the initial
     *             command to quit as well (e.g. `{ "cmd", "arg1", "arg2", ... }`).
     * @return A new {@link AddTaskCommand} command instance.
     * @throws GrugCommandParserException If something went wrong parsing the args.
     * @throws IllegalArgumentException   If args is empty.
     */
    static AddTaskCommand parseTodoCommand(String[] args) throws GrugCommandParserException {
        CommandParser.requireNonEmptyArgs(args);

        CommandParser.ParsedArgs parsedArgs = CommandParser.parseArgs(args);
        parsedArgs.validateFlags(Set.of("/priority"));
        if (parsedArgs.inputs().isEmpty()) {
            throw new GrugCommandParserException.InvalidUsage(
                    "todo <details> [%s]".formatted(CommandParser.getPriorityFlagUsage()));
        }

        String details = String.join(" ", parsedArgs.inputs());
        TaskPriority priority = CommandParser.parseTaskPriorityFrom(parsedArgs, TaskPriority.DEFAULT);

        TodoTask todoTask = new TodoTask(details);
        todoTask.setPriority(priority);
        return new AddTaskCommand(todoTask);
    }
}
