package kpei.commands.flags;

import kpei.commands.parser.ParsedCommand;
import kpei.exceptions.BertException;
import kpei.exceptions.MissingArgumentException;

import java.util.List;

public abstract class Flag<T> {
    private static final int HELP_ALIAS_WIDTH = 30;

    protected final String argument;
    protected final int position;
    protected List<String> aliases;
    protected final boolean isRequired;
    protected final T defaultValue;
    protected final String helpDescription;

    public Flag(String argument, int position, List<String> aliases, String helpDescription) {
        this(argument, position, aliases, true, null, helpDescription);
    }

    public Flag(String argument, int position, List<String> aliases, T defaultValue, String helpDescription) {
        this(argument, position, aliases, false, defaultValue, helpDescription);
    }

    private Flag(String argument, int position, List<String> aliases, boolean isRequired, T defaultValue,
                 String helpDescription) {
        this.argument = argument;
        this.position = position;
        this.aliases = aliases;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.helpDescription = helpDescription;
    }

    public String getValue(ParsedCommand parsedCommand) throws MissingArgumentException {
        String value = null;
        if (position < parsedCommand.getArguments().size()) {
            value = parsedCommand.getArguments().get(position);
        } else {
            for (String alias : aliases) {
                value = parsedCommand.getFlags().get(alias);
                if (value != null) {
                    break;
                }
            }
        }

        if (value == null) {
            throw new MissingArgumentException(argument);
        }

        return value;
    }

    public abstract T parse(ParsedCommand parsedCommand) throws BertException;

    public String formatArgument() {
        if (isRequired) {
            return String.format("<%s>", argument);
        }
        return String.format("[<%s>]", argument);
    }

    public String getHelp() {
        String formattedAliases = String.join(" | ", aliases);
        return String.format("   %-" + HELP_ALIAS_WIDTH + "s%s", formattedAliases, helpDescription);
    }
}
