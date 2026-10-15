package kpei.commands.user;

import kpei.commands.parser.ParsedCommand;
import kpei.exceptions.BertException;

import java.util.List;

public class ExitCommand extends Command<Boolean> {

    public ExitCommand() {
        commandType = "exit";
        aliases = List.of("q", "quit", "exit", "bye");
        maxArgs = 0;
    }

    public Boolean parse(ParsedCommand parsedCommand) throws BertException {
        checkArgCount(parsedCommand);
        return Boolean.TRUE;
    }

    @Override
    public String getSyntax() {
        return String.format("Syntax: %s", commandType);
    }
}
