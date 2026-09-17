package botavius.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import botavius.exception.BotaviusException;
import botavius.tasklist.TaskList;

/** Parses user input into command names and command parameters. */
public class Parser {

    /** Creates a parser. */
    public Parser() {
        ;
    }
    /**
     * Extracts slash-prefixed named parameters from a command string.
     *
     * @param command command containing an optional task and named values
     * @return map of parameter names to their trimmed values
     */
    public static Map<String, String> getNamedParameters(String command) {
        Map<String, String> parametersByName = new HashMap<>();
        Pattern pattern = Pattern.compile("(/[a-zA-Z0-9]+)");
        Matcher matcher = pattern.matcher(command);

        String currentKey = "/task";
        int lastMatchEnd = 0;

        while (matcher.find()) {
            // If not null, have valid key to pair
            if (currentKey != null) {
                String value = command.substring(lastMatchEnd, matcher.start()).trim();
                parametersByName.put(currentKey, value);
            }

            currentKey = matcher.group(0); // Set key to "\XXX"
            lastMatchEnd = matcher.end();
        }

        if (currentKey != null) {
            String value = command.substring(lastMatchEnd).trim();
            parametersByName.put(currentKey, value);
        }
        //only for debugging
        //parametersByName.forEach((key, value) -> System.out.println(key + " => " + value));
        return parametersByName;
    }

    /**
     * Splits a command into whitespace-separated words.
     *
     * @param command command text to split on whitespace
     * @return the whitespace-separated command words
     */
    public static String[] getUnnamedParameters(String command) {
        return command.split("\\s+");
    }

    /**
     * Dispatches a command to the corresponding operation on a task list.
     *
     * @param command complete command entered by the user
     * @param taskList task list to modify or query
     * @return the operation's message for display
     * @throws BotaviusException if the command name is not supported
     */
    public static String process(String command, TaskList taskList) {
        Map<String, String> namedParameters = getNamedParameters(command);
        String[] parameters = getUnnamedParameters(command);
        // A valid command always has a first word, which is the dispatch key below.
        assert parameters.length > 0 : "Command must contain at least one word";
        switch (parameters[0].toLowerCase()) {
            case "list":
                return taskList.listTasks();
            case "find":
                return taskList.find(command.substring(5).strip());
            case "mark":
                return taskList.markTask(parameters);
            case "unmark":
                return taskList.unmarkTask(parameters);
            case "deadline":
                return taskList.deadline(namedParameters);
            case "event":
                return taskList.event(namedParameters);
            case "doafter":
                return taskList.doAfter(namedParameters);
            case "todo":
                return taskList.todo(namedParameters);
            case "delete":
                return taskList.delete(parameters);
            case "bye":
                return "bye"; //exit case
            default:
                throw new BotaviusException("bad command issued.");
        }
    }
}
