package org.cs2103t.marquee.cli.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Immutable data class containing details of a command.
 * <p>
 * A command can have an argument and any number of flag-value pairs.
 *
 * @see Code
 */
public class Command {
    private final Code code;
    private final String argument;
    private final Map<String, String> parameters;

    /**
     * Creates a new {@code Command} instance with the given {@code Code} and parameters.
     * The argument is taken from the parameter map at key {@code ""}.
     *
     * @param code the command {@link Code}
     * @param parameters mappings of flag names to parameter values
     * @throws NullPointerException if {@code code} or {@code parameters} is {@code null}
     * @throws NoSuchElementException if an unrecognized flag was given
     */
    public Command(Code code, Map<String, String> parameters)
            throws NullPointerException, NoSuchElementException {
        if (code == null || parameters == null) {
            throw new NullPointerException();
        }
        this.code = code;
        this.parameters = new HashMap<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (!code.getFlagNames().contains(entry.getKey())) {
                throw new NoSuchElementException(entry.getKey());
            }

            this.parameters.put(entry.getKey(), entry.getValue());
        }
        this.argument = this.parameters.remove("");
    }

    /**
     * Creates a new {@code Command} instance with the given {@code Code}, argument and parameters.
     * The empty string {@code ""} key in the parameter map is ignored.
     *
     * @param code the command {@link Code}
     * @param parameters mappings of flag names to parameter values
     * @throws NullPointerException if {@code code} or {@code parameters} is {@code null}
     * @throws NoSuchElementException if an unrecognized flag was given
     */
    public Command(Code code, String argument, Map<String, String> parameters)
            throws NullPointerException, NoSuchElementException {
        if (code == null || parameters == null) {
            throw new NullPointerException();
        }
        this.code = code;
        this.argument = argument;
        this.parameters = new HashMap<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (!code.getFlagNames().contains(entry.getKey())) {
                throw new NoSuchElementException(entry.getKey());
            }
            this.parameters.put(entry.getKey(), entry.getValue());
        }
        this.parameters.remove("");
    }

    /**
     * Gets the {@code Code} of this command.
     *
     * @return the {@code Code} of this command
     */
    public Code getCode() {
        return this.code;
    }

    /**
     * Gets the argument of this command.
     *
     * @return the argument of this command
     */
    public String getArgument() {
        return this.argument;
    }

    /**
     * Gets the value of the flag in this command.
     *
     * @param flagName the name of the flag to get
     * @return the value of the flag
     */
    public String getFlag(String flagName) {
        return this.parameters.get(flagName);
    }

    /**
     * Checks if the command has an argument.
     *
     * @return whether the argument exist or not
     */
    public boolean hasArgument() {
        return this.argument != null;
    }

    /**
     * Checks if the flag is set in this command.
     *
     * @param flagName the name of the flag to check
     * @return whether the flag is set or not
     */
    public boolean hasFlag(String flagName) {
        return this.parameters.containsKey(flagName);
    }

    /**
     * Returns a view of the parameter map.
     * <p>
     * Use this to take advantage of built-in {@link Map} functionalities.
     *
     * @return an unmodifiable view of the parameter map
     */
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Command
                && this.getCode().equals(((Command) obj).getCode())
                && (this.hasArgument()
                ? this.getArgument().equals(((Command) obj).getArgument())
                : !((Command) obj).hasArgument())
                && this.getParameters().equals(((Command) obj).getParameters());
    }
}
