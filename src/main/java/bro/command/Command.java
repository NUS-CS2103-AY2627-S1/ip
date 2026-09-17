package bro.command;

import java.util.Objects;

/** Represents a user command after its action word and arguments are separated. */
public final class Command {
    private final String action;
    private final String arguments;

    /** Creates a command with its normalized action and remaining arguments. */
    public Command(String action, String arguments) {
        this.action = Objects.requireNonNull(action);
        this.arguments = Objects.requireNonNull(arguments).trim();
    }

    /** Returns the command action word. */
    public String getAction() {
        return action;
    }

    /** Returns the command arguments, or an empty string when none were supplied. */
    public String getArguments() {
        return arguments;
    }

    /** Returns whether the command contains at least one non-whitespace argument. */
    public boolean hasArguments() {
        return !arguments.isBlank();
    }
}
