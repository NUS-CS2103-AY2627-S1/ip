package kpei.commands.user;

import kpei.commands.parser.ParsedCommand;
import kpei.exceptions.BertException;

import java.util.List;

public class ListCommand extends Command<Boolean> {

    public ListCommand() {
        commandType = "list";
        aliases = List.of("ls", "list", "dir");
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
