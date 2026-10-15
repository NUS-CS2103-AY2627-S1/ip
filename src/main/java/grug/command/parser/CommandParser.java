package grug.command.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import grug.command.EmptyCommand;
import grug.command.GrugCommand;
import grug.task.TaskPriority;

/**
 * Parser to parse the list of args given to Grug into the appropriate
 * {@link GrugCommand}.
 */
public class CommandParser {
    /**
     * Holds the result of the parsing.
     *
     * @param command         The main command.
     * @param inputs          The non-flag inputs passed to the command.
     * @param flagValues      A map that maps each normalized flag to its values.
     * @param flagOccurrences A map that records how many times each flag occurs.
     */
    record ParsedArgs(String command, List<String> inputs, Map<String, List<String>> flagValues,
            Map<String, Integer> flagOccurrences) {
        /**
         * Checks that the {@code flagValues} contain all the flags specified by
         * {@code requiredFlags}, and their values are non-empty (i.e. values are
         * provided for that flag).
         *
         * @param requiredFlags The flags that are required.
         * @return {@code true} if {@code flagValues} contains all required flags whose
         *         values are non-empty, {@code false} otherwise.
         */
        public boolean hasNonEmptyFlagValues(Collection<String> requiredFlags) {
            boolean areAllValuesNonEmpty = requiredFlags
                    .stream()
                    .map(flagValues::get)
                    .allMatch(values -> values != null && !values.isEmpty());
            return areAllValuesNonEmpty;
        }

        /**
         * Checks that only the allowed flags occur and that each flag occurs at most
         * once.
         *
         * @param allowedFlags The flags accepted by the command.
         * @throws GrugCommandParserException If an unknown or repeated flag occurs.
         */
        public void validateFlags(Set<String> allowedFlags) throws GrugCommandParserException {
            for (Map.Entry<String, Integer> entry : flagOccurrences.entrySet()) {
                String flag = entry.getKey();
                if (!allowedFlags.contains(flag)) {
                    throw new GrugCommandParserException.InvalidArgument(flag, "unknown flag");
                }
                if (entry.getValue() > 1) {
                    throw new GrugCommandParserException.InvalidArgument(
                            flag, "flag must not be specified more than once");
                }
            }
        }
    }

    /**
     * Parses the list of args into the command, inputs and flag values. Flags start
     * with {@code /}, are case-insensitive, and may be escaped with an additional
     * slash when a literal value beginning with a slash is required.
     *
     * e.g.
     *
     * <pre>{@code
     * parseArgs(new String[] { "cmd", "in1", "in2", "/flag1", "value1", "/flag2", "value21", "value22" })
     * }</pre>
     *
     * is parsed into
     *
     * <pre>{@code
     * ParsedArgs(
     *     cmd,
     *     ["in1", "in2"],
     *     HashMap {
     *         "flag1": ["value1"],
     *         "flag2": ["value21", "value22"]
     *     }
     * );
     * }</pre>
     *
     * Also, command, inputs, and flagValues are guaranteed to be non-null. However,
     * {@code flagValues.get("/flag")} is guaranteed to be null if the flag did not
     * appear in {@code args[]}, and guaranteed to be an empty list if the flag
     * appeared without declaring its values, e.g. {@code cmd something /flag}
     *
     * @param args The args to parse.
     * @return {@link ParsedArgs} The command, inputs and flag values.
     */
    static ParsedArgs parseArgs(String[] args) {
        if (args.length == 0) {
            return new ParsedArgs("", new ArrayList<>(0), new HashMap<>(0), new HashMap<>(0));
        }

        String command = args[0];
        List<String> inputs = new ArrayList<>();
        HashMap<String, List<String>> flagValues = new HashMap<>();
        HashMap<String, Integer> flagOccurrences = new HashMap<>();

        if (args.length == 1) {
            return new ParsedArgs(command, inputs, flagValues, flagOccurrences);
        }

        String currentFlag = null;

        // since args[0] is the command, start from args[1] onwards
        for (int i = 1; i < args.length; i++) {
            String token = args[i];

            boolean isEscapedSlash = token.startsWith("//");
            boolean isFlag = token.startsWith("/") && !isEscapedSlash;
            if (isFlag) {
                // our token is a flag, add subsequent tokens to the flag's values
                currentFlag = token.toLowerCase();
                flagValues.putIfAbsent(currentFlag, new ArrayList<>());
                flagOccurrences.merge(currentFlag, 1, Integer::sum);
                continue;
            }

            if (isEscapedSlash) {
                token = token.substring(1);
            }

            if (currentFlag == null) {
                inputs.add(token);
            } else {
                flagValues.get(currentFlag).add(token);
            }
        }

        return new ParsedArgs(command, inputs, flagValues, flagOccurrences);
    }

    /**
     * Retrives the value of the {@code /priority} flag passed in and parses it into
     * the appropriate {@link TaskPriority} variant.
     *
     * @param parsedArgs The parsed arguments from the user input.
     * @param default    The default priority if no {@code /priority}
     *                   was given.
     * @return The appropriate {@link TaskPriority} variant based on the
     *         {@code /priority} flag's value if it was given,
     *         {@code default} if no {@code /priority} was given.
     * @throws GrugCommandParserException If the number of non-empty
     *                                    {@code /priority} flags given is not
     *                                    exactly 1, or the priority is invalid.
     */
    static TaskPriority parseTaskPriorityFrom(ParsedArgs parsedArgs, TaskPriority defaultPriority)
            throws GrugCommandParserException {
        List<String> priorities = parsedArgs.flagValues().get("/priority");
        if (priorities == null) {
            return defaultPriority;
        }

        String priorityFlagOptions = getPriorityFlagOptions(" | ");

        if (priorities.isEmpty()) {
            String reason = "No priority specified. priority must be one of " + priorityFlagOptions;
            throw new GrugCommandParserException.InvalidArgument("/priority", reason);
        }

        if (priorities.size() != 1) {
            throw new GrugCommandParserException.InvalidArgument(
                    "/priority", "A task can only have 1 priority");
        }

        String priorityName = priorities.get(0);

        try {
            return TaskPriority.createFromDisplayName(priorityName);
        } catch (IllegalArgumentException e) {
            String reason = "priority must be one of " + priorityFlagOptions;
            throw new GrugCommandParserException.InvalidArgument("/priority", reason);
        }
    }

    static String getPriorityFlagUsage() {
        return "/priority " + getPriorityFlagOptions(" | ");
    }

    static String getPriorityFlagOptions(String delimiter) {
        return String.join(delimiter, TaskPriority.getValidPriorityNames());
    }

    /**
     * Parses raw user input and maps it to the appropriate {@link GrugCommand}
     * variant.
     *
     * @param input the raw string input from the user's terminal.
     * @return the corresponding command variant.
     * @throws GrugCommandParserException if there was an error parsing the input.
     */
    public static GrugCommand parseInput(String input) throws GrugCommandParserException {
        input = input.strip();

        // no command given, e.g. user just presses enter without inputting anything, or
        // inputting whitespace
        if (input.isEmpty()) {
            return new EmptyCommand();
        }

        // input must have had a non-whitespace character, so splitting it must give an
        // array with at least length 1, so args[0] will not throw
        String[] args = input.split("\\s+");
        assert args.length > 0;
        String commandString = args[0].toLowerCase();

        return switch (commandString) {
            case "bye" -> QuitCommandParser.parseQuitCommand(args);
            case "list" -> ListCommandParser.parseListCommand(args);
            case "mark" -> TaskCompletionCommandParser.parseMarkCommand(args);
            case "unmark" -> TaskCompletionCommandParser.parseUnmarkCommand(args);
            case "todo" -> AddTaskCommandParser.parseTodoCommand(args);
            case "deadline" -> AddTaskCommandParser.parseDeadlineCommand(args);
            case "event" -> AddTaskCommandParser.parseEventCommand(args);
            case "delete" -> DeleteCommandParser.parseDeleteCommand(args);
            case "find-on" -> FindCommandParser.parseFindByDateCommand(args);
            case "find" -> FindCommandParser.parseFindByDetailsCommand(args);
            case "set-priority" -> SetPriorityCommandParser.parseSetPriorityCommand(args);
            default -> throw new GrugCommandParserException.UnknownCommand(commandString);
        };
    }

    /**
     * Requires {@code args} to be non-empty and throws if it is empty.
     *
     * @param args The argument list which should be non-empty.
     * @throws IllegalArgumentException If {@code args} is empty.
     */
    static void requireNonEmptyArgs(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("`args[]` must be non-empty.");
        }
    }

    /**
     * Converts the {@code text} into an integer.
     *
     * @param text           The text to convert into an integer.
     * @param erraticCommand The command that we are passing the integer to.
     * @param errorReason    The reason for if there is a parsing error.
     * @return The parsed integer.
     * @throws GrugCommandParserException.InvalidArgument Containing the
     *                                                    {@code erraticCommand} and
     *                                                    {@code errorReason} if we
     *                                                    could not parse the
     *                                                    integer
     */
    static int parseInt(String text, String erraticCommand, String errorReason)
            throws GrugCommandParserException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new GrugCommandParserException.InvalidArgument(erraticCommand, errorReason);
        }
    }
}
