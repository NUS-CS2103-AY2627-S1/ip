package echo.command;

import java.util.List;

import echo.EchoException;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents an executable command entered by the user.
 */
public abstract class Command {
    /** Represents a command type supported by E.C.H.O. */
    public enum Type {
        HELP,
        LIST,
        TODO,
        DEADLINE,
        EVENT,
        MARK,
        UNMARK,
        DELETE,
        /** Finds by keyword. */
        FIND,
        /** Sorts the task list. */
        SORT,
        /** Ends the session. */
        BYE
    }

    private final Type type;
    private final List<String> arguments;

    /**
     * Creates a command with already-validated arguments.
     *
     * @param type command type.
     * @param arguments validated command arguments.
     */
    protected Command(Type type, List<String> arguments) {
        this.type = type;
        this.arguments = List.copyOf(arguments);
    }

    /**
     * Executes the command using the application's task service and UI.
     *
     * @param taskManager service used to perform task operations.
     * @param ui interface used to display results.
     * @return response message resulting from command execution.
     * @throws EchoException if the command cannot be completed.
     */
    public abstract String execute(TaskManager taskManager, Ui ui) throws EchoException;

    /**
     * Returns whether executing this command should end the session.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns the command type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns a validated command argument.
     *
     * @param index zero-based argument position.
     * @return argument at that index.
     */
    public String getArgument(int index) {
        return arguments.get(index);
    }
}
