package arthur.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arthur.exceptions.MissingArgumentException;
import arthur.messages.Message;
import arthur.state.CommandContext;

/**
 * Represents an operation requested by the user.
 * It has one main function: the {@link Command#handle(CommandContext context, String arg)} method.
 */
public abstract class Command {
    // AI-assisted: Named parsing literals and exposed the key for unprefixed arguments to command classes.
    protected static final String UNNAMED_ARGUMENT = "";

    private static final String ARGUMENT_SEPARATOR = " ";
    /**
     * Handles a command.
     *
     * @param context the shared command state.
     * @param arg the extra command arguments.
     * @return the response to display.
     */
    public abstract Message handle(CommandContext context, String arg);

    /**
     * Parses the arguments sent by the user into a map.
     *
     * @param arg the extra arguments sent by the user. For example, if the user sends "hello world john",
     *            arg will be "world john".
     * @param argNames the names of the arguments to parse (for example, "/world" and "/john").
     * @return a map of the arguments.
     */
    public Map<String, String> parseArgs(String arg, String... argNames) {
        String[] splits = arg.split(ARGUMENT_SEPARATOR);
        List<String> argNamesList = Arrays.asList(argNames);

        Map<String, String> map = new HashMap<>();
        StringBuilder currentArg = new StringBuilder();
        String currentArgName = UNNAMED_ARGUMENT;
        for (String split : splits) {
            if (argNamesList.contains(split)) {
                // Trim off the argument
                if (!currentArg.isEmpty()) {
                    currentArg.deleteCharAt(currentArg.length() - ARGUMENT_SEPARATOR.length());
                }
                map.put(currentArgName, currentArg.toString());
                currentArgName = split;
                currentArg = new StringBuilder();
                continue;
            }
            currentArg.append(split).append(ARGUMENT_SEPARATOR);
        }
        if (!currentArg.isEmpty()) {
            currentArg.deleteCharAt(currentArg.length() - ARGUMENT_SEPARATOR.length());
        }
        map.put(currentArgName, currentArg.toString());
        for (String argName : argNames) {
            if (!map.containsKey(argName)) {
                throw new MissingArgumentException(CommandRegistry.getCommandName(this.getClass()), argName);
            }
        }
        return map;
    }
}
