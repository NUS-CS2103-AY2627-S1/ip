package kpei.commands.flags;

import kpei.commands.parser.ParsedCommand;
import kpei.exceptions.MissingArgumentException;

import java.util.List;

public class IntFlag extends Flag<Integer> {

    public IntFlag(String argument, int position, List<String> aliases, String helpDescription) {
        super(argument, position, aliases, helpDescription);
    }

    public IntFlag(String argument, int position, List<String> aliases, Integer defaultValue,
                   String helpDescription) {
        super(argument, position, aliases, defaultValue, helpDescription);
    }

    @Override
    public Integer parse(ParsedCommand parsedCommand) throws MissingArgumentException {
        try {
            String value = getValue(parsedCommand);
            assert !value.isBlank() : "value of argument should be initialized";
            return Integer.valueOf(value);
        } catch (MissingArgumentException e) {
            if (isRequired) {
                throw e;
            }
            return defaultValue;
        }
    }
}
