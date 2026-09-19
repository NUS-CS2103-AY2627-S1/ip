package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.model.NyonException;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    public static final String COMMAND_KEY = "command";
    public static final String DESCRIPTION_KEY = "description";

    protected final HashMap<String, String> arguments;

    public Command() {
        this.arguments = new HashMap<>();
    }

    public Command(HashMap<String, String> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    /**
     * Executes this command
     *
     * @return <code>Result</code> containing output and application control info
     * @throws NyonException if command input is invalid or otherwise raises issues
     */
    public abstract Result execute() throws NyonException;

}
