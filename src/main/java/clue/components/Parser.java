package clue.components;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clue.ui.Ui;

/**
 * Parses user commands and arguments and dispatches them to defined handlers.
 */
public class Parser implements Ui.InputHandler {
    /** Command response callback */
    @FunctionalInterface
    public interface Command {
        /**
         * Response to a specific user command.
         *
         * @param ui - Ui object for writing back to the user.
         * @param args - A mapping of /key value arguments.
         *               If nonempty, always contains a {@link DEFAULT_ARGUMENT} key for the argument without any name.
         * @return boolean for whether to continue running. If false, initiates shutdown of the program.
         */
        boolean run(Ui ui, Map<String, String> args);
    }
    /** The key that refers to the default argument in argument mapping
     *  The default argument is the argument that does not have a '/' specifier
     */
    public static final String DEFAULT_ARGUMENT = "";

    /** Handler that is run when the user's command is unrecognized. */
    public static final Command DEFAULT_COMMAND = (ui, _) -> {
        ui.println("uhh... sorry, I don't have a clue :(");
        return true;
    };

    /** Regex to match for named arguments. */
    public static final Pattern ARGUMENT_REGEX = Pattern.compile("/(.+?) ");

    private final HashMap<String, Command> commands;

    public Parser() {
        this.commands = new HashMap<>();
    }

    /**
     * Registers a handler for a given command name.
     *
     * @param name - Name of the command. When the user starts their input with this name, this command is run.
     * @param command - Callback when this command is activated.
     */
    public void register(String name, Command command) {
        this.commands.put(name, command);
    }

    @Override
    public boolean handle(Ui ui, String in) {
        in = in.trim();

        // Handle empty input
        if (in.isEmpty()) {
            ui.println("Please enter a command.");
            return true;
        }

        int idx = in.indexOf(' ');
        String command;
        Map<String, String> args;
        if (idx == -1) {
            // no arguments at all
            command = in;
            args = Map.of();
        } else {
            command = in.substring(0, idx);
            // Validate command doesn't contain invalid characters
            if (!isValidCommandName(command)) {
                ui.println("Invalid command format. "
                        + "Commands should only contain letters, numbers, underscores, and hyphens.");
                return true;
            }

            Matcher matcher = ARGUMENT_REGEX.matcher(in).region(idx, in.length());
            String name = DEFAULT_ARGUMENT;
            args = new HashMap<>();
            while (matcher.find()) {
                String argValue = in.substring(idx, matcher.start()).trim();
                // Check for empty argument value (e.g., "/key /next")
                // ignore if the first arg i.e. the default one is empty
                if (argValue.isEmpty() && !name.isEmpty()) {
                    ui.println("Warning: Argument '" + name + "' has no value. It will be ignored.");
                }
                args.put(name, argValue);

                name = matcher.group(1);

                // Validate argument name
                if (!isValidArgumentName(name)) {
                    ui.println("Warning: Invalid argument name '" + name + "'. "
                            + "Arguments should only contain letters, numbers, underscores, and hyphens.");
                    continue;
                }

                idx = matcher.end();
            }
            args.put(name, in.substring(idx).trim());
        }

        return this.commands.getOrDefault(command, DEFAULT_COMMAND).run(ui, args);
    }

    /**
     * Validates that a command name only contains valid characters.
     */
    private boolean isValidCommandName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9_-]+$");
    }

    /**
     * Validates that an argument name only contains valid characters.
     */
    private boolean isValidArgumentName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9_-]+$");
    }
}
