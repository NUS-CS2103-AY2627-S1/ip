package nico;

/**
 * Stores shared command information for commands that accept one parameter.
 */
public abstract class ParameterCommand extends Command {
    private final String commandWord;
    private final String parameter;

    /**
     * Creates a command with its command word and raw parameter.
     *
     * @param commandWord Command word entered by the user.
     * @param parameter Parameter text entered after the command word.
     */
    protected ParameterCommand(String commandWord, String parameter) {
        this.commandWord = commandWord;
        this.parameter = parameter;
    }

    protected String getCommandWord() {
        return commandWord;
    }

    protected String getParameter() {
        return parameter;
    }
}
