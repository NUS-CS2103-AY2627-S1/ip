package kpei.commands.user;

import kpei.commands.parser.ParsedCommand;
import kpei.exceptions.BertException;

import java.util.List;

public abstract class Command<T> {
    protected String commandType;
    protected int maxArgs;
    protected List<String> aliases;

    public abstract T parse(ParsedCommand parsedCommand) throws BertException;

    public boolean isMatch(ParsedCommand parsedCommand) {
        return aliases.contains(parsedCommand.getCommandType());
    }

    public void checkArgCount(ParsedCommand parsedCommand) throws BertException {
        int totalargs = parsedCommand.getArguments().size() + parsedCommand.getFlags().size();
        if (totalargs > maxArgs) {
            throw new BertException(String.format("'%s' command does not take more than %d arguments.", commandType, maxArgs));
        }
    }

    public abstract String getSyntax();
}
