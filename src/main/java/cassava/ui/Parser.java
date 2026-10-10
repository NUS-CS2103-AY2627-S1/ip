package cassava.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Parses user input, extracting the commands, parameters,
 * and their respective input arguments.
 */
public class Parser {

    public static final List<String> VALID_CMDS = new ArrayList<String>(
            Arrays.asList(
                    "list",
                    "find",
                    "todo",
                    "deadline",
                    "event",
                    "mark",
                    "unmark",
                    "prioritise",
                    "delete",
                    "bye"
            )
    );

    /**
     * Parses user input, extracting the command, parameters,
     * and their respective input arguments into a HashMap.
     * @param input raw user input as a String.
     * @return HashMap storing the parameters and respective arguments as key-value pairs.
     *         The command is stored in the "command" key.
     */
    public static HashMap<String, String> parseUserInput(String input) {
        HashMap<String, String> args = new HashMap<String, String>();
        String[] tokens = input.split("\s");

        args.put("command", tokens[0]);

        String param = tokens[0];
        StringBuilder option = new StringBuilder();
        for (int i = 1; i < tokens.length; ++i) {
            if (tokens[i].charAt(0) == '\\') {
                args.put(param, option.toString());
                param = tokens[i].substring(1);
                option = new StringBuilder();
            } else {
                option.append(option.isEmpty() ? tokens[i] : " " + tokens[i]);
            }
        }
        args.put(param, option.toString());

        return args;
    }

}
