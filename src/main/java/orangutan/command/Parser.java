package orangutan.command;

import java.util.Arrays;

import orangutan.OrangutanException;

/**
 * Parses commands from user and runs the appropriate commands.
 */
public class Parser {
    private final Context context;

    /**
     * Creates new parser object.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     */
    public Parser(Context context) {
        assert context != null : "Parser says: context should not be null!!!";

        this.context = context;
    }

    /**
     * Breaks user input down into individual parameters.
     *
     * @param input Command supplied by user, guaranteed to be non-empty.
     * @return Array of String Arrays; the first element is the (command, title) pair
     *      and the second element are any date/time parameters.
     *      e.g. { {"event", "eat lunch"} , {"20260831 1800", "20260831 1900"} }
     */
    private String[][] parseCommand(String input) {
        String[] fields = input.split("/"); // {"event eat lunch ", "from 20260831 1800 ", "to 20260831 1900"}
        String[] action = Arrays.stream(fields)
                .limit(1) // ["event eat lunch "]
                .map(String::trim) // ["event eat lunch"]
                .flatMap(s -> Arrays.stream(s.split(" ", 2))) // ["event", "eat lunch"]
                .toArray(String[]::new); // {"event", "eat lunch"}
        String[] params = Arrays.stream(fields)
                .skip(1) // ["from 20260831 1800 ", "to 20260831 1900"]
                .map(String::trim) // ["from 20260831 1800", "to 20260831 1900"]
                .map(s -> Arrays.stream(s.split(" ", 2)))
                // [ ["from", "20260831 1800"] , ["to", "20260831 1900] ]
                .map(s -> s.skip(1)) // [ ["20260831 1800"] , ["20260831 1900"] ]
                .flatMap(s -> s) // ["20260831 1800" , "20260831 1900"]
                .toArray(String[]::new); // {"20260831 1800" , "20260831 1900"}

        return new String[][]{action, params};
    }

    /**
     * Returns action specified by user.
     * @param parsedCommand 2D String array containing action and parameters.
     * @return Requested action, e.g. {"event", "dinner"}.
     */
    private String[] getAction(String[][] parsedCommand) {
        return parsedCommand[0];
    }

    /**
     * Returns parameters specified by user.
     * @param parsedCommand 2D String array containing action and parameters.
     * @return Parameters of the command, e.g. {"20260831 1900"}.
     */
    private String[] getParams(String[][] parsedCommand) {
        return parsedCommand[1];
    }

    /**
     * Returns CommandType from user-specified action.
     * @param action String array denoting the requested action, e.g. {"event", "dinner"}.
     * @return CommandType item corresponding to the command type, e.g. EVENT.
     */
    private CommandType getCommandType(String[] action) {
        return CommandType.valueOf(action[0].toUpperCase());
    }

    /**
     * Parses user input, calls the corresponding command with given parameters, and returns the command output.
     *
     * @param input Command supplied by user.
     * @return Reply after command completion.
     */
    public String runCommand(String input) {
        assert input != null : "Parser says: input should not be null!!!";

        String[][] command = parseCommand(input);
        String[] action = getAction(command); // e.g. {"event", "eat"}
        String[] params = getParams(command); // e.g. { {"from", 20260831 1800"} , {"to", "20260831 1900"} }

        assert action.length > 0 : "Parser says: action should not be empty!!!";

        try {
            CommandType commandType = getCommandType(action);
            return commandType.checkAndRun(action, params, context);
        } catch (OrangutanException e) {
            return (e.toString());
        } catch (IllegalArgumentException e) {
            return CommandError.unknownCommandError().toString();
        }
    }
}
